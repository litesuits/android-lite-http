package com.litesuits.http.impl.apache;

import com.litesuits.http.request.content.MultipartBody;
import com.litesuits.http.request.content.multi.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Simplified multipart entity mainly used for sending one or more files.
 *
 * @author MaTianyu
 */
class MultipartEntity implements HttpEntity {


    private String boundary;
    private byte[] boundaryLine;
    private byte[] boundaryEnd;
    private boolean isRepeatable = false;
    private int bytesWritten;
    private int totalSize;

    private List<AbstractPart> httpBodyParts = new ArrayList<AbstractPart>();

    public MultipartEntity() {
        this(null);
        BoundaryCreater creater = new BoundaryCreater();
        boundary = creater.getBoundary();
        boundaryLine = creater.getBoundaryLine();
        boundaryEnd = creater.getBoundaryEnd();
    }

    public MultipartEntity(MultipartBody body) {
        if (body != null) {
            boundary = body.getBoundary();
            boundaryLine = body.getBoundaryLine();
            boundaryEnd = body.getBoundaryEnd();
            if (body.getHttpParts() != null) {
                for (AbstractPart part : body.getHttpParts()) {
                    addPart(part);
                }
            }
        }
    }

    public void addPart(String key, String string, String charset, String mimeType) throws UnsupportedEncodingException {
        addPart(new StringPart(key, string, charset, mimeType));
    }

    public void addPart(String key, byte[] bytes, String mimeType) {
        addPart(new BytesPart(key, bytes, mimeType));
    }

    public void addPart(String key, File file, String mimeType) throws FileNotFoundException {
        addPart(new FilePart(key, file, mimeType));
    }

    public void addPart(String key, InputStream inputStream, String fileName, String mimeType) {
        addPart(new InputStreamPart(key, inputStream, fileName, mimeType));
    }

    public void addPart(AbstractPart part) {
        part.createHeader(boundaryLine);
        httpBodyParts.add(part);
    }

    private void updateProgress(int count) {
        bytesWritten += count;
    }

    @Override
    public long getContentLength() {
        long contentLen = -1;
        try {
            for (AbstractPart part : httpBodyParts) {
                long len = 0;
                len = part.getTotalLength();
                if (len < 0) {
                    return -1;
                }
                contentLen += len;
            }
            contentLen += boundaryEnd.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentLen;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    public void setIsRepeatable(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    @Override
    public boolean isRepeatable() {
        return isRepeatable;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        bytesWritten = 0;
        totalSize = (int) getContentLength();

        for (AbstractPart part : httpBodyParts) {
            part.writeToServer(outstream);
        }

        outstream.write(boundaryEnd);

        updateProgress(boundaryEnd.length);
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }


    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("getContent() is not supported. Use writeTo() instead.");
    }

}