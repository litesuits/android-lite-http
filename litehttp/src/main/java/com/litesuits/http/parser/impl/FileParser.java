package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.FileCacheableParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to file.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class FileParser extends FileCacheableParser<File> {
    public FileParser() {
    }
    public FileParser(File saveToFile) {
        this.file = saveToFile;
    }

    @Override
    public File parseNetStream(InputStream stream, long len, String charSet) throws IOException {
        return streamToFile(stream, len);
    }

    @Override
    public File parseDiskCache(File file) {
        return file;
    }
}
