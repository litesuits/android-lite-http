package com.litesuits.http.request.content.multi;

import com.litesuits.http.log.HttpLog;
import com.litesuits.http.data.Consts;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.utils.StringCodingUtils;

import java.io.*;

/**
 * 上传文件
 *
 * @author MaTianyu
 * @date 14-7-29
 */
public class FilePart extends AbstractPart {
    public File file;
    public static final String TAG = FilePart.class.getSimpleName();

    public FilePart(String key, File file) {
        this(key, file, Consts.MIME_TYPE_OCTET_STREAM);
    }

    public FilePart(String key, File file, String mimeType) {
        super(key, mimeType);
        this.file = file;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Consts.CONTENT_TYPE + ": " + mimeType + "\r\n", infoCharset);
    }

    @Override
    protected byte[] createContentDisposition() {
        String dis = "Content-Disposition: form-data; name=\"" + key;
        return StringCodingUtils.getBytes(dis + "\"; filename=\"" + file.getName() + "\"\r\n", infoCharset);
    }


    public long getTotalLength() {
        long len = file.length();
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, TAG + " 内容长度header ： " + header.length
                           + " ,body: " + len + " ," + "换行：" + CR_LF.length);
        }
        return header.length + len + CR_LF.length;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    public void writeTo(OutputStream out) throws IOException {
        final InputStream instream = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[HttpBody.OUTPUT_BUFFER_SIZE];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                updateProgress(l);
            }
            out.write(CR_LF);
            updateProgress(CR_LF.length);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            instream.close();
        }
    }

}
