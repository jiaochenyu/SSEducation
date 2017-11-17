package com.ss.education.ui.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.ss.education.MainActivity;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.User;
import com.ss.education.ui.activity.login.LoginActivity;
import com.ss.education.utils.GetRongCloudToken;
import com.ss.education.utils.JPushUtils;
import com.ss.education.utils.SPUtils;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class StartActivity extends BaseActivity {
    private String username;
    private String pass;
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        doIt();
//        sleep(MainActivity.class);
    }

    //判断是否免登录
    private void doIt() {
        pass = (String) SPUtils.get(StartActivity.this, "password", String.class);
        username = (String) SPUtils.get(StartActivity.this, "username", String.class);
        if (pass != null && username != null) {
            httpLogin();
        } else {
            sleep(LoginActivity.class);
        }
    }

    private void httpLogin() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.LOGIN, RequestMethod.POST);
        request.add("username", username);
        request.add("password", pass);
        request.add("registrationid", JPushUtils.getRegistrationID(this));
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        if (object.getString("status").equals("100")) {
//                            showToast(object.getString("msg"));
                            User user = gson.fromJson(object.getJSONObject("user").toString(), User.class);
                            MyApplication.setUser(user);
                            getRongCloudToken();

                        } else {
                            sleep(LoginActivity.class);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                sleep(LoginActivity.class);
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    public void sleep(final Class<? extends Activity> c) {
//        goAndFinish(c);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(500);
                    goAndFinish(c);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public String getRongCloudToken() {
        String token = "";
        String App_Key = "3argexb630loe"; //开发者平台分配的 App Key。
        String App_Secret = "Z1DrHhqGRm9f2";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳，从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。
        String nonce = String.valueOf(Math.floor(Math.random() * 1000000));//随机数，无长度限制。
        String signature = GetRongCloudToken.sha1(App_Secret + nonce + timestamp);//数据签名。
        Logger.i(signature);
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.RONGCLOUD, RequestMethod.POST);
        request.setHeader("App-Key", App_Key);
        request.setHeader("Timestamp", timestamp);
        request.setHeader("Nonce", nonce);
        request.setHeader("Signature", signature);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.add("userId", MyApplication.getUser().getUuid());
        request.add("username", MyApplication.getUser().getRealname());
        request.add("portraitUri", MyApplication.getUser().getImgpath());
        request.add("name", MyApplication.getUser().getRealname());

        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject object = response.get();
                    try {
                        if (object.getString("code").equals("200")) {
//                            showToast(object.getString("token"));
                            connectRongCloud(object.getString("token"));
                        } else {
                            goAndFinish(LoginActivity.class);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                goAndFinish(LoginActivity.class);
            }

            @Override
            public void onFinish(int what) {

            }
        });

        return "";
    }

    ///连接融云
    private void connectRongCloud(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                            2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.e("connect", "onTokenIncorrect");
                    getRongCloudToken();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
//                    showToast("融云连接成功");
                    User u = MyApplication.getUser();
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(u.getUuid(), u.getRealname(), Uri.parse(ConnectUrl.IMAGEURL_HEADER)));
                    sleep(MainActivity.class);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    showToast("连接失败,请重新登录！");
                    sleep(LoginActivity.class);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll();

    }
}
