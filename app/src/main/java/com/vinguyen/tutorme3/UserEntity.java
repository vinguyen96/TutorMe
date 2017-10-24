package com.vinguyen.tutorme3;

/**
 * Created by vinguyen on 31/8/17.
 */

public class UserEntity {
    public String name;
    public String age;
    public String degree;
    public String suburb;
    public String contact;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;
    public String saturday;
    public String sunday;
    public String description;

    public UserEntity() {
        this.monday = "no";
        this.tuesday = "no";
        this.wednesday = "no";
        this.thursday= "no";
        this.friday = "no";
        this.saturday = "no";
        this.sunday = "no";
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

    public UserEntity(String name, String age, String degree, String contact, String suburb, String description) {
        this.name = name;
        this.age = age;
        this.degree = degree;
        this.contact = contact;
        this.suburb = suburb;
        this.monday = "no";
        this.tuesday = "no";
        this.wednesday = "no";
        this.thursday = "no";
        this.friday = "no";
        this.saturday = "no";
        this.sunday = "no";
        this.description = description;

    }

    public String getMonday() {
        return monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
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

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
