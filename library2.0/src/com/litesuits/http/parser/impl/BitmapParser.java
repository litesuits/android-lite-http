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
    /**
     * save to default path
     */
    //public BitmapParser(AbstractRequest<Bitmap> request) {
    //    super(request);
    //}

    /**
     * save to this file
     */
    public BitmapParser(File file) {
        //super(request);
        this.file = file;
    }

    @Override
    public Bitmap parseNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        if (request.isCachedModel() || this.file != null
            || (request.getHttpListener() != null && request.getHttpListener().isReadingNotify())) {
            File file = streamToFile(stream, len, cacheDir);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            return BitmapFactory.decodeStream(stream);
        }
    }

    @Override
    public Bitmap readFromDiskCache(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}