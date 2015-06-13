/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.litesuits.http.impl.apache.entity;

import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.IOException;

/**
 * Abstract base class for entities.
 * Provides the commonly used attributes for streamed and self-contained
 * implementations of {@link HttpEntity HttpEntity}.
 *
 * @since 4.0
 */
public abstract class AbstractHttpEntity implements HttpEntity {
    /*______________________ add by MaTianyu ____________________*/
    protected final static int BUFFER_SIZE = 4096;
    protected HttpListener httpListener;
    protected AbstractRequest request;
    protected long bytesWritten;
    protected long totalSize;
    /*______________________ add by MaTianyu over____________________*/

    protected Header contentType;
    protected Header contentEncoding;
    protected boolean chunked;

    /**
     * Protected default constructor.
     * The attributes of the created object remain
     * <code>null</code> and <code>false</code>, respectively.
     */
    protected AbstractHttpEntity() {
        super();
    }


    /**
     * Obtains the Content-Type header.
     * The default implementation returns the value of the
     * {@link #contentType contentType} attribute.
     *
     * @return the Content-Type header, or <code>null</code>
     */
    public Header getContentType() {
        return this.contentType;
    }


    /**
     * Obtains the Content-Encoding header.
     * The default implementation returns the value of the
     * {@link #contentEncoding contentEncoding} attribute.
     *
     * @return the Content-Encoding header, or <code>null</code>
     */
    public Header getContentEncoding() {
        return this.contentEncoding;
    }

    /**
     * Obtains the 'chunked' flag.
     * The default implementation returns the value of the
     * {@link #chunked chunked} attribute.
     *
     * @return the 'chunked' flag
     */
    public boolean isChunked() {
        return this.chunked;
    }


    /**
     * Specifies the Content-Type header.
     * The default implementation sets the value of the
     * {@link #contentType contentType} attribute.
     *
     * @param contentType the new Content-Encoding header, or
     *                    <code>null</code> to unset
     */
    public void setContentType(final Header contentType) {
        this.contentType = contentType;
    }

    /**
     * Specifies the Content-Type header, as a string.
     * The default implementation calls
     * {@link #setContentType(Header) setContentType(Header)}.
     *
     * @param ctString the new Content-Type header, or
     *                 <code>null</code> to unset
     */
    public void setContentType(final String ctString) {
        Header h = null;
        if (ctString != null) {
            h = new BasicHeader(HTTP.CONTENT_TYPE, ctString);
        }
        setContentType(h);
    }


    /**
     * Specifies the Content-Encoding header.
     * The default implementation sets the value of the
     * {@link #contentEncoding contentEncoding} attribute.
     *
     * @param contentEncoding the new Content-Encoding header, or
     *                        <code>null</code> to unset
     */
    public void setContentEncoding(final Header contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * Specifies the Content-Encoding header, as a string.
     * The default implementation calls
     * {@link #setContentEncoding(Header) setContentEncoding(Header)}.
     *
     * @param ceString the new Content-Encoding header, or
     *                 <code>null</code> to unset
     */
    public void setContentEncoding(final String ceString) {
        Header h = null;
        if (ceString != null) {
            h = new BasicHeader(HTTP.CONTENT_ENCODING, ceString);
        }
        setContentEncoding(h);
    }


    /**
     * Specifies the 'chunked' flag.
     * The default implementation sets the value of the
     * {@link #chunked chunked} attribute.
     *
     * @param b the new 'chunked' flag
     */
    public void setChunked(boolean b) {
        this.chunked = b;
    }


    /**
     * Does not consume anything.
     * The default implementation does nothing if
     * {@link HttpEntity#isStreaming isStreaming}
     * returns <code>false</code>, and throws an exception
     * if it returns <code>true</code>.
     * This removes the burden of implementing
     * an empty method for non-streaming entities.
     *
     * @throws IOException                   in case of an I/O problem
     * @throws UnsupportedOperationException if a streaming subclass does not override this method
     */
    public void consumeContent()
            throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException
                    ("streaming entity does not implement consumeContent()");
        }
    } // consumeContent

    /*______________________ add by MaTianyu ____________________*/
    @SuppressWarnings("unchecked")
    protected void updateProgress(long count) {
        bytesWritten += count;
        if (httpListener != null) {
            httpListener.notifyCallUploading(request, totalSize, bytesWritten);
        }
    }

    public HttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public AbstractRequest getRequest() {
        return request;
    }

    public void setRequest(AbstractRequest request) {
        this.request = request;
        setHttpListener(request.getHttpListener());
    }
    /*______________________ add by MaTianyu voer ____________________*/
} // class AbstractHttpEntity
