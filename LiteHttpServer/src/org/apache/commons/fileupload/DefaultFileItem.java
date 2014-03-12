/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.fileupload;

import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItem;

/**
 * <p> The default implementation of the
 * {@link org.apache.commons.fileupload.FileItem FileItem} interface.
 *
 * <p> After retrieving an instance of this class from a {@link
 * org.apache.commons.fileupload.DiskFileUpload DiskFileUpload} instance (see
 * {@link org.apache.commons.fileupload.DiskFileUpload
 * #parseRequest(javax.servlet.http.HttpServletRequest)}), you may
 * either request all contents of file at once using {@link #get()} or
 * request an {@link java.io.InputStream InputStream} with
 * {@link #getInputStream()} and process the file without attempting to load
 * it into memory, which may come handy with large files.
 *
 * @version $Id: DefaultFileItem.java 1454690 2013-03-09 12:08:48Z simonetripodi $
 *
 * @deprecated 1.1 Use <code>DiskFileItem</code> instead.
 */
@Deprecated
public class DefaultFileItem
    extends DiskFileItem {

    // ----------------------------------------------------------- Constructors

    /**
     * The UID to use when serializing this instance.
     */
    private static final long serialVersionUID = 4088572813833518255L;

    /**
     * Constructs a new <code>DefaultFileItem</code> instance.
     *
     * @param fieldName     The name of the form field.
     * @param contentType   The content type passed by the browser or
     *                      <code>null</code> if not specified.
     * @param isFormField   Whether or not this item is a plain form field, as
     *                      opposed to a file upload.
     * @param fileName      The original filename in the user's filesystem, or
     *                      <code>null</code> if not specified.
     * @param sizeThreshold The threshold, in bytes, below which items will be
     *                      retained in memory and above which they will be
     *                      stored as a file.
     * @param repository    The data repository, which is the directory in
     *                      which files will be created, should the item size
     *                      exceed the threshold.
     *
     * @deprecated 1.1 Use <code>DiskFileItem</code> instead.
     */
    @Deprecated
    public DefaultFileItem(String fieldName, String contentType,
            boolean isFormField, String fileName, int sizeThreshold,
            File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold,
                repository);
    }

}
