package com.litesuits.http.request.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class SerializableBody extends ByteArrayBody {

    public SerializableBody(Serializable ser) {
        super(getBytes(ser), null);
    }

    public static byte[] getBytes(Serializable ser) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(baos);
            oos.writeObject(ser);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
