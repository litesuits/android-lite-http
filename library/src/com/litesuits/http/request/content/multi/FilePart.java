package com.litesuits.http.request.content.multi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 上传文件
 *
 * @author MaTianyu
 * @date 14-7-29
 */
public class FilePart extends InputStreamPart {
    public File file;

    public FilePart(String key, File file) {
        this(key, file, null);
    }

    public FilePart(String key, File file, String contentType) {
        super(key, getInputStream(file), file.getName(), contentType);
        this.file = file;
    }
    public static InputStream getInputStream(File file){
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public long getTotalLength() {
        return header.length + file.length();
    }
}
