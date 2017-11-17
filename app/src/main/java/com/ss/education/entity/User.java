package com.ss.education.entity;

import com.ss.education.base.ConnectUrl;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class User implements Serializable {

    /**
     * schoolid : 1
     * sex : B
     * nickname : null
     * state : 0
     * vipstate : 1
     * password : 123456
     * integral : 0
     * coursename : null
     * vipendtime : null
     * vip : 0
     * vipintegral : 0
     * username : 1
     * schoolname : 上海外国语中学
     * email : null
     * uuid : 1
     * part : T  // T:老师 S:老师 J：家长
     * courseid : null
     * realname : ""
     * imgpath:""
     */

    private String schoolid;
    private String sex;
    private String nickname;
    private String state;
    private String vipstate;
    private String password;
    private int integral;
    private String coursename;
    private String vipendtime;
    private String vip;
    private int vipintegral;
    private String username;
    private String schoolname;
    private String email;
    private String uuid;
    private String part;
    private String courseid;
    private String realname;
    private String phone;
    private String xjh; //学籍号
    private String imgpath;

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVipstate() {
        return vipstate;
    }

    public void setVipstate(String vipstate) {
        this.vipstate = vipstate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getVipendtime() {
        return vipendtime;
    }

    public void setVipendtime(String vipendtime) {
        this.vipendtime = vipendtime;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public int getVipintegral() {
        return vipintegral;
    }

    public void setVipintegral(int vipintegral) {
        this.vipintegral = vipintegral;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getXjh() {
        return xjh;
    }

    public void setXjh(String xjh) {
        this.xjh = xjh;
    }

    public String getImgpath() {
        return ConnectUrl.PICURL + imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }
}
