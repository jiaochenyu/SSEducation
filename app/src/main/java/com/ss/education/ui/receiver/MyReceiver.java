package com.ss.education.ui.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.shcyd.lib.base.BaseAppManager;
import com.ss.education.MainActivity;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.HomeWorkInfo;
import com.ss.education.ui.activity.classes.ClassDetailActivity;
import com.ss.education.ui.activity.classes.TeacherClassesShenheActivity;
import com.ss.education.ui.activity.classes.homework.HomeWorkDetailActivity;
import com.ss.education.ui.activity.classes.homework.HomeWorkListActivity;
import com.ss.education.ui.activity.login.LoginActivity;
import com.ss.education.ui.activity.parent.ShenheSPActivity;
import com.ss.education.utils.JPushUtils;
import com.ss.education.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();


            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                //下线 // 跳转到班级 --- 一系列操作
                option(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);


            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                goAppointPage(context, bundle);
                //打开自定义的Activity
//                Intent i = new Intent(context, MainActivity.class);
//                i.putExtras(bundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    /**
     * 跳转到指定页面
     *
     * @param context
     * @param bundle
     */
    private void goAppointPage(Context context, Bundle bundle) {

        if (MyApplication.getUser() == null) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } else {
            try {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                String type = json.getString("type");
                if (type.equals(Constant.TEACHER_CLASS_SHENHE)) {
                    String classid = json.getString("classid");
                    String classbh = json.getString("classbh");
                    Bundle b = new Bundle();
                    b.putString("classid", classid);
                    b.putString("classbh", classbh);
                    Intent intent = new Intent(context, TeacherClassesShenheActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
                if (type.equals(Constant.JZORXS_BANGDING_SHENHE)) {
                    Intent intent = new Intent(context, ShenheSPActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                if (type.equals(Constant.STDENT_CLASS_XIANGQING)) {  //详情
                    String classid = json.getString("classid");
                    String classbh = json.getString("classbh");
                    Bundle b = new Bundle();
                    b.putString("classid", classid);
                    b.putString("classbh", classbh);
                    Intent intent = new Intent(context, ClassDetailActivity.class);
                    intent.putExtras(b);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                if (type.equals(Constant.STDENT_CLASS_LIEBIAO)) {  //班级列表
                    Bundle bm = new Bundle();
                    bm.putInt("page", MainActivity.CLASS);
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                if (type.equals(Constant.STUDENT_WORK_LIEBIAO)) {  //作业列表
                    String classid = json.getString("classid");
                    Bundle bm = new Bundle();
                    bm.putString("classid", classid);
                    Intent intent = new Intent(context, HomeWorkListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                if (type.equals(Constant.STDENT_CLASS_WORK_JIEXI)) {  //
                    String obj = json.getString("object");
//                    JSONObject  object = new JSONObject(obj);
                    HomeWorkInfo workInfo = new Gson().fromJson(obj, HomeWorkInfo.class);
                    Bundle bm = new Bundle();
                    bm.putSerializable("homeDetail", workInfo);
                    Intent intent = new Intent(context, HomeWorkDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 一系列操作
     */
    private void option(final Context context, Bundle bundle) {
        try {
            JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String registrationid = json.getString("registrationid");
            String time = json.getString("time");
            String type = json.getString("type");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

//            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
//            Log.e("极光内容title",title);
            Log.e("极光内容registrationid", registrationid);
            Log.e("极光内容time", time);
//            Log.e("极光内容alert",alert);

            if (type.contains(Constant.LOGOUT)) {
                RongIM.getInstance().logout();
                JPushUtils.jPushMethod(context, "", null);
                SPUtils.put(context, "password", "");
                SPUtils.put(context, "username", "");
                MyApplication.setUser(null);

                //显示弹框
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("下线通知");
                builder.setMessage(message);
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        BaseAppManager.getInstance().clear();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                AlertDialog alterDialog = builder.create();
                alterDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alterDialog.show();


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkeyextre:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {

    }
}
