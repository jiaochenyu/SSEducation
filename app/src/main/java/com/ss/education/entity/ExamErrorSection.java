package com.ss.education.entity;

/**
 * Created by JCY on 2017/9/27.
 * 说明：
 */

public class ExamErrorSection {

    /**
     * sectionname : 力学
     * seq : 0
     * uuid : 5
     * courseid : 4
     * coursename : 物理
     */

    private String sectionname;
    private int seq;
    private String uuid;
    private String courseid;
    private String coursename;

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }
}
