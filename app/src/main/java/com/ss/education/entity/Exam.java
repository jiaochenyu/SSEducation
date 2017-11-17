package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/9/19.
 * 说明：
 */

public class Exam implements Serializable {

    /**
     * imgstate : 0
     * schoolid : 1
     * rightanswer : 3
     * sectionname : 第一章 宇宙与地球
     * answer : [{"id":"1","name":"恒星和行星"},{"id":"1","name":"恒星和行星"},{"id":"1","name":"恒星和行星"},{"id":"1","name":"恒星和行星"}]
     * issingle : 1   //0：多选 1单选
     * coursename : 地理
     * content : 宇宙的各类天体中最基本的是
     * schoolname : 上海外国语中学
     * imgpath : [{"url":"jjsjdj"}]
     * sectionid : 3
     * uuid : e2d7989906804dd1aeb9816901f9493a
     * courseid : 2,
     * remarks："解析"
     */

    private int imgstate;
    private String schoolid;
    private String rightanswer;
    private String sectionname;
    private int issingle;   //0：多选 1单选
    private String coursename;
    private String content;
    private String schoolname;
    private String sectionid;
    private String uuid;
    private String courseid;
    private List<AnswerBean> answer;
    private List<ImgpathBean> imgpath;
    private Long times;
    private String remarks;
    private int whether;
    private String myanswer;

    public int getImgstate() {
        return imgstate;
    }

    public void setImgstate(int imgstate) {
        this.imgstate = imgstate;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getRightanswer() {
        return rightanswer;
    }

    public void setRightanswer(String rightanswer) {
        this.rightanswer = rightanswer;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public int getIssingle() {
        return issingle;
    }

    public void setIssingle(int issingle) {
        this.issingle = issingle;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getSectionid() {
        return sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public List<AnswerBean> getAnswer() {
        return answer;
    }

    public void setAnswer(List<AnswerBean> answer) {
        this.answer = answer;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public List<ImgpathBean> getImgpath() {
        return imgpath;
    }

    public void setImgpath(List<ImgpathBean> imgpath) {
        this.imgpath = imgpath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getWhether() {
        return whether;
    }

    public void setWhether(int whether) {
        this.whether = whether;
    }

    public String getMyanswer() {
        return myanswer;
    }

    public void setMyanswer(String myanswer) {
        this.myanswer = myanswer;
    }

    public static class AnswerBean implements Serializable {
        /**
         * id : 1
         * name : 恒星和行星
         */

        private String id;
        private String name;
        private boolean isChecked; //是否选中

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class ImgpathBean implements Serializable {
        /**
         * path : jjsjdj
         */

        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
