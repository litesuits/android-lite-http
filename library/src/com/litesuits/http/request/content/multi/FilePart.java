package com.litesuits.http.request.content.multi;

import com.litesuits.android.log.Log;

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
    public static final String TAG = FilePart.class.getSimpleName();

    public FilePart(String key, File file) {
        this(key, file, null);
    }

    public FilePart(String key, File file, String contentType) {
        super(key, getInputStream(file), file.getName(), contentType);
        this.file = file;
    }

    public static InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getTotalLength() {
        long len = file.length();
        if (Log.isPrint) Log.v(TAG, TAG + " 内容长度header ： " + header.length + " ,body: " + len + " ," +
                "换行：" + CR_LF.length);
        return header.length + len + CR_LF.length;
    }
}
