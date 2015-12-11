package com.litesuits.http.request.content.multi;

import com.litesuits.android.log.Log;
import com.litesuits.http.data.Consts;
import com.litesuits.http.utils.StringCodingUtils;

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
    protected InputStream inputStream;

    protected static final String TAG = InputStreamPart.class.getSimpleName();
    protected String fileName;

    public InputStreamPart(String key, InputStream inputStream) {
        this(key, inputStream, null, Consts.MIME_TYPE_OCTET_STREAM);
    }

    public InputStreamPart(String key, InputStream inputStream, String contentType) {
        this(key, inputStream, null, contentType);
    }

    public InputStreamPart(String key, InputStream inputStream, String fileName, String mimeType) {
        super(key, mimeType);
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Consts.CONTENT_TYPE + ": " + mimeType + "\r\n", infoCharset);
    }

    @Override
    protected byte[] createContentDisposition() {
        String dis = "Content-Disposition: form-data; name=\"" + key;
        return fileName == null ? StringCodingUtils.getBytes(dis + "\"\r\n", infoCharset)
                : StringCodingUtils.getBytes(dis + "\"; filename=\"" + fileName + "\"\r\n", infoCharset);
    }

    @Override
    public long getTotalLength() throws IOException {
        long len = inputStream.available();
        if (Log.isPrint) Log.v(TAG, TAG + "内容长度 header ： " + header.length + " ,body: " + len + " ," +
                "换行：" + CR_LF.length);
        return header.length + len + CR_LF.length;
    }

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
