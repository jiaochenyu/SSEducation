package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/11/9.
 * 说明：
 */

public class HomeworkFeedback implements Serializable {

    /**
     * feedback :
     * evaluate :
     * evaluateis :
     * workid : bb185ff73d824493a4c9057e387e4160
     * userid : 4a014a5279cf4c78847e0a3f08dc5f12
     * feedbackfile : [{"file":"upload/7568599f5dff485abdd40b4e6e5fb4ff.png","filename":"7568599f5dff485abdd40b4e6e5fb4ff.png","keyname":"analysisfile","oldname":"Screenshot_2017-11-01-13-14-04.png"}]
     * cztime : 2017-11-01 15:47:00.0
     * uuid : b065c6b6c54d473d8dcfe0c07c34b875
     * readis : F
     */

    private String feedback;
    private String evaluate;
    private String evaluateis;
    private String workid;
    private String userid;
    private String scztime; // 操作时间 // 学生提交作业时间
    private String tcztime; // 操作时间 // 老师评价时间
    private String uuid;
    private String readis;
    private List<FeedbackfileBean> feedbackfile;
    private List<EvaluatefileBean> evaluatefile;
    private Student mStudent;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getEvaluateis() {
        return evaluateis;
    }

    public void setEvaluateis(String evaluateis) {
        this.evaluateis = evaluateis;
    }

    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getScztime() {
        return scztime;
    }

    public void setScztime(String scztime) {
        this.scztime = scztime;
    }

    public String getTcztime() {
        return tcztime;
    }

    public void setTcztime(String tcztime) {
        this.tcztime = tcztime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReadis() {
        return readis;
    }

    public void setReadis(String readis) {
        this.readis = readis;
    }

    public List<FeedbackfileBean> getFeedbackfile() {
        return feedbackfile;
    }

    public List<EvaluatefileBean> getEvaluatefile() {
        return evaluatefile;
    }

    public void setEvaluatefile(List<EvaluatefileBean> evaluatefile) {
        this.evaluatefile = evaluatefile;
    }

    public void setFeedbackfile(List<FeedbackfileBean> feedbackfile) {
        this.feedbackfile = feedbackfile;

    }

    public Student getStudent() {
        return mStudent;
    }

    public void setStudent(Student student) {
        mStudent = student;
    }

    public static class FeedbackfileBean implements Serializable{
        /**
         * file : upload/7568599f5dff485abdd40b4e6e5fb4ff.png
         * filename : 7568599f5dff485abdd40b4e6e5fb4ff.png
         * keyname : analysisfile
         * oldname : Screenshot_2017-11-01-13-14-04.png
         */

        private String file;
        private String filename;
        private String keyname;
        private String oldname;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getKeyname() {
            return keyname;
        }

        public void setKeyname(String keyname) {
            this.keyname = keyname;
        }

        public String getOldname() {
            return oldname;
        }

        public void setOldname(String oldname) {
            this.oldname = oldname;
        }
    }

    public static class EvaluatefileBean implements Serializable{
        /**
         * file : upload/7568599f5dff485abdd40b4e6e5fb4ff.png
         * filename : 7568599f5dff485abdd40b4e6e5fb4ff.png
         * keyname : analysisfile
         * oldname : Screenshot_2017-11-01-13-14-04.png
         */

        private String file;
        private String filename;
        private String keyname;
        private String oldname;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getKeyname() {
            return keyname;
        }

        public void setKeyname(String keyname) {
            this.keyname = keyname;
        }

        public String getOldname() {
            return oldname;
        }

        public void setOldname(String oldname) {
            this.oldname = oldname;
        }
    }
}
