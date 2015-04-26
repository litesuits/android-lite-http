package com.litesuits.http.parser;

import android.os.Build;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.request.AbstractRequest;
import org.apache.http.util.CharArrayBuffer;

import java.io.*;
import java.nio.charset.Charset;

/**
 * parse inputstream to string.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public abstract class MemeoryDataParser<T> extends DataParser<T> {

    public MemeoryDataParser(AbstractRequest<T> request) {
        super(request);
    }

    @Override
    public boolean isMemCacheSupport() {
        return true;
    }

    @Override
    public File getSpecifyFile(String dir) {
        return new File(dir, request.getCacheKey());
    }

    protected abstract T parseDiskCache(InputStream is, long length) throws IOException;

    public T readDisk(File file) {
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(file);
            data = parseDiskCache(fos, file.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    protected final String streamToString(InputStream is, long len, String charSet) throws IOException {
        if (len > 0) {
            Reader reader = new InputStreamReader(is, charSet);
            CharArrayBuffer buffer = new CharArrayBuffer((int) len);
            try {
                char[] tmp = new char[buffSize];
                int l;
                while (!request.isCancelledOrInterrupted() && (l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                    readLength += l;
                    if (buffSize < len) {
                        notifyReading(len, readLength);
                    }
                }
            } finally {
                reader.close();
            }
            return buffer.toString();
        } else {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!request.isCancelledOrInterrupted() && (l = is.read(buff)) > 0) {
                    swapStream.write(buff, 0, l);
                    readLength += l;
                    notifyReading(len, readLength);
                }
                return swapStream.toString(charSet);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                swapStream.close();
            }
            return null;
        }

    }

    protected final void keepToCache(byte[] data, File file) {
        if (data != null) {
            FileOutputStream fos = null;
            try {
                File pFile = file.getParentFile();
                if (!pFile.exists()) {
                    boolean mk = pFile.mkdirs();
                    HttpLog.i(TAG, "keep cache mkdirs result: " + mk + "  path:" + pFile.getAbsolutePath());
                }
                //if (!file.exists()) {
                //    boolean cf = file.createNewFile();
                //    HttpLog.v(TAG, "keep cache create file result: " + cf + "  path:" + file.getAbsolutePath());
                //}
                fos = new FileOutputStream(file);
                fos.write(data);
                HttpLog.v(TAG,
                          "lite http keep disk cache success, "
                          + "   tag: " + request.getTag()
                          + "   url: " + request.getUri()
                          + "   key: " + request.getCacheKey()
                          + "   path: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    protected final void keepToCache(String src, File file) {
        if (data != null) {
            keepToCache(getBytes(src, Charset.forName(charSet)), file);
        }
    }

    protected byte[] getBytes(String src, Charset charSet) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            try {
                return src.getBytes(charSet.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return src.getBytes(charSet);
        }
    }

}
