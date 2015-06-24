package com.mediafire.sdk.uploader;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.MediaFire;
import com.mediafire.sdk.api.UploadApi;
import com.mediafire.sdk.api.responses.*;
import com.mediafire.sdk.api.responses.data_models.*;
import com.mediafire.sdk.util.HashUtil;

import java.io.*;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;

/**
 * Runnable which handles Web Uploads, Simple Uploads, and Resumable Uploads
 */
public class MediaFireUpload implements Runnable {
    private static final int TIME_BETWEEN_POLLS_MILLIS = 1000 * 5;
    private static final int MAX_POLLS = 24;
    private static final long FOUR_MB = 4000000;
    private static final String UTF8 = "UTF-8";

    private static final String PARAM_RESPONSE_FORMAT = "response_format";
    private static final String PARAM_URL = "url";
    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_FOLDER_KEY = "folder_key";
    private static final String PARAM_FOLDER_PATH = "path";
    private static final String PARAM_RESUMABLE = "resumable";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_HASH = "hash";
    private static final String PARAM_KEY = "key";
    private static final String PARAM_UPLOAD_KEY = "upload_key";
    private static final String PARAM_ALL_WEB_UPLOADS = "all_web_uploads";

    private static final String JSON = "json";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_X_FILENAME = "x-filename";
    private static final String HEADER_X_FILESIZE = "x-filesize";
    private static final String HEADER_X_FILEHASH = "x-filehash";
    private static final String HEADER_X_UNIT_HASH = "x-unit-hash";
    private static final String HEADER_X_UNIT_ID = "x-unit-id";
    private static final String HEADER_X_UNIT_SIZE = "x-unit-size";
    private static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";

    // args passed via constructor
    private final MediaFire mediaFire;
    private final int statusToFinish;
    private File file;
    private ActionOnInAccount actionOnInAccount;
    // values set by class during execution
    private String url;
    private final MediaFireUploadHandler handler;
    private String fileHash;
    private List<Boolean> uploadUnits = new LinkedList<Boolean>();
    // optional values that can be set
    private final String filename;
    private String folderKey;
    private String folderPath;
    private final long id;

    public MediaFireUpload(MediaFire mediaFire, int statusToFinish, File file, String filename, ActionOnInAccount actionOnInAccount, MediaFireUploadHandler handler, long id) {
        this.mediaFire = mediaFire;
        this.statusToFinish = statusToFinish;
        this.file = file;
        this.filename = filename;
        this.actionOnInAccount = actionOnInAccount;
        this.handler = handler;
        this.id = id;
    }

    public MediaFireUpload(MediaFire mediaFire, int statusToFinish, String url, String filename, MediaFireUploadHandler handler, long id) {
        this.mediaFire = mediaFire;
        this.statusToFinish = statusToFinish;
        this.url = url;
        this.filename = filename;
        this.handler = handler;
        this.id = id;
    }

    private void setOptionalFolderKey(String folderKey) {
        this.folderKey = folderKey;
    }

    private void setOptionalFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public void run() {
        try {
            if (this.url != null && !this.url.isEmpty()) {
                    addWebUpload();
            } else if (file != null && file.exists()) {
                if (this.file.length() < FOUR_MB) {
                    simpleUpload();
                } else {
                    checkUpload();
                }
            } else {
                throw new MFException("no url or file was passed to upload");
            }
        } catch (MFException e) {
            if (this.handler != null) {
                this.handler.uploadFailed(this.id, e);
            }
        } catch (MFApiException e) {
            if (this.handler != null) {
                this.handler.uploadFailed(this.id, e);
            }
        } catch (IOException e) {
            if (this.handler != null) {
                this.handler.uploadFailed(this.id, e);
            }
        } catch (InterruptedException e) {
            if (this.handler != null) {
                this.handler.uploadFailed(this.id, e);
            }
        } catch (MFSessionNotStartedException e) {
            if (this.handler != null) {
                this.handler.uploadFailed(this.id, e);
            }
        }
    }

    private void checkUpload() throws IOException, MFException, MFApiException, InterruptedException, MFSessionNotStartedException {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        params.put(PARAM_RESUMABLE, "yes");
        params.put(PARAM_SIZE, this.file.length());
        params.put(PARAM_HASH, getFileHash());
        params.put(PARAM_FILENAME, this.filename);
        if (this.folderKey != null && !this.folderKey.isEmpty()) {
            params.put(PARAM_FOLDER_KEY, this.folderKey);
        }

        if (this.folderPath != null && !this.folderPath.isEmpty()) {
            params.put(PARAM_FOLDER_PATH, this.folderPath);
        }

        UploadCheckResponse response = UploadApi.check(this.mediaFire, params, "1.4", UploadCheckResponse.class);

        String hashExists = response.getHashExists();
        String inAccount = response.getInAccount();
        String inFolder = response.getInFolder();
        String duplicateQuickKey = response.getDuplicateQuickkey();

        if ("yes".equals(inAccount)) {
            switch (actionOnInAccount) {
                case UPLOAD_ALWAYS:
                    instantUpload();
                    break;
                case UPLOAD_IF_NOT_IN_FOLDER:
                    if ("no".equals(inFolder)) {
                        instantUpload();
                    } else {
                        uploadFinished(duplicateQuickKey);
                    }
                    break;
                case DO_NOT_UPLOAD:
                default:
                    uploadFinished(duplicateQuickKey);
                    break;
            }
            return;
        }

        if ("yes".equals(hashExists)) {
            instantUpload();
        } else {
            ResumableUpload resumableUpload = response.getResumableUpload();
            resumableUpload(resumableUpload);
        }
    }

    private void instantUpload() throws MFException, MFApiException, IOException, MFSessionNotStartedException {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        params.put(PARAM_SIZE, this.file.length());
        params.put(PARAM_HASH, getFileHash());
        params.put(PARAM_FILENAME, URLEncoder.encode(filename, UTF8));
        if (this.folderKey != null && !this.folderKey.isEmpty()) {
            params.put(PARAM_FOLDER_KEY, folderKey);
        }

        if (this.folderPath != null && !this.folderPath.isEmpty()) {
            params.put(PARAM_FOLDER_PATH, this.folderPath);
        }

        UploadInstantResponse response = UploadApi.instant(this.mediaFire, params, "1.4", UploadInstantResponse.class);
        String quickKey = response.getQuickKey();
        String fileName = response.getFileName();
        uploadFinished(quickKey, fileName);
    }

    private void resumableUpload(ResumableUpload resumableUpload) throws MFException, MFApiException, IOException, InterruptedException, MFSessionNotStartedException {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        if (this.folderKey != null && !this.folderKey.isEmpty()) {
            params.put(PARAM_FOLDER_KEY, folderKey);
        }

        if (this.folderPath != null && !this.folderPath.isEmpty()) {
            params.put(PARAM_FOLDER_PATH, this.folderPath);
        }

        // base headers that don't change
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(HEADER_X_FILESIZE, file.length());
        headers.put(HEADER_X_FILEHASH, getFileHash());
        headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_OCTET_STREAM);
        headers.put(HEADER_X_FILENAME, URLEncoder.encode(this.filename, UTF8));


        int numUnits = resumableUpload.getNumberOfUnits();
        int unitSize = resumableUpload.getUnitSize();

        for (int chunkNumber = 0; chunkNumber < numUnits; chunkNumber++) {
            if (isChunkUploaded(chunkNumber)) {
                continue;
            }

            int chunkSize = getChunkSize(chunkNumber, numUnits, this.file.length(), unitSize);
            byte[] chunk = makeChunk(unitSize, chunkNumber);
            String chunkHash = HashUtil.sha256(chunk);

            headers.put(HEADER_X_UNIT_ID, chunkNumber);
            headers.put(HEADER_X_UNIT_SIZE, chunkSize);
            headers.put(HEADER_X_UNIT_HASH, chunkHash);
            UploadResumableResponse response = UploadApi.resumable(this.mediaFire, params, headers, chunk, "1.4", UploadResumableResponse.class);
            ResumableDoUpload doUpload = response.getDoUpload();
            ResumableUpload newResumableUpload = response.getResumableUpload();
            String allUnitsReady = newResumableUpload.getAllUnitsReady();

            if (allUnitsReady != null && "yes".equals(allUnitsReady) && doUpload != null) {
                String uploadKey = doUpload.getKey();
                pollUpload(uploadKey);
                return;
            }

            ResumableBitmap bitmap = resumableUpload.getBitmap();
            if (bitmap != null) {
                int count = bitmap.getCount();
                List<Integer> words = bitmap.getWords();

                updateUploadBitmap(count, words);
            }

            int numUploaded = 0;
            for (int chunkCount = 0; chunkCount < numUnits; chunkCount++) {
                if (isChunkUploaded(chunkCount)) {
                    numUploaded++;
                }
            }

            double percentFinished = (double) numUploaded / (double) numUnits;
            percentFinished *= 100;
            uploadProgress(percentFinished);
        }
    }

    private void pollUpload(String uploadKey) throws MFException, MFApiException, InterruptedException, MFSessionNotStartedException {
        final LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        params.put(PARAM_KEY, uploadKey);
        long pollCount = 0;
        do {
            UploadPollUploadResponse response = UploadApi.pollUpload(this.mediaFire, params, "1.4", UploadPollUploadResponse.class);
            PollDoUpload doUpload = response.getDoUpload();

            int fileErrorCode = doUpload.getFileErrorCode();
            int resultCode = doUpload.getResultCode();
            int statusCode = doUpload.getStatusCode();

            String description = doUpload.getDescription();

            String quickKey = doUpload.getQuickKey();
            String filename = doUpload.getFilename();

            if (quickKey != null && !quickKey.isEmpty()) {
                uploadFinished(quickKey);
                return;
            }

            if (statusCode >= statusToFinish) {
                uploadFinished(quickKey, filename);
                return;
            }

            if (fileErrorCode != 0) {
                throw new MFApiException(fileErrorCode, "file error code " + fileErrorCode + " while polling");
            }

            if (resultCode != 0) {
                throw new MFApiException(fileErrorCode, "resultCode code " + resultCode + " while polling");
            }

            if (handler != null) {
                handler.uploadPolling(id, statusCode, description);
            }
            Thread.sleep(TIME_BETWEEN_POLLS_MILLIS);
            pollCount++;
        } while (pollCount <= MAX_POLLS);
    }

    private void getWebUploads(String uploadKey) throws MFException, MFApiException, InterruptedException, MFSessionNotStartedException {
        final LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        params.put(PARAM_UPLOAD_KEY, uploadKey);
        params.put(PARAM_ALL_WEB_UPLOADS, "yes");
        long pollCount = 0;
        do {
            UploadGetWebUploadsResponse response = UploadApi.getWebUploads(this.mediaFire, params, "1.4", UploadGetWebUploadsResponse.class);
            WebUploads[] webUploadsArray = response.getWebUploads();

            if (webUploadsArray == null || webUploadsArray.length == 0) {
                pollCount++;
                continue;
            }

            WebUploads webUpload = webUploadsArray[0];

            int statusCode = webUpload.getStatusCode();
            int errorStatus = webUpload.getErrorStatus();
            String description = webUpload.getStatus();
            String quickKey = webUpload.getQuickKey();
            String filename = webUpload.getFilename();
            if (quickKey != null && !quickKey.isEmpty()) {
                uploadFinished(quickKey);
                return;
            }

            if (statusCode >= statusToFinish) {
                uploadFinished(quickKey, filename);
                return;
            }

            if (errorStatus != 0) {
                throw new MFApiException(errorStatus, "error status " + errorStatus + " while polling upload/get_web_uploads");
            }

            if (handler != null) {
                handler.uploadPolling(id, statusCode, description);
            }
            Thread.sleep(TIME_BETWEEN_POLLS_MILLIS);
            pollCount++;
        } while (pollCount <= MAX_POLLS);
    }

    private void simpleUpload() throws MFException, MFApiException, IOException, InterruptedException, MFSessionNotStartedException {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        if (this.folderKey != null && !this.folderKey.isEmpty()) {
            params.put(PARAM_FOLDER_KEY, folderKey);
        }

        if (this.folderPath != null && !this.folderPath.isEmpty()) {
            params.put(PARAM_FOLDER_PATH, this.folderPath);
        }

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(HEADER_X_FILENAME, filename);
        headers.put(HEADER_X_FILESIZE, file.length());
        headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_OCTET_STREAM);
        headers.put(HEADER_CONTENT_LENGTH, file.length());

        byte[] payload = getFileBytes(file);
        UploadSimpleResponse response = UploadApi.simple(this.mediaFire, params, headers, payload, "1.4", UploadSimpleResponse.class);
        String uploadKey = response.getDoUpload().getUploadKey();
        pollUpload(uploadKey);
    }

    private void addWebUpload() throws MFException, MFApiException, InterruptedException, UnsupportedEncodingException, MFSessionNotStartedException {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_RESPONSE_FORMAT, JSON);
        params.put(PARAM_URL, this.url);
        params.put(PARAM_FILENAME, URLEncoder.encode(this.filename, UTF8));
        if (this.folderKey != null && !this.folderKey.isEmpty()) {
            params.put(PARAM_FOLDER_KEY, this.folderKey);
        }
        UploadAddWebUploadResponse response = UploadApi.addWebUpload(this.mediaFire, params, "1.4", UploadAddWebUploadResponse.class);
        String uploadKey = response.getUploadKey();
        getWebUploads(uploadKey);
    }

    private void uploadFinished() {
        uploadFinished(null);
    }

    private void uploadFinished(String quickKey) {
        uploadFinished(quickKey, null);
    }

    private void uploadFinished(String quickKey, String fileName) {
        if (this.handler != null) {
            this.handler.uploadFinished(this.id, quickKey, fileName);
        }
    }

    private void uploadProgress(double percentFinished) {
        if (this.handler != null) {
            this.handler.uploadProgress(this.id, percentFinished);
        }
    }

    private String getFileHash() throws IOException {
        if (this.fileHash == null) {
            this.fileHash = HashUtil.sha256(file);
        }

        return this.fileHash;
    }

    private byte[] makeChunk(int unitSize, int chunkNumber) throws IOException {
        FileInputStream fis = new FileInputStream(this.file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] uploadChunk = createUploadChunk(unitSize, chunkNumber, bis);
        fis.close();
        bis.close();
        return uploadChunk;
    }

    private int getChunkSize(int chunkNumber, int numChunks, long fileSize, int unitSize) {
        int chunkSize;
        if (chunkNumber >= numChunks) {
            chunkSize = 0; // represents bad size
        } else {
            if (fileSize % unitSize == 0) { // all units will be of unitSize
                chunkSize = unitSize;
            } else if (chunkNumber < numChunks - 1) { // this unit is of unitSize
                chunkSize = unitSize;
            } else { // this unit is "special" and is the modulo of fileSize and unitSize;
                chunkSize = (int) (fileSize % unitSize);
            }
        }

        return chunkSize;
    }

    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        int offset = (int) (unitSize * chunkNumber);
        fileStream.skip(offset);

        ByteArrayOutputStream output = new ByteArrayOutputStream( (int) unitSize);
        int bufferSize = 65536;

        byte[] buffer = new byte[bufferSize];
        int readSize;
        int t = 0;

        while ((readSize = fileStream.read(buffer)) > 0 && t <= unitSize) {
            if (output.size() + readSize > unitSize) {
                int actualReadSize = (int) unitSize - output.size();
                output.write(buffer, 0, actualReadSize);
            } else {
                output.write(buffer, 0, readSize);
            }

            if (readSize > 0) {
                t += readSize;
            }
        }

        return output.toByteArray();
    }

    private boolean isChunkUploaded(int chunkId) {
        if (this.uploadUnits.isEmpty()) {
            return false;
        }
        return this.uploadUnits.get(chunkId);
    }

    private void updateUploadBitmap(int count, List<Integer> words) {
        List<Boolean> uploadUnits = new LinkedList<Boolean>();

        if (words == null || words.isEmpty()) {
            this.uploadUnits = uploadUnits;
            return;
        }

        //loop count times
        for (int i = 0; i < count; i++) {
            //convert words to binary string
            String word = Integer.toBinaryString(words.get(i));

            //ensure number is 16 bit by adding 0 until there are 16 bits
            while (word.length() < 16) {
                word = "0" + word;
            }

            //add boolean to collection depending on bit value
            for (int j = 0; j < word.length(); j++) {
                uploadUnits.add(i * 16 + j, word.charAt(15 - j) == '1');
            }
        }

        this.uploadUnits = uploadUnits;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        byte[] fileBytes = new byte[(int) file.length()];
        //convert file into array of bytes
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileBytes);
        fileInputStream.close();
        return fileBytes;
    }

    public enum ActionOnInAccount {
        UPLOAD_IF_NOT_IN_FOLDER, DO_NOT_UPLOAD, UPLOAD_ALWAYS,
    }

    @Override
    public String toString() {
        return "MediaFireUpload{" +
                "id=" + id +
                ", file=" + file +
                ", url='" + url + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", filename='" + filename + '\'' +
                ", folderKey='" + folderKey + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", actionOnInAccount=" + actionOnInAccount +
                ", statusToFinish=" + statusToFinish +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaFireUpload that = (MediaFireUpload) o;

        return this.id == that.id && this.actionOnInAccount == that.actionOnInAccount
                && !(this.file != null ? !file.equals(that.file) : that.file != null)
                && !(this.fileHash != null ? !fileHash.equals(that.fileHash) : that.fileHash != null)
                && !(this.url != null ? !url.equals(that.url) : that.url != null);

    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (actionOnInAccount != null ? actionOnInAccount.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (fileHash != null ? fileHash.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
