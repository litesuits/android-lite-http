package com.litesuits.http.model;

import java.util.ArrayList;

/**
 * response string will be converted to this model
 */
public class User extends ApiModel<User.Data> {

    public static class Data extends BaseModel{
        public int age;
        public String name;
        public ArrayList<String> girl_friends;

        @Override
        public String toString() {
            return "Data{" +
                   "age=" + age +
                   ", name='" + name + '\'' +
                   ", girl_friends=" + girl_friends +
                   "} " ;
        }
    }
}
