package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by jcy on 2017/5/15.
 */


public class EventFlag implements Serializable {
    String flag;
    int position;
    Object mObject;

    public EventFlag() {
    }

    public EventFlag(String flag) {
        this.flag = flag;
    }

    public EventFlag(String flag, Object object) {
        this.flag = flag;
        this.mObject = object;
    }

    public EventFlag(String flag, int position) {
        this.flag = flag;
        this.position = position;
    }


    public EventFlag(String flag, Object object, int position) {
        this.flag = flag;
        this.position = position;
        mObject = object;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }
}
