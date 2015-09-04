package com.litesuits.http.parser;

import com.litesuits.http.log.HttpLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MaTianyu
 * @date 2015-04-26
 */
public abstract class FileCacheableParser<T> extends DataParser<T> {
    protected File file;

    /**
     * save to default file
     */
    public FileCacheableParser() {
    }

    /**
     * save to this file
     */
    public FileCacheableParser(File saveToFile) {
        this.file = saveToFile;
    }

    @Override
    public boolean isMemCacheSupport() {
        return false;
    }

    /**
     * if you have set a file or path to this parser, return your file.
     * otherwise return new File(cacheDir, cacheKey)
     */
    @Override
    public File getSpecifyFile(String dir) {
        return file != null ? file : new File(dir, request.getCacheKey());
    }

    protected final File streamToFile(InputStream is, long len, String cacheDir) throws IOException {
        File file = getSpecifyFile(cacheDir);
        FileOutputStream fos = null;
        try {
            File pFile = file.getParentFile();
            if (!pFile.exists()) {
                boolean mk = pFile.mkdirs();
                if (HttpLog.isPrint) {
                    HttpLog.i(TAG, "keep cache mkdirs result: " + mk + "  path:" + pFile.getAbsolutePath());
                }
            }
            //if (!file.exists()) {
            //    boolean cf = file.createNewFile();
            //    HttpLog.v(TAG, "keep cache create file result: " + cf + "  path:" + file.getAbsolutePath());
            //}
            fos = new FileOutputStream(file);
            byte[] tmp = new byte[buffSize];
            int l;
            while (!request.isCancelledOrInterrupted() && (l = is.read(tmp)) != -1) {
                tmp = translateBytes(tmp);
                fos.write(tmp, 0, l);
                readLength += l;
                notifyReading(len, readLength);
            }
            if (HttpLog.isPrint && file != null) {
                HttpLog.i("FileParser", "file len: " + file.length());
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return file;
    }

}
