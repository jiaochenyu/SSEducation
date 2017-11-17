package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/10/31.
 * 说明：
 */

public class HomeWorkInfo implements Serializable {


    /**
     * fbr : 1a5af64ce0b04ec49382515b98715b52
     * remarks : 备注说明
     * receivetype : B
     * analysis : 解析说明
     * analysisfile : [{"file":"upload/c43e249cb08c4d7e8d8726aa6a54cedf.jpg","oldname":"小胖子.jpg"},{"file":"upload/3038f267fba543b98980b421f60c2de7.jpg","oldname":"小明.jpg"}]
     * fbrname : 赵老师
     * createtime : 2017-10-27 10:58:13.519
     * workfile : [{"file":"upload/661597e38cea44f7bdda09844dc754f9.jpg","oldname":"玫瑰.jpg"},{"file":"upload/a30e9edfb32943c8af37ead0b231df01.jpg","oldname":"小狗.jpg"}]
     * titles : 发布作业标题
     * receives : [{"uuid":"d8b20711c6b845cd9f49cc1693cc40fd"}]
     * uuid : a304225073564764b8b51c70286b4e9d
     * showis : T
     * classid : d8b20711c6b845cd9f49cc1693cc40fd
     * submitis : T
     * jztimes:"2017-11-08 12:00:00.0",
     */

    private String fbr;
    private String remarks;
    private String receivetype;
    private String analysis;
    private String fbrname;
    private String createtime;
    private String titles;
    private String uuid;
    private String showis;
    private String classid;
    private String submitis;
    private String jztimes; //截止日期
    private List<AnalysisfileBean> analysisfile;
    private List<WorkfileBean> workfile;
    private List<ReceivesBean> receives;

    public String getFbr() {
        return fbr;
    }

    public void setFbr(String fbr) {
        this.fbr = fbr;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReceivetype() {
        return receivetype;
    }

    public void setReceivetype(String receivetype) {
        this.receivetype = receivetype;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getFbrname() {
        return fbrname;
    }

    public void setFbrname(String fbrname) {
        this.fbrname = fbrname;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getShowis() {
        return showis;
    }

    public void setShowis(String showis) {
        this.showis = showis;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getSubmitis() {
        return submitis;
    }

    public void setSubmitis(String submitis) {
        this.submitis = submitis;
    }

    public String getJztimes() {
        return jztimes;
    }

    public void setJztimes(String jztimes) {
        this.jztimes = jztimes;
    }

    public List<AnalysisfileBean> getAnalysisfile() {
        return analysisfile;
    }

    public void setAnalysisfile(List<AnalysisfileBean> analysisfile) {
        this.analysisfile = analysisfile;
    }

    public List<WorkfileBean> getWorkfile() {
        return workfile;
    }

    public void setWorkfile(List<WorkfileBean> workfile) {
        this.workfile = workfile;
    }

    public List<ReceivesBean> getReceives() {
        return receives;
    }

    public void setReceives(List<ReceivesBean> receives) {
        this.receives = receives;
    }

    public static class AnalysisfileBean implements Serializable {
        /**
         * file : upload/c43e249cb08c4d7e8d8726aa6a54cedf.jpg
         * oldname : 小胖子.jpg
         */

        private String file;
        private String oldname;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getOldname() {
            return oldname;
        }

        public void setOldname(String oldname) {
            this.oldname = oldname;
        }
    }

    public static class WorkfileBean implements Serializable {
        /**
         * file : upload/661597e38cea44f7bdda09844dc754f9.jpg
         * oldname : 玫瑰.jpg
         */

        private String file;
        private String oldname;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getOldname() {
            return oldname;
        }

        public void setOldname(String oldname) {
            this.oldname = oldname;
        }
    }

    public static class ReceivesBean implements Serializable{
        /**
         * uuid : d8b20711c6b845cd9f49cc1693cc40fd
         */

        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
