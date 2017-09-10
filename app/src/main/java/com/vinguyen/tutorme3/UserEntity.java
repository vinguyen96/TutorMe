package com.vinguyen.tutorme3;

/**
 * Created by vinguyen on 31/8/17.
 */

public class UserEntity {
    public String name;
    public String age;
    public String degree;
    public String contact;
    public String monday;

    public UserEntity() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public UserEntity(String name, String age, String degree, String contact) {
        this.name = name;
        this.age = age;
        this.degree = degree;
        this.contact = contact;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }



    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDegree() {
        return degree;
    }

    public String getContact() {
        return contact;
    }
}
