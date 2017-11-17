package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/19.
 * 说明： 章节
 */

public class Section implements Serializable {

    /**
     * sectionname : 第一章 宇宙与地球
     * uuid : 3
     */

    private String sectionname;
    private String uuid;
    private boolean isChecked;

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
