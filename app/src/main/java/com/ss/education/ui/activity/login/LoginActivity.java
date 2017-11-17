package com.ss.education.ui.activity.login;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ss.education.MainActivity;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.User;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.username)
    EditText mUsernameET;
    @Bind(R.id.password)
    EditText mPasswordET;
    @Bind(R.id.cha)
    ImageView mChaIV;
    private String username;
    private String password;

    RequestQueue mQueue = NoHttp.newRequestQueue();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initView() {

    }


    private void initData() {
        username = (String) SPUtils.get(LoginActivity.this, "username", String.class);
        password = (String) SPUtils.get(LoginActivity.this, "password", String.class);
        if (username != null && password != null) {
            mUsernameET.setText(username);
            mPasswordET.setText(password);
        }
        if (!TextUtils.isEmpty(username)) {
            mChaIV.setVisibility(View.VISIBLE);
        } else {
            mChaIV.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mUsernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (TextUtils.isEmpty(str)) {
                    mChaIV.setVisibility(View.GONE);
                } else {
                    mChaIV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.login, R.id.register, R.id.forget_password, R.id.cha})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                judgeData();
                break;
            case R.id.register:
                go(RegisterActivity.class);
                break;
            case R.id.forget_password:
                go(ForgetPassActivity.class);
                break;
            case R.id.cha:
                mUsernameET.setText("");
                mPasswordET.setText("");
                break;
        }
    }

    private void judgeData() {
        username = mUsernameET.getText().toString();
        password = mPasswordET.getText().toString();
        if (TextUtils.isEmpty(username)) {
            showToast(getString(R.string.toast_username_notnull));
            go(MainActivity.class);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.toast_pass_notnull));
            return;
        }
        if (password.length() < 6) {
            showToast(getString(R.string.toast_pass_six));
            return;
        }
        httpLogin();
    }

    private void httpLogin() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.LOGIN, RequestMethod.POST);
//        request.addHeader("Content-Type","multipart/form-data;  boundary=ABCD");
        request.add("username", username);
        request.add("password", password);
        request.add("registrationid", JPushUtils.getRegistrationID(this));
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Log.d("login", "onSucceed: " + response.get().toString());
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        if (object.getString("status").equals("100")) {
//                            showToast(object.getString("msg"));
                            SPUtils.put(LoginActivity.this, "username", username);
                            SPUtils.put(LoginActivity.this, "password", password);
                            User user = gson.fromJson(object.getJSONObject("user").toString(), User.class);
                            MyApplication.setUser(user);
//                            goAndFinish(MainActivity.class);
                            getRongCloudToken();
                        } else {
                            hideLoading();
                            showToast(object.getString("msg"));
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
//                hideLoading();
//                RongIM.getInstance().refreshUserInfoCache(new UserInfo(MyApplication.getUser().getUuid(),MyApplication.getUser().getRealname(),Uri.parse("")));
            }
        });
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
        request.add("portraitUri  ", MyApplication.getUser().getImgpath());
        request.add("name", MyApplication.getUser().getRealname());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject object = response.get();
//                    showToast(response.get().toString());
                    try {
                        if (object.getString("code").equals("200")) {
//                            showToast(object.getString("token"));
                            connectRongCloud(object.getString("token"));
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
                hideLoading();
            }
        });

        return "";
    }

    ///连接融云
    private void connectRongCloud(String token) {
        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            showLoading();

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                            2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
//                    showToast("onTokenIncorrect");
                    getRongCloudToken();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {

                    hideLoading();
                    User u = MyApplication.getUser();
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(u.getUuid(), u.getRealname(), Uri.parse(ConnectUrl.IMAGEURL_HEADER)));
                    goAndFinish(MainActivity.class);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    hideLoading();
                    showToast("连接失败,请重新登录！");
                    goAndFinish(LoginActivity.class);
                }
            });
        } else {

        }
    }

    private void initRongyunUser() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                UserInfo userInfo = null;
                if (s.equals(MyApplication.getUser().getUuid())) {
                    userInfo = new UserInfo(MyApplication.getUser().getUuid(), MyApplication.getUser().getRealname(), Uri.parse(""));
                }
                return userInfo;
            }
        }, true);
    }
}
