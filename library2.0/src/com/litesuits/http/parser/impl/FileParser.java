package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.FileDataParser;
import com.litesuits.http.request.AbstractRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to file.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class FileParser extends FileDataParser<File> {
    /**
     * save to default path
     */
    public FileParser(AbstractRequest<File> request) {
        super(request);
    }

    /**
     * save to this file
     */
    public FileParser(AbstractRequest<File> request, File saveToFile) {
        super(request);
        this.file = saveToFile;
    }

    @Override
    public File parseNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        return streamToFile(stream, len, cacheDir);
    }

    @Override
    public File readDisk(File file) {
        return file;
    }
}
