package com.litesuits.http.request;

import android.graphics.Bitmap;
import com.litesuits.http.parser.impl.BitmapParser;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.param.RequestModel;

import java.io.File;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class BitmapRequest extends AbstractRequest<Bitmap> {

    private File saveToFile;

    public BitmapRequest() {
        super();
    }

    public BitmapRequest(RequestModel model) {
        super(model);
    }

    public BitmapRequest(RequestModel model, File saveToFile) {
        super(model);
        this.saveToFile = saveToFile;
    }

    public BitmapRequest(RequestModel model, String saveToPath) {
        super(model);
        setFileSavePath(saveToPath);
    }

    public BitmapRequest(String url) {
        super(url);
    }

    public BitmapRequest(String url, File saveToFile) {
        super(url);
        this.saveToFile = saveToFile;
    }

    public BitmapRequest(String url, String saveToPath) {
        super(url);
        setFileSavePath(saveToPath);
    }


    public void setFileSavePath(String savaToPath) {
        if (savaToPath != null) {
            saveToFile = new File(savaToPath);
        }
    }

    @Override
    protected DataParser<Bitmap> createDataParser() {
        return new BitmapParser(this, saveToFile);
    }
}
