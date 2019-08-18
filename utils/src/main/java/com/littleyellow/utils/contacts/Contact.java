package com.littleyellow.utils.contacts;

import android.support.annotation.NonNull;

public class Contact implements Comparable<Contact>{

    private String name;        //联系人姓名

    private String telPhone;    //电话号码

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public Contact() {
    }

    public Contact(String name, String telPhone) {
        this.name = name;
        this.telPhone = telPhone;
    }

    @Override
    public int compareTo(@NonNull Contact o) {
        return 0;
    }
}
