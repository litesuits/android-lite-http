package com.litesuits.http.model;

/**
 * response string will be converted to this model
 */
public class User extends BaseModel {

    private int age;
    protected String name;

    @Override
    public String toString() {
        return "User{" + "age=" + age + ", name='" + name + '\'' + '}';
    }
}
