package com.litesuits.http.request.content.multi;

import com.litesuits.android.log.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 上传字节
 *
 * @author MaTianyu
 * @date 14-7-29
 */
public class BytesPart extends AbstractPart {
    public byte[] bytes;
    public static final String TAG = BytesPart.class.getSimpleName();

    public BytesPart(String key, byte[] bytes) {
        this(key, bytes, null);
    }

    public BytesPart(String key, byte[] bytes, String contentType) {
        super(key, null, contentType);
        this.bytes = bytes;
    }

    public long getTotalLength() {
        if (Log.isPrint) if (Log.isPrint) Log.v(TAG, TAG + "内容长度 header ： " + header.length + " ,body: "
                + bytes.length + " ," + "换行：" + CR_LF.length);
        return header.length + bytes.length + CR_LF.length;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes);
        out.write(CR_LF);
    }
}
