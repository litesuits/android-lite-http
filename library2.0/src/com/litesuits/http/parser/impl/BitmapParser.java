package com.litesuits.http.parser.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.litesuits.http.parser.FileDataParser;
import com.litesuits.http.request.AbstractRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to bitmap.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class BitmapParser extends FileDataParser<Bitmap> {
    /**
     * save to default path
     */
    public BitmapParser(AbstractRequest<Bitmap> request) {
        super(request);
    }

    /**
     * save to this file
     */
    public BitmapParser(AbstractRequest<Bitmap> request, File file) {
        super(request);
        this.file = file;
    }

    @Override
    public Bitmap parseNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        File file = streamToFile(stream, len, cacheDir);
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    public Bitmap readFromDiskCache(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

}
