package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.FileParser;
import com.litesuits.http.request.param.HttpParamModel;

import java.io.File;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class FileRequest extends AbstractRequest<File> {

    private File saveToFile;

    public FileRequest(String url) {
        super(url);
    }

    public FileRequest(HttpParamModel model, String savaToPath) {
        super(model);
        setFileSavePath(savaToPath);
    }

    public FileRequest(HttpParamModel model, File saveToFile) {
        super(model);
        this.saveToFile = saveToFile;
    }

    public FileRequest(String url, File saveToFile) {
        super(url);
        this.saveToFile = saveToFile;
    }

    public FileRequest(String url, String savaToPath) {
        super(url);
        setFileSavePath(savaToPath);
    }

    public FileRequest setFileSavePath(String savaToPath) {
        if (savaToPath != null) {
            saveToFile = new File(savaToPath);
        }
        return this;
    }

    public File getCachedFile() {
        return saveToFile != null ? saveToFile : super.getCachedFile();
    }

    @Override
    public DataParser<File> createDataParser() {
        return new FileParser(saveToFile);
    }
}
