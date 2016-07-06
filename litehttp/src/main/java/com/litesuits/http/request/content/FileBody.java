package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

import java.io.*;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class FileBody extends HttpBody {
    private File file;

    public FileBody(File file) {
        this(file, Consts.MIME_TYPE_OCTET_STREAM);
    }

    public FileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    public File getFile() {
        return file;
    }

    @Override
    public long getContentLength() {
        return file.length();
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        final InputStream instream = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
            outstream.flush();
        } finally {
            instream.close();
        }
    }
}
