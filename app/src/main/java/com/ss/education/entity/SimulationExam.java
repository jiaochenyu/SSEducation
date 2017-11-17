package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/11/16.
 * 说明：首页真题模拟
 */

public class SimulationExam implements Serializable {
    String file = "";
    String name = "";
    String time = "";

    public SimulationExam() {
    }

    public SimulationExam(String file, String name, String time) {
        this.file = file;
        this.name = name;
        this.time = time;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
