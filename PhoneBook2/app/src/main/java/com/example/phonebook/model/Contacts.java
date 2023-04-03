package com.example.phonebook.model;

import java.io.Serializable;

public class Contacts implements Serializable {
    private int id;
    private String name;
    private byte[] image;
    private String mobile;

    public Contacts(int id, String name, byte[] image, String mobile) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.mobile = mobile;
    }

    public Contacts() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
