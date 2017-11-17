package com.ss.education.utils;

/**
 * Created by JCY on 2017/9/20.
 * 说明： 1-10 转化成ABCDEF
 */

public class AZChange {

    //考试选项顺序 A-Z 转换
    public static String getLetter(int a) {
        String letter = "";
        switch (a) {
            case 0:
                letter = "A";
                break;
            case 1:
                letter = "B";
                break;
            case 2:
                letter = "C";
                break;
            case 3:
                letter = "D";
                break;
            case 4:
                letter = "E";
                break;
            case 5:
                letter = "F";
                break;
            case 6:
                letter = "G";
                break;
            case 7:
                letter = "H";
                break;
            case 8:
                letter = "I";
                break;
            case 9:
                letter = "J";
                break;
        }

        return letter;
    }
}
