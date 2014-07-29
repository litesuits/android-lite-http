package com.litesuits.http.request.content;

import com.litesuits.http.request.content.multi.AbstractPart;

import java.util.LinkedList;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class MultipartBody extends AbstractBody {
    private LinkedList<AbstractPart> httpParts;

    public LinkedList<AbstractPart> getHttpParts() {
        return httpParts;
    }

    public MultipartBody setHttpParts(LinkedList<AbstractPart> httpParts) {
        this.httpParts = httpParts;
        return this;
    }

    public MultipartBody addPart(AbstractPart part) {
        if (part == null) return this;
        if (httpParts == null) httpParts = new LinkedList<AbstractPart>();
        httpParts.add(part);
        return this;
    }
}
