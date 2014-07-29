package com.litesuits.http.request.content.multi;

import com.litesuits.http.data.Consts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 抽象上传类
 * @author MaTianyu
 * @date 14-7-29
 */
public abstract  class AbstractPart {

    public static final Charset charset                  = Charset.forName(Consts.DEFAULT_CHARSET);
    public static final byte[]  CR_LF                    = ("\r\n").getBytes(charset);
    public static final byte[]  TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n".getBytes(charset);
    public static final byte[]  TRANSFER_ENCODING_8BIT   = "Content-Transfer-Encoding: 8bit\r\n".getBytes(charset);


    public String key;
    public String fileName;
    public String contentType;
    public byte[] header;

    protected AbstractPart(String key) {
        this.key = key;
    }

    protected AbstractPart(String key, String fileName, String contentType) {
        this.key = key;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    //此方法需要被调用以产生header（开发者无需自己调用，Entity会调用它）
    public byte[] createHeader(byte[] boundaryLine) {
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
        try {
            headerStream.write(boundaryLine);
            headerStream.write(createContentDisposition(key, fileName));
            if (contentType == null) contentType = Consts.MIME_TYPE_OCTET_STREAM;
            headerStream.write(createContentType(contentType));
            headerStream.write(getTransferEncoding());
            headerStream.write(CR_LF);
            header = headerStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    private byte[] createContentType(String type) {
        return ("Content-Type: " + type + "\r\n").getBytes(charset);
    }

    private byte[] createContentDisposition(final String key, final String fileName) {
        String dis = "Content-Disposition: form-data; name=\"" + key;
        return fileName == null ? (dis + "\"\r\n").getBytes(charset)
                : (dis + "\"; filename=\"" + fileName + "\"\r\n").getBytes(charset);
    }

    public abstract long getTotalLength() throws IOException;

    public abstract byte[] getTransferEncoding();

    public abstract void writeTo(OutputStream out) throws IOException;

    public void writeToServer(OutputStream out) throws IOException {
        if (header == null) throw new RuntimeException("Not call createHeader()，未调用createHeader方法");
        out.write(header);
        updateProgress(header.length);
        writeTo(out);
    }

    protected void updateProgress(int length) {

    }


}
