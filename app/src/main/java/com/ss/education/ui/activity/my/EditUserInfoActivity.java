package com.ss.education.ui.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shcyd.lib.utils.RegexUtils;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.User;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditUserInfoActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mRightText;
    @BindView(R.id.name_edit)
    EditText mNameEdit;
    @BindView(R.id.email_edit)
    EditText mEmailEdit;
    @BindView(R.id.phone_edit)
    EditText mPhoneEdit;
    @BindView(R.id.school_tv)
    TextView mSchoolTV;

    String name = "";
    String email = "";
    String phone = "";
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitle.setText("编辑信息");
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("完成");
        mNameEdit.setText(MyApplication.getUser().getRealname());
        mEmailEdit.setText(MyApplication.getUser().getEmail());
        mPhoneEdit.setText(MyApplication.getUser().getPhone());
        mSchoolTV.setText(MyApplication.getUser().getSchoolname());


    }

    @OnClick({R.id.back, R.id.right_text, R.id.update_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                juge();
                break;
            case R.id.update_pass:

                break;
        }
    }

    private void juge() {
        phone = mPhoneEdit.getText().toString();
        email = mEmailEdit.getText().toString();
        name = mNameEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入姓名");
            return;
        }
        if (!TextUtils.isEmpty(email) && !RegexUtils.isEmail(email)) {
            showToast("请输入正确的邮箱");
            return;
        }
        if (!TextUtils.isEmpty(phone) && !RegexUtils.isMobileExact(phone)) {
            showToast("请输入正确的电话号码");
            return;
        }
        httpUpdateInfo();
    }


    //修改
    private void httpUpdateInfo() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPDATE_USER_INFO, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("realname", name);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("useruuid", MyApplication.getUser().getUuid());
        request.add("data", jsonObject.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        String status = object.getString("status");
                        if (status.equals("100")) {
                            showToast(object.getString("msg"));
                            User u = MyApplication.getUser();
                            u.setEmail(email);
                            u.setPhone(phone);
                            u.setRealname(name);
                            MyApplication.setUser(u);

                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateUserInfo, ""));


                            finish();

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

            }

            @Override
            public void onFinish(int what) {
                hideLoading();
            }


        });

    }
}
