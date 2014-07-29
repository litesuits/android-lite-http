package com.litesuits.http.request.content;

import java.io.File;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class FileBody extends AbstractContentBody {
    public File file;

    public FileBody(File file) {
        this(file,null);
    }

    public FileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }
}
