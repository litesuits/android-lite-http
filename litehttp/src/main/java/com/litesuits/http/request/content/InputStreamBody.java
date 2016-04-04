package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class InputStreamBody extends HttpBody {
    protected InputStream instream;
    protected long length;

    public InputStreamBody(InputStream instream) {
        this(instream, null);
    }

    public InputStreamBody(InputStream instream, String contentType) {
        this(instream, contentType, -1);
    }

    public InputStreamBody(InputStream instream, String contentType, long length) {
        this.instream = instream;
        this.contentType = (contentType != null) ? contentType : Consts.MIME_TYPE_OCTET_STREAM;
        this.length = length;
    }

    public InputStream getInstream() {
        return instream;
    }

    @Override
    public long getContentLength() {
        return length;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (instream == null) {
            return;
        }
        try {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            if (this.length < 0) {
                // consume until EOF
                while ((l = instream.read(buffer)) != -1) {
                    outstream.write(buffer, 0, l);
                }
            } else {
                // consume no more than length
                long remaining = this.length;
                while (remaining > 0) {
                    l = instream.read(buffer, 0, (int) Math.min(OUTPUT_BUFFER_SIZE, remaining));
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                    remaining -= l;
                }
            }
            outstream.flush();
        } finally {
            instream.close();
        }
    }

    @Override
    public String toString() {
        return "InputStreamBody{" +
               "instream=" + instream +
               ", length=" + length +
               "} " + super.toString();
    }
}
