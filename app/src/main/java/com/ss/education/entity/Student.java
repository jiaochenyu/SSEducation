package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/25.
 * 说明：
 */

public class Student implements Serializable {

    /**
     * schoolid : 1
     * sex : B
     * nickname : 小学生
     * state : 0
     * vipstate : 1
     * password : 123456
     * integral : 0
     * createtime : 2017-08-18 15:57:05.876
     * vip : 0
     * vipintegral : 0
     * username : 12345
     * schoolname : 上海外国语中学
     * realname : 小学僧
     * uuid : 2
     * part : S
     * jiazhangname：sdf
     * jiazhanguuid：sdf
     * jiazhangphone:sdf
     */

    private String schoolid;
    private String sex;
    private String nickname;
    private String state;
    private String vipstate;
    private String password;
    private int integral;
    private String createtime;
    private int vip;
    private int vipintegral;
    private String username;
    private String schoolname;
    private String realname;
    private String uuid;
    private String part;
    private String imgpath;
    private boolean isCheck;
    private Parent mParent;
    private String phone;
    private String xjh;
    private String groupname;
    private String groupid;

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

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Parent getParent() {
        return mParent;
    }

    public String getXjh() {
        return xjh;
    }

    public void setXjh(String xjh) {
        this.xjh = xjh;
    }

    public void setParent(Parent parent) {
        mParent = parent;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
