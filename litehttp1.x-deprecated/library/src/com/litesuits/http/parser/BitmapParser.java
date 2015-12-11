package com.litesuits.http.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.litesuits.android.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to bitmap.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class BitmapParser extends DataParser<Bitmap> {
    private File file;

    /**
     * 空构造函数则Bitmap只加载到内存
     */
    public BitmapParser() {

    }

    /**
     * 传地址则Bitmap保存至文件
     *
     * @param path
     */
    public BitmapParser(String path) {
        this.file = new File(path);
    }

    @Override
    public Bitmap parseData(InputStream stream, long len, String charSet) throws IOException {
        return streamToBitmap(stream, len);
    }

    private Bitmap streamToBitmap(InputStream is, long len) throws IOException {
        Bitmap b = null;
        if (file == null) {
            b = BitmapFactory.decodeStream(is);
        } else {
            FileOutputStream fos = null;
            try {
                if (!file.exists() && file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);
                final byte[] tmp = new byte[buffSize];
                int l;
                while (!Thread.currentThread().isInterrupted() && (l = is.read(tmp)) != -1) {
                    fos.write(tmp, 0, l);
                    readLength += l;
                    if (httpReadingListener != null) httpReadingListener.onReading(request, len, readLength);
                }
                b = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (Log.isPrint && file != null) Log.i("FileParser", "file len: " + file.length());
            } finally {
                if (fos != null) fos.close();
            }
        }
        return b;
    }
}
