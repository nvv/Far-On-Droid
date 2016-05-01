package com.openfarmanager.android.core.network.webdav;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A RequestEntity that contains an InputStream.
 *
 * @since 3.0
 */
public class InputStreamRequestEntity implements RequestEntity {

    private static final Log LOG = LogFactory.getLog(InputStreamRequestEntity.class);

    private InputStream content;
    private File mSource;

    private OutputStreamListener mListener;

    public InputStreamRequestEntity(File file) {
        mSource = file;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
     */
    public String getContentType() {
        return null;
    }


    /**
     * Tests if this method is repeatable.  Only <code>true</code> if the content has been
     * buffered.
     *
     * @see #getContentLength()
     */
    public boolean isRepeatable() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.httpclient.RequestEntity#writeRequest(java.io.OutputStream)
     */
    public void writeRequest(OutputStream out) throws IOException {

        content = new FileInputStream(mSource);
        byte[] tmp = new byte[32 * 1024];
        int i;
        while ((i = content.read(tmp)) > 0) {
            out.write(tmp, 0, i);
            mListener.onProgress(i);
        }
    }

    public long getContentLength() {
        return mSource.length();
    }

    public void setListener(OutputStreamListener listener) {
        mListener = listener;
    }

    public interface OutputStreamListener {
        void onProgress(long bytes);
    }
}
