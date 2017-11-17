package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/10/27.
 * 说明：
 */

public class RecordModel implements Serializable {
    public int times;
    public String formatTimes;
    public String path;
    public boolean isPlaying;
    //标记当前用户是否读过, false:未读  true：已读
    public boolean isReaded;

    public RecordModel() {
    }

    public RecordModel(int times, String path) {
        this.times = times;
        this.path = path;
    }
}
