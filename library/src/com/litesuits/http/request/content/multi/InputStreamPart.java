package com.litesuits.http.request.content.multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 上传数据流
 *
 * @author MaTianyu
 * @date 14-7-29
 */
public class InputStreamPart extends AbstractPart {
    public InputStream inputStream;

    public InputStreamPart(String key, InputStream inputStream) {
        this(key, inputStream, null, null);
    }

    public InputStreamPart(String key, InputStream inputStream, String contentType) {
        this(key, inputStream, null, contentType);
    }

    public InputStreamPart(String key, InputStream inputStream, String fileName, String contentType) {
        super(key, fileName, contentType);
        this.inputStream = inputStream;
    }

    public long getTotalLength() throws IOException {
        return -1;
    }

    //public long getTotalLength() throws IOException {
    //    Log.i("ISP", "inputStream.available(): " + inputStream.available());
    //    return header.length + inputStream.available();
    //}

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    public void writeTo(OutputStream out) throws IOException {
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = inputStream.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                updateProgress(l);
            }
            out.write(CR_LF);
            updateProgress(CR_LF.length);
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }
}
