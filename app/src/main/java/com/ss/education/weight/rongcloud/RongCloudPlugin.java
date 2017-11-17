package com.ss.education.weight.rongcloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by JCY on 2017/10/23.
 * 说明：  融云会话界面
 *    “+”  号扩展区域自定义
 */

public class RongCloudPlugin implements IPluginModule{
    @Override
    public Drawable obtainDrawable(Context context) {
        return null;
    }

    @Override
    public String obtainTitle(Context context) {
        return null;
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {

    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
