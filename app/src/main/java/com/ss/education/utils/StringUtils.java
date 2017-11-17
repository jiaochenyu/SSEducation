package com.ss.education.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by JCY on 2017/9/20.
 * 说明：
 */

public class StringUtils {
    /*
   *   字符串按照顺序转换
   *   例如： 1-3-2       转换为 1-2-3
   * */
    public static String getSortString(String ranswer) {
        String[] strary = ranswer.split("-");
        int[] inary = new int[strary.length];
        for (int i = 0; i < strary.length; i++) {
            inary[i] = Integer.valueOf(strary[i].trim());
        }
        Arrays.sort(inary);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inary.length; i++) {
            sb.append(inary[i] + "-");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 将 1-2 转换成 A、B
     *
     * @param a
     * @return
     */
    public static String getLetterAZ(String a) {
        String letter = "";
        if (!a.equals("null") && !TextUtils.isEmpty(a)) {
            if (a.contains("-")) {
                String[] b = a.split("-");
                for (int i = 0; i < b.length; i++) {
                    int x = Integer.parseInt(b[i]);
                    letter += changeAZ(x) + "、";
                }
                letter = letter.substring(0, letter.length() - 1);
            } else {
                letter = changeAZ(Integer.parseInt(a));
            }
        } else {
            letter = "空";
        }

        return letter;
    }

    private static String changeAZ(int x) {
        switch (x) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
        }
        return "";
    }

    /*
    * 传入int数字    返回string时间
    * 例如：60        -->   01:00
    *
    * */
    public static String getTime(long time) {
        time = time / 1000;
        long m = time / 60;
        long s = time % 60;
        String ma = m + "";
        String sa = s + "";
        if (m < 10) {
            ma = "0" + ma;
        }
        if (s < 10) {
            sa = "0" + sa;
        }
        return "" + ma + ":" + sa;
    }

    /**
     * 保留一位小数
     *
     * @param a
     * @return
     */
    public static String format(float a) {
        DecimalFormat df = new DecimalFormat("0.0");//格式化小数
        String num = df.format(a);//返回的是String类型
        return num;
    }
}
