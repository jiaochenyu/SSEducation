package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/10/25.
 * 说明：
 */

public class StudentGroup implements Serializable {
    private String groupname;
    private String groupid;
    private boolean isChecked;
    private List<Student> mStudents;

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<Student> getStudents() {
        return mStudents;
    }

    public void setStudents(List<Student> students) {
        mStudents = students;
    }
}
