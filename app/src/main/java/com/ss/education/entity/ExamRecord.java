package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/9/25.
 * 说明： 答题记录
 */

public class ExamRecord implements Serializable {

    /**
     * createtime : 2017-09-22 13:53:38.333
     * times : 8497
     * uuid : 59d6657765e343058214a537bafdbd77
     * courseid : 4
     * accuracy : 0.0%
     * ztmainname : 物理练习
     * coursename : 物理
     * remarks //评语
     */

    private String createtime;
    private long times;
    private String uuid;
    private String courseid;
    private String accuracy;
    private String ztmainname;
    private String coursename;
    private String remarks;
    private String teachername;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getZtmainname() {
        return ztmainname;
    }

    public void setZtmainname(String ztmainname) {
        this.ztmainname = ztmainname;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }
}
