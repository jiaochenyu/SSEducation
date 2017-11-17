package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/20.
 * 说明： 答题时候的 每一道题的记录 ，并且将该实体类的集合上传到后台
 */

public class ExamResult implements Serializable {
    private String userid;
    private String courseid;
    private String coursename;
    private String sectionid;
    private String sectionname;
    private String quizid;//试题id （uuid）
    private String content; // 题目
    private String answer; //JSONArray
    private String rightanswer;//正确答案
    private String myanswer;//我的答案
    private int whether;//是否正确 0 否，1 是
    private long times; //每一题的时间


    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getSectionid() {
        return sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getQuizid() {
        return quizid;
    }

    public void setQuizid(String quizid) {
        this.quizid = quizid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRightanswer() {
        return rightanswer;
    }

    public void setRightanswer(String rightanswer) {
        this.rightanswer = rightanswer;
    }

    public String getMyanswer() {
        return myanswer;
    }

    public void setMyanswer(String myanswer) {
        this.myanswer = myanswer;
    }

    public int getWhether() {
        return whether;
    }

    public void setWhether(int whether) {
        this.whether = whether;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
