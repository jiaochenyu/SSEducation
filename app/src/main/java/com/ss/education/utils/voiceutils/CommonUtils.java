package com.ss.education.utils.voiceutils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by timor.fan on 2016/8/8.
 * *项目名：CZF
 * 类描述：
 */
public class CommonUtils {

    public static void setLanguage(Context context, Locale locale){
        Resources resources=context.getResources();
        DisplayMetrics dm=resources.getDisplayMetrics();
        Configuration config=resources.getConfiguration();
        config.locale= locale;
        locale.getLanguage();
        resources.updateConfiguration(config,dm);
    }

    public static void setFontSize(Context context,  float fontScale){
        Resources resources=context.getResources();
        DisplayMetrics dm=resources.getDisplayMetrics();
        Configuration config=resources.getConfiguration();
        config.fontScale=fontScale;
        resources.updateConfiguration(config,dm);
    }

}
