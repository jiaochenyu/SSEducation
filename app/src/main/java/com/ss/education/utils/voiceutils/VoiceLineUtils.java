package com.ss.education.utils.voiceutils;

import android.content.Context;

import com.ss.education.utils.DensityUtil;


/**
 * Created by wgyscsf on 2016/8/30.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class VoiceLineUtils {
    //    默认长度：90,总长度：220；剩余长度：130;剩余时长：54s;剩余增量：130/54=
    //
    //根据时间长短计算语音条宽度
    public synchronized static int getVoiceLineWight(Context context, int seconds) {

        int x = seconds;// 声音的持续秒数
        int length = (int) (70 + 150 * ((x - 1.0) / (x + 5)));// 计算气泡的长度  12是最短值    140是最长与最短的差值

//        if (seconds <= 6) {
//            return DensityUtil.dip2px(context, 90);
//        } else {
//            return DensityUtil.dip2px(context, seconds <= 60 ? 90 + seconds * (130 / 54) : 220);
//        }
        return DensityUtil.dip2px(context, length);
    }
}
