package com.ss.education.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ss.education.entity.WpsModel;

/**
 * Created by JCY on 2017/11/1.
 * 说明：
 */

public class WpsBroadCastReciver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WpsModel.Reciver.ACTION_BACK://返回键广播
                System.out.println(WpsModel.Reciver.ACTION_BACK);
                break;
            case WpsModel.Reciver.ACTION_CLOSE://关闭文件时候的广播
                System.out.println(WpsModel.Reciver.ACTION_CLOSE);

                break;
            case WpsModel.Reciver.ACTION_HOME://home键广播
                System.out.println(WpsModel.Reciver.ACTION_HOME);

                break;
            case WpsModel.Reciver.ACTION_SAVE://保存广播
                System.out.println(WpsModel.Reciver.ACTION_SAVE);

                break;
            default:
                break;
        }

    }

}