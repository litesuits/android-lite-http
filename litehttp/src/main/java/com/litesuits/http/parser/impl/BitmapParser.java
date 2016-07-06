package com.litesuits.http.parser.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.litesuits.http.parser.FileCacheableParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to bitmap.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class BitmapParser extends FileCacheableParser<Bitmap> {
    public BitmapParser() {}
    public BitmapParser(File file) {
        this.file = file;
    }

    @Override
    public Bitmap parseNetStream(InputStream stream, long len, String charSet) throws IOException {
        //if (this.file != null || request.isCachedModel()
        //    || (request.getHttpListener() != null && request.getHttpListener().isReadingNotify())) {
        //    File file = streamToFile(stream, len, cacheDir);
        //    return BitmapFactory.decodeFile(file.getAbsolutePath());
        //} else {
        //    return BitmapFactory.decodeStream(stream);
        //}
        file = streamToFile(stream, len);
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    public Bitmap parseDiskCache(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}