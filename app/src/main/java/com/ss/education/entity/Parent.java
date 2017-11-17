package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/10/17.
 * 说明：家长
 */

public class Parent implements Serializable {


    /**
     * schoolid : bf514d9db6034d4c94fc9b317960a643
     * sex : B
     * state : 0
     * vipstate : 1
     * password : 123456
     * integral : 0
     * createtime : 2017-10-17 13:15:28.589
     * vip : 0
     * username : 111111
     * vipintegral : 0
     * imgpath :
     * schoolname : 森盛科技
     * realname : 家长
     * uuid : c58fd6e94e8743e49d231f70f82cf25b
     * part : J
     */

    private String schoolid;
    private String sex;
    private String state;
    private String vipstate;
    private String password;
    private int integral;
    private String createtime;
    private int vip;
    private String username;
    private int vipintegral;
    private String imgpath;
    private String schoolname;
    private String realname;
    private String uuid;
    private String part;
    private String phone;
    private String email;

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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVipintegral() {
        return vipintegral;
    }

    public void setVipintegral(int vipintegral) {
        this.vipintegral = vipintegral;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
