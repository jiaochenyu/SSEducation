package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/10/31.
 * 说明： 按时间分割
 */

public class HomeWorkDate implements Serializable {
    private String date;
    private List<HomeWorkInfo> mHomeWorkInfos;


    public HomeWorkDate() {
    }

    public HomeWorkDate(String date, List<HomeWorkInfo> homeWorkInfos) {
        this.date = date;
        mHomeWorkInfos = homeWorkInfos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<HomeWorkInfo> getHomeWorkInfos() {
        return mHomeWorkInfos;
    }

    public void setHomeWorkInfos(List<HomeWorkInfo> homeWorkInfos) {
        mHomeWorkInfos = homeWorkInfos;
    }
}
