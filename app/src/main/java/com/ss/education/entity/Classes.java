package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/10/9.
 * 说明： 班级
 */

public class Classes implements Serializable {

    /**
     * createtime : 2017-10-09 14:37:45.0
     * classrs : 0
     * classname : 高三物理一班
     * teacherid : 06b7636392424d86848b9e26277ed983
     * remarks : 班级备注
     * uuid : 2e7b79820b884c96aa398d9d9ad909a2
     * classbh : 1234567
     * teachername : 位育老师
     * shrs :100
     * state: 0 1 2  // 0审核中 1 同意 2拒绝
     */

    private String createtime;
    private int classrs; //班级人数
    private String classname;
    private String teacherid;
    private String remarks;
    private String uuid;
    private String classbh;
    private String teachername;
    private int shrs;  //待审核人数
    private int state;
    private String imgpath;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getClassrs() {
        return classrs;
    }

    public void setClassrs(int classrs) {
        this.classrs = classrs;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClassbh() {
        return classbh;
    }

    public void setClassbh(String classbh) {
        this.classbh = classbh;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public int getShrs() {
        return shrs;
    }

    public void setShrs(int shrs) {
        this.shrs = shrs;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }
}
