package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/10/16.
 * 说明：
 */

public class Apk implements Serializable {

    private int versioncode;
    private String path;
    /**
     * createtime : 2017-10-16 16:01:33.0
     * versionname : 1.2
     * remarks : 更新了啥啥啥啥啥
     */

    private String createtime;
    private String versionname;
    private String remarks;

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
