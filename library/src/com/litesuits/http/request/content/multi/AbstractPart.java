package com.litesuits.http.request.content.multi;

import com.litesuits.http.data.Consts;
import com.litesuits.http.utils.StringCodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 抽象上传类
 *
 * @author MaTianyu
 * @date 14-7-29
 */
public abstract class AbstractPart {

    protected static final Charset infoCharset = BoundaryCreater.charset;
    public static final byte[] CR_LF = StringCodingUtils.getBytes("\r\n",infoCharset);
    public static final byte[] TRANSFER_ENCODING_BINARY =
            StringCodingUtils.getBytes("Content-Transfer-Encoding: binary\r\n", infoCharset );
    public static final byte[] TRANSFER_ENCODING_8BIT =
            StringCodingUtils.getBytes("Content-Transfer-Encoding: 8bit\r\n", infoCharset);


    protected String key;
    public    byte[] header;
    protected String mimeType = Consts.MIME_TYPE_OCTET_STREAM;

    protected AbstractPart(String key,String mimeType) {
        this.key = key;
        if(mimeType != null) this.mimeType = mimeType;
    }

    //此方法需要被调用以产生header（开发者无需自己调用，Entity会调用它）
    public byte[] createHeader(byte[] boundaryLine) {
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
        try {
            headerStream.write(boundaryLine);
            headerStream.write(createContentDisposition());
            headerStream.write(createContentType());
            headerStream.write(getTransferEncoding());
            headerStream.write(CR_LF);
            header = headerStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    protected abstract byte[] createContentType();

    protected abstract byte[] createContentDisposition();

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
