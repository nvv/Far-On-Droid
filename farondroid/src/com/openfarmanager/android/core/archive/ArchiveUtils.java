package com.openfarmanager.android.core.archive;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.github.junrar.Archive;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.exeptions.CreateArchiveException;
import com.openfarmanager.android.utils.FileUtilsExt;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;

import static com.openfarmanager.android.core.archive.MimeTypes.*;

public class ArchiveUtils {

    private static final String TAG = "ArchiveUtils";

    public enum ArchiveType {
        zip, tar, ar, jar, cpio, rar, x7z;

        public static ArchiveType getType(String mime) {

            if (MIME_APPLICATION_ZIP.equals(mime)) {
                return zip;
            } else if (MIME_APPLICATION_X_TAR.equals(mime)) {
                return tar;
            } else if (MIME_APPLICATION_X_AR.equals(mime)) {
                return ar;
            } else if (MIME_APPLICATION_JAVA_ARCHIVE.equals(mime)) {
                return jar;
            } else if (MIME_APPLICATION_X_CPIO.equals(mime)) {
                return cpio;
            } else if (MIME_APPLICATION_X_RAR_COMPRESSED.equals(mime)) {
                return rar;
            } else if (MIME_APPLICATION_7Z.equals(mime)) {
                return x7z;
            }

            return null;
        }

    }

    public enum CompressionEnum {
        gzip, bzip2;

        public static CompressionEnum getCompression(String mime) {

            if (mime.equals(MimeTypes.MIME_APPLICATION_X_GZIP) || mime.equals(MimeTypes.MIME_APPLICATION_TGZ)) {
                return gzip;
            } else if (mime.equals(MimeTypes.MIME_APPLICATION_X_XZ)) {
                //return xz;
            } else if (mime.equals(MimeTypes.MIME_APPLICATION_X_BZIP2)) {
                return bzip2;
            } else if (mime.equals(MimeTypes.MIME_APPLICATION_X_PACK200)) {
                //return pack200;
            }

            return null;
        }

        public static String toString(CompressionEnum type) {
            switch (type) {
                case gzip: return CompressorStreamFactory.GZIP;
                //case xz: return CompressorStreamFactory.XZ;
                case bzip2: return CompressorStreamFactory.BZIP2;
                //case pack200: return CompressorStreamFactory.PACK200;
                default: return "";
            }
        }

    }

    public static String getMimeType(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return MimeTypes.getMimeType(extension);
    }

    public static boolean isCompressionSupported(String mime) {
        return CompressionEnum.getCompression(mime) != null;
    }

    public static boolean isCompressionSupported(File file) {
        return CompressionEnum.getCompression(getMimeType(file)) != null;
    }

    public static boolean isArchiveSupported(String mime) {
        return ArchiveType.getType(mime) != null;
    }

    public static boolean isArchiveSupported(File file) {
        return ArchiveType.getType(getMimeType(file)) != null;
    }

    public static boolean isRarArchive(File file) {
        return ArchiveType.getType(getMimeType(file)) == ArchiveType.rar;
    }

    public static boolean is7zArchive(File file) {
        return ArchiveType.getType(getMimeType(file)) == ArchiveType.x7z;
    }

    public static boolean isZipArchive(File file) {
        return ArchiveType.getType(getMimeType(file)) == ArchiveType.zip;
    }

    public static ArchiveInputStream createInputStream(InputStream stream) {
        try {
            return new ArchiveStreamFactory()
                    .createArchiveInputStream(new BufferedInputStream(stream));
        } catch (ArchiveException e) {
            return null;
        }
    }

    public static boolean isArchiveFile(File file) {
        return isArchiveSupported(file) || isCompressionSupported(file);
    }

    /**
     * Extract archive files, which represented by <code>extractFileTree</code> -
     * top node of archive files, which must be extracted. Note, that it's not required to extract files
     * from root note - we can specify certain files by extractFileTree.
     *
     * How it works: while iteration through every archive entry in <code>inputFile</code>
     * we get stream of every required for extraction item (from <code>extractFileTree</code>) and redirect it to output.
     *
     * @param inputFile input archive file (which files need to be extracted).
     * @param outputDir directory where files need to be copied.
     * @param extractFileTree tree representation of files, which need to be extracted.
     * @param isCompressed is original archive compressed by additional compression (such as gzip or xz).
     * @param encryptedZipPassword special case for encrypted zip files.
     *                             If param provided - try to use password for zip encryption.
     *                             Should be null for not encrypted archives.
     * @param listener listener to notify upper layer about key events while extract operation.
     *
     * @throws IOException in case of problem with IO operations.
     * @throws ArchiveException in case of problem with  archive operation.
     * @throws FileNotFoundException in case when output directory(ies) for extracted files doesn't exist and can't be created.
     * @throws CompressorException in case when archive is compressed and some errors took place during creation of input stream.
     */
    public static void extractArchive(final File inputFile, final File outputDir,
                                      ArchiveScanner.File extractFileTree, boolean isCompressed, String encryptedZipPassword, ExtractArchiveListener listener)
            throws IOException, ArchiveException, CompressorException {

        // if output directory doesn't exist and can't be created
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new FileNotFoundException(App.sInstance.getResources().getString(R.string.error_output_directory_doesnt_exists));
        }

        if (encryptedZipPassword != null && isZipArchive(inputFile)) {

            try {
                net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(inputFile);

                if (listener != null) {
                    listener.beforeExtractStarted(zipFile.getFileHeaders().size());
                }

                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(encryptedZipPassword);
                }

                // Get the list of file headers from the zip file
                List fileHeaderList = zipFile.getFileHeaders();

                // Loop through the file headers
                for (Object aFileHeaderList : fileHeaderList) {
                    FileHeader fileHeader = (FileHeader) aFileHeaderList;

                    // try to find current archive entry in list of files to extraction
                    ArchiveScanner.File file = extractFileTree.findFile(fileHeader.getFileName());
                    // if current entry shouldn't be extracted - goto next
                    if (file == null) {
                        continue;
                    }

                    // Extract the file to the specified destination
                    zipFile.extractFile(fileHeader, outputDir.getAbsolutePath());

                    if (listener != null) {
                        listener.onFileExtracted(null);
                    }
                }

            } catch (Exception e) {
                throw new ArchiveException("error extracting encrypted zip");
            }

            return;
        }

        if (isRarArchive(inputFile)) {
            Archive arch;
            try {
                arch = new Archive(inputFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (listener != null) {
                listener.beforeExtractStarted(arch.getFileHeaders().size());
            }

            List<com.github.junrar.rarfile.FileHeader> fileHeaderList = arch.getFileHeaders();

            for (com.github.junrar.rarfile.FileHeader fileHeader : fileHeaderList) {

                if (fileHeader.isEncrypted()) {
                }

                // try to find current archive entry in list of files to extraction
                ArchiveScanner.File file = extractFileTree.findFile(fileHeader.getFileNameString());
                // if current entry shouldn't be extracted - goto next
                if (file == null || file.isDirectory()) {
                    continue;
                }

                try {
                    String outputPath = adjustExtractDirectory(file, extractFileTree, outputDir);

                    OutputStream stream = new FileOutputStream(new File(outputPath));
                    arch.extractFile(fileHeader, stream);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (listener != null) {
                    listener.onFileExtracted(null);
                }
            }

            return;
        }

        if (is7zArchive(inputFile)) {
            SevenZFile sevenZFile = new SevenZFile(inputFile, encryptedZipPassword == null ? null : encryptedZipPassword.getBytes());

            if (listener != null) {
                listener.beforeExtractStarted(-1);
            }

            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {

                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);

                ArchiveScanner.File file = extractFileTree.findFile(entry.getName());
                if (file != null && !file.isDirectory()) {
                    String outputPath = adjustExtractDirectory(file, extractFileTree, outputDir);
                    OutputStream stream = new FileOutputStream(new File(outputPath));
                    IOUtils.write(content, stream);
                    stream.close();
                }

                entry = sevenZFile.getNextEntry();
            }
            sevenZFile.close();

            return;
        }

        if (listener != null) {
            listener.beforeExtractStarted(extractFileTree.countFiles());
        }

        // get input stream from archive file
        final ArchiveInputStream archiveInputStream = createInputStream(isCompressed ?
                new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(inputFile))) :
                new FileInputStream(inputFile));
        ArchiveEntry entry;
        while ((entry = archiveInputStream.getNextEntry()) != null) {
            String fullPath = entry.getName();

            // try to find current archive entry in list of files to extraction
            ArchiveScanner.File file = extractFileTree.findFile(fullPath);
            // if current entry shouldn't be extracted - goto next
            if (file == null) {
                continue;
            }

            String internalPath = File.separator + file.getFullDirectoryPath();
            if (!extractFileTree.isRoot()) {
                if (!extractFileTree.isDirectory()) { // single file.
                    internalPath = "";
                } else { // find sub path within current working directory.
                    internalPath = File.separator + extractFileTree.getSubDirectoryPath(file);
                }
            }

            String directoryPath = outputDir + internalPath;
            final File outputFile = new File(directoryPath, file.getName());
            File directory = new File(directoryPath);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new FileNotFoundException(App.sInstance.getResources().getString(R.string.error_output_directory_doesnt_exists));
            }

            final OutputStream outputFileStream = new FileOutputStream(outputFile);
            IOUtils.copy(archiveInputStream, outputFileStream);
            outputFileStream.close();

            if (listener != null) {
                listener.onFileExtracted(file);
            }
        }

        archiveInputStream.close();

    }

    private static String adjustExtractDirectory(ArchiveScanner.File file, ArchiveScanner.File extractFileTree, File outputDir) {

        String internalPath = File.separator + file.getFullDirectoryPath();
        if (!extractFileTree.isRoot()) {
            if (!extractFileTree.isDirectory()) { // single file.
                internalPath = "";
            } else { // find sub path within current working directory.
                internalPath = File.separator + extractFileTree.getSubDirectoryPath(file);
            }
        }

        String directoryPath = outputDir + internalPath;
        final File outputFile = new File(directoryPath, file.getName());
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return outputFile.getPath();

        /*
        String outputPath = outputDir.getAbsolutePath();
        if (!outputPath.endsWith(File.separator)) {
            outputPath += "/";
        }
        outputPath += fileName.trim().replace("\\", "/");
        String outputDirectory = outputPath.substring(0, outputPath.lastIndexOf("/"));

        File outDir = new File(outputDirectory);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        return outputPath;
        */
    }

    /**
     * Create archive <code>outputFile</code> from <code>inputFiles</code>.
     *
     * @param inputFiles files to be added to archive.
     * @param outputFile target archive file.
     * @param targetArchiveType target type for archive file.
     * @param additionalCompression additional compression over the original archivation.
     *                              Usually used for non compressed archives, such as <code>ArchiveType.tar</code> or <code>ArchiveType.ar</code>.
     * @param compressionForZip do we need to compress <code>zip</code> archive. Actual only for <code>ArchiveType.zip</code>
     * @param listener listener to notify upper layer about key events while adding items to archive.
     *
     * @throws ArchiveException
     * @throws CompressorException
     * @throws IOException
     * @throws com.openfarmanager.android.model.exeptions.CreateArchiveException
     *
     * @see ArchiveType
     * @see CompressionEnum
     */
    public static void addToArchive(final List<File> inputFiles, String outputFile,
                                    ArchiveType targetArchiveType, CompressionEnum additionalCompression,
                                    boolean compressionForZip, AddToArchiveListener listener)
            throws ArchiveException, CompressorException, IOException, CreateArchiveException {

        File output = new File(outputFile + "." + targetArchiveType.name());
        if (!output.exists() && !output.createNewFile()) {
            throw new CreateArchiveException();
        }

        if (listener != null) {
            listener.beforeStarted(FileUtilsExt.getFilesCount(inputFiles));
        }

        // output file stream
        OutputStream out = new FileOutputStream((output));
        ArchiveOutputStream outputStream = new ArchiveStreamFactory().createArchiveOutputStream(targetArchiveType.name(), out);

        addFilesToArchiveStream(inputFiles, "", targetArchiveType, compressionForZip, outputStream, listener);

        outputStream.finish();
        outputStream.flush();
        outputStream.close();

        if (additionalCompression == null) {
            return;
        }

        File finalOutputFile = new File(output.getAbsolutePath() + "." + CompressionEnum.toString(additionalCompression));
        if (!finalOutputFile.exists() && !finalOutputFile.createNewFile()) {
            throw new CreateArchiveException();
        }

        OutputStream outCompressed = new FileOutputStream(finalOutputFile);
        CompressorOutputStream stream = new CompressorStreamFactory().
                createCompressorOutputStream(CompressionEnum.toString(additionalCompression), outCompressed);

        //org.apache.commons.compress.utils.IOUtils.copy(new FileInputStream(output), stream);

        // manual copying to provide feedback about progress
        int bufferSize = 16384;
        InputStream input = new FileInputStream(output);

        if (listener != null) {
            listener.beforeCompressionStarted((int) output.getAbsoluteFile().length() / bufferSize);
        }

        final byte[] buffer = new byte[bufferSize];
        int n;
        while (-1 != (n = input.read(buffer))) {
            stream.write(buffer, 0, n);

            if (listener != null) {
                listener.onFileAdded(null);
            }
        }

        stream.flush();
        stream.close();

        output.delete();
    }

    /**
     * Add <code>inputFiles</code> to archive output stream (keeping files tree).
     * This code extracted to separate method for recursive calls.
     *
     * @param inputFiles files to be added to archive stream.
     * @param parentPath parent path relatively to current files. used for keeping files tree.
     * @param targetArchiveType target type for archive file.
     * @param compressionForZip do we need to compress <code>zip</code> archive. Actual only for <code>ArchiveType.zip</code>.
     * @param outputStream target archive output stream.
     * @param listener listener to notify upper layer about key events while adding items to archive.
     *
     * @throws IOException
     */
    private static void addFilesToArchiveStream(List<File> inputFiles, String parentPath, ArchiveType targetArchiveType,
                                                boolean compressionForZip, ArchiveOutputStream outputStream,
                                                AddToArchiveListener listener) throws IOException {
        for (File file : inputFiles) {
            if (file.isDirectory()) {
                addFilesToArchiveStream(new ArrayList<File>(Arrays.asList(file.listFiles())), parentPath + file.getName() + "/" ,
                        targetArchiveType, compressionForZip, outputStream, listener);
                continue;
            }

            ArchiveEntry entry = outputStream.createArchiveEntry(file, parentPath + file.getName());

            if (targetArchiveType == ArchiveType.zip) {
                ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) entry;
                zipArchiveEntry.setSize(file.length());
                zipArchiveEntry.setCompressedSize(file.length());
                zipArchiveEntry.setCrc(FileUtils.checksumCRC32(file.getAbsoluteFile()));
                zipArchiveEntry.setMethod(compressionForZip ? ZipEntry.DEFLATED : ZipEntry.STORED);
            }

            outputStream.putArchiveEntry(entry);
            IOUtils.copy(new FileInputStream(file), outputStream);
            outputStream.closeArchiveEntry();

            if (listener != null) {
                listener.onFileAdded(file);
            }
        }
    }

    public static interface ExtractArchiveListener {

        void beforeExtractStarted(int filesToExtract);

        void onFileExtracted(ArchiveScanner.File extractedFile);

    }

    public static interface AddToArchiveListener {

        void beforeStarted(int filesToArchive);

        void beforeCompressionStarted(int fileParts);

        void onFileAdded(File file);

    }

}
