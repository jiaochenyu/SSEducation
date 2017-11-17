package com.ss.education.utils;

import android.app.Notification;
import android.content.Context;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by JCY on 2017/8/10.
 * 说明：
 */

public class JPushUtils {
    /**
     * 注册
     *
     * @param context
     * @param alias
     * @param tags
     */
    public static void jPushMethod(Context context, String alias, Set<String> tags) {
        JPushInterface.init(context);
        JPushInterface.resumePush(context);
        JPushInterface.setAlias(context, alias, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = R.mipmap.logos;
//        builder.notificationDefaults = Notification.DEFAULT_VIBRATE;//震动
        builder.notificationFlags = Notification.FLAG_SHOW_LIGHTS; //闪烁灯
        JPushInterface.setPushNotificationBuilder(1, builder);

        JPushInterface.setTags(context, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
            }
        });
    }

      public static String getRegistrationID(Context context) {
        return JPushInterface.getRegistrationID(context);
    }
}
