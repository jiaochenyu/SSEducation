package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/19.
 * 说明： 首页快速练习
 */

public class HomePractice implements Serializable {
    private String name;
    private String id;
    private String imageUrl;
    private int imageLocal;

    public HomePractice(String name, int imageLocal) {
        this.name = name;
        this.imageLocal = imageLocal;
    }

    public HomePractice(int imageLocal) {
        this.imageLocal = imageLocal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageLocal() {
        return imageLocal;
    }

    public void setImageLocal(int imageLocal) {
        this.imageLocal = imageLocal;
    }
}
