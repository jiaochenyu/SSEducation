package com.ss.education.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JCY on 2017/11/3.
 * 说明：
 */

public class DateUtils {
    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = time + " 12:00:00";
        try {
            Date date = format.parse(time);
            return date.getTime();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

}
