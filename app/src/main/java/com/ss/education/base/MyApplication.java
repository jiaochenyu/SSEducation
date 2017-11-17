package com.ss.education.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ss.education.entity.User;
import com.ss.education.utils.SPUtils;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by jcy on 2017/6/12.
 */
public class MyApplication extends Application {
    private static User mUser;
    private static MyApplication inst;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private List<User> mUsers = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        inst = MyApplication.this;
        InitializationConfig config = InitializationConfig.newBuilder(this)
//                .addHeader("Content-Type", "multipart/form-data; boundary=-----------------------------264141203718551") // 全局请求头。
                .connectionTimeout(30 * 1000)
                .readTimeout(30 * 1000)
                .retry(10)
                .build();
        NoHttp.initialize(config);
        Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpSample");
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
        RongIM.init(this);
        initRongyunUsers();
        RongIM.getInstance().setMessageAttachedUserInfo(true);
//        httpGetAllUser();
//        RongIM.getInstance().registerConversationTemplate(new MyPrivateConversationProvider());
    }

    public static MyApplication getInst() {
        return inst;
    }

    public static User getUser() {
//        User user = (User) SPUtils.get(MyApplication.getInst(), "userInfor", User.class);
        User user = (User) SPUtils.get(MyApplication.getInst(), "userInfo", new TypeToken<User>() {
        }.getType());
        return user;
    }

    public static void setUser(User user) {
        SPUtils.put(MyApplication.getInst(), "userInfo", user);
        mUser = user;
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    private void httpGetAllUser() {
        final UserInfo userInfo = null;
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.FIND_ALL_USER_RONG_CLOUD, RequestMethod.POST);
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Log.d("login", "onSucceed: " + response.get().toString());
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        if (object.getString("status").equals("100")) {
                            JSONArray array = object.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                User user = gson.fromJson(array.getJSONObject(i).toString(), User.class);
                                mUsers.add(user);
                            }


                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {


            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void initRongyunUsers() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                mUsers = httpGetAllUsersSync();
                if (mUsers != null && mUsers.size() > 0) {
                    for (User u : mUsers) {
                        if (u.getUuid().equals(userId)) {
                            return new UserInfo(u.getUuid(), u.getRealname(), Uri.parse(u.getImgpath()));
                        }
                    }
                }
                return null;

            }
        }, false);
    }

    //同步请求
    private List<User> httpGetAllUsersSync() {
        List<User> uList = new ArrayList<>();
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.FIND_ALL_USER_RONG_CLOUD, RequestMethod.POST);
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.isSucceed()) {
            // 请求成功
            JSONObject object = response.get();
            Gson gson = new Gson();
            try {
                if (object.getString("status").equals("100")) {
                    JSONArray array = object.getJSONArray("array");
                    for (int i = 0; i < array.length(); i++) {
                        User user = gson.fromJson(array.getJSONObject(i).toString(), User.class);
                        uList.add(user);
                    }
                    return uList;

                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 请求失败
        }
        return null;
    }


}
