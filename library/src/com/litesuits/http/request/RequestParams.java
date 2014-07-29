package com.litesuits.http.request;

import com.litesuits.http.data.Consts;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.InputStream;

/**
 * define the interface, method, file parameter and input stream parameter, etc.
 *
 * @author MaTianyu
 *         2014-1-18上午2:40:43
 */
public class RequestParams {

    public static class HttpEntity {
        public String contentType;
    }

    public static class FileEntity extends HttpEntity {
        public File file;

        public FileEntity(File file, String contentType) {
            this.file = file;
            this.contentType = contentType;
        }
    }

    public static class StringEntity extends HttpEntity {
        public String string;
        public String charset;

        public StringEntity(String string) {
            this(string, null,null);
        }
        public StringEntity(String string, String mimeType, String charset) {
            this.string = string;
            if (mimeType == null) {
                mimeType = Consts.MIME_TYPE_TEXT;
            }
            if (charset == null) {
                charset = Consts.DEFAULT_CHARSET;
            }
            this.charset = charset;
            this.contentType = mimeType + HTTP.CHARSET_PARAM + charset;
        }

        @Override
        public String toString() {
            return "StringEntity{" +
                    "string='" + string + '\'' +
                    ", charset='" + charset + '\'' +
                    ", contentType='" + contentType + '\'' +
                    '}';
        }
    }

    public static class ByteArrayEntity extends HttpEntity {
        public byte[] bytes;

        public ByteArrayEntity(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }

    }

    public static class InputStreamEntity extends HttpEntity {
        public InputStream inputStream;
        public String name;

        public InputStreamEntity(InputStream inputStream, String name, String contentType) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
        }

        @Override
        public String toString() {
            return "InputStreamEntity{" +
                    "inputStream=" + inputStream +
                    ", name='" + name + '\'' +
                    ", contentType='" + contentType + '\'' +
                    '}';
        }
    }
}
