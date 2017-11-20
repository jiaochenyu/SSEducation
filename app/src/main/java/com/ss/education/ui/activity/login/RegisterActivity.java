package com.ss.education.ui.activity.login;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shcyd.lib.utils.RegexUtils;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.AddressPicker2;
import cn.qqtheme.framework.util.AssetsUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.phone)
    EditText mPhoneET;
    @Bind(R.id.code_edit)
    EditText mCodeET;
    @Bind(R.id.btn_yanzheng)
    TextView mYanzhengTV;
    @Bind(R.id.password_edit)
    EditText mPassEdit;
    @Bind(R.id.radiogroup)
    RadioGroup mRadiogroup;
    String role = "S";
    @Bind(R.id.confirm_password_edit)
    EditText mConfirmPasswordEdit;
    @Bind(R.id.realname_edit)
    EditText mRealnameEdit;
    @Bind(R.id.invitation_code_edit)
    EditText mInvitationCodeEdit;
    @Bind(R.id.invitation_ll)
    LinearLayout mInvitaionLL;

    String code = "";
    String phone = "";
    String pass = "";
    String comfirmPass = "";
    String mRealname = "";
    String mInvitaionCode = "";
    @Bind(R.id.address_text)
    TextView mAddressTV;


    String provinceS = "山东省";
    String cityS = "济南市";
    private RequestQueue mQueue = NoHttp.newRequestQueue();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        initListener();
    }


    private void initView() {
        mTitle.setText(R.string.register);
        mAddressTV.setText(provinceS + " " + cityS);
    }

    @OnClick({R.id.back, R.id.btn_yanzheng, R.id.next_step, R.id.address_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_yanzheng:

                break;
            case R.id.next_step:
                judgeData();
//
                break;
            case R.id.address_layout:
                addressPickup();
                break;
        }

    }


    private void initListener() {
        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.teacher:
                        role = "T";
                        mInvitaionLL.setVisibility(View.VISIBLE);
                        break;
                    case R.id.student:
                        role = "S";
                        mInvitaionLL.setVisibility(View.GONE);
                        break;
                    case R.id.parent:
                        role = "J";
                        mInvitaionLL.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void judgeData() {
        phone = mPhoneET.getText().toString();
        pass = mPassEdit.getText().toString();
        mRealname = mRealnameEdit.getText().toString();
        comfirmPass = mConfirmPasswordEdit.getText().toString();
        mInvitaionCode = mInvitationCodeEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast(getString(R.string.toast_phone_notnull));
            return;
        }
        if (!RegexUtils.isMobileExact(phone)) {
            showToast(getString(R.string.toast_phone_yes));
            return;
        }
        if (TextUtils.isEmpty(mRealname)) {
            showToast(getString(R.string.toast_realname_yes));
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            showToast(getString(R.string.toast_pass_notnull));
            return;
        }
        if (pass.length() < 6) {
            showToast(getString(R.string.toast_pass_six));
            return;
        }
        if (!comfirmPass.equals(pass)) {
            showToast(getString(R.string.toast_pass_notseem));
            return;
        }
        if (role.equals("T")) {
            if (TextUtils.isEmpty(mInvitaionCode)) {
                showToast("请输入邀请码");
                return;
            }

        }
        if (TextUtils.isEmpty(provinceS) && TextUtils.isEmpty(cityS)) {
            showToast("请选择地区");
            return;
        }
        httpRegister();
    }

    private void addressPickup() {
        ArrayList<AddressPicker2.Province> data = new ArrayList<AddressPicker2.Province>();
        String json = AssetsUtils.readText(this, "city.json");
        List<AddressPicker2.Province> provinces = new Gson().fromJson(json, new TypeToken<List<AddressPicker2.Province>>() {
        }.getType());
        data.addAll(provinces);
        AddressPicker2 picker = new AddressPicker2(this, data);
        picker.setSelectedItem(provinceS, cityS);
        picker.setOnAddressPickListener(new AddressPicker2.OnAddressPickListener() {
            @Override
            public void onAddressPicked(String province, String city) {
                provinceS = province;
                cityS = city;
                mAddressTV.setText(provinceS + " " + cityS);
            }
        });
        picker.show();
    }


    private void httpRegister() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.REGISTER, RequestMethod.POST);
//        request.addHeader("Content-Type","multipart/form-data;  boundary=ABCD");
        request.add("username", phone);
        request.add("password", pass);
        request.add("part", role);
        request.add("realname", mRealname);
        request.add("province", provinceS);
        request.add("city", cityS);
        request.add("invitationCode", mInvitaionCode);

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
                            showToast(object.getString("msg"));
                            httpLogin();
                        } else {
                            showToast(object.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                hideLoading();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    private void httpLogin() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.LOGIN, RequestMethod.POST);
//        request.addHeader("Content-Type","multipart/form-data;  boundary=ABCD");
        request.add("username", phone);
        request.add("password", pass);
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
                            SPUtils.put(RegisterActivity.this, "username", phone);
                            SPUtils.put(RegisterActivity.this, "password", pass);
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
                hideLoading();
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
        request.add("portraitUri", MyApplication.getUser().getImgpath());
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
                    try {
                        if (object.getString("code").equals("200")) {
//                            showToast(object.getString("token"));
                            connectRongCloud(object.getString("token"));
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

        return "";
    }

    ///连接融云
    private void connectRongCloud(String token) {
        showLoading();
        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                            2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    hideLoading();
//                    showToast("融云连接成功");
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
        }
    }


}
