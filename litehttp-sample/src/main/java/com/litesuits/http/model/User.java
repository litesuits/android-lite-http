package com.litesuits.http.model;

import java.util.ArrayList;

/**
 * response string will be converted to this model
 */
public class User extends ApiModel<User.Data> {

    public static class Data extends BaseModel{
        private int age;
        private String name;
        private ArrayList<String> girl_friends;

        public int getAge() {
            return age;
        }

        public Data setAge(int age) {
            this.age = age;
            return this;
        }

        public String getName() {
            return name;
        }

        public Data setName(String name) {
            this.name = name;
            return this;
        }

        public ArrayList<String> getGirl_friends() {
            return girl_friends;
        }

        public Data setGirl_friends(ArrayList<String> girl_friends) {
            this.girl_friends = girl_friends;
            return this;
        }

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
