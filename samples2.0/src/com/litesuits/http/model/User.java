package com.litesuits.http.model;

/**
 * response string will be converted to this model
 */
public class User extends BaseModel {

    private int age;
    protected String name;

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
               "age=" + age +
               ", name='" + name + '\'' +
               "} ";
    }
}
