package com.ss.education.ui.activity.parent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.User;
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

public class SearchParentAndStuActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.edit_phone)
    EditText mEditPhone;
    @Bind(R.id.submit_btn)
    TextView mSubmitBtn;
    @Bind(R.id.text_name)
    TextView mTextName;
    @Bind(R.id.name)
    TextView mNameTV;
    @Bind(R.id.text_phone)
    TextView mTextPhone;
    @Bind(R.id.phone)
    TextView mPhoneTV;
    @Bind(R.id.result_layout)
    LinearLayout mResultLayout;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_parent_and_stu);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitle.setText("查找");
        if (MyApplication.getUser().getPart().equals("S")) {
            mEditPhone.setHint("请输入家长手机号");
            mSubmitBtn.setText("搜索家长");
        } else {
            mEditPhone.setHint("请输入学生账号");
            mSubmitBtn.setText("搜索学生");
        }
    }

    private void httpSearch() {
        String phone = mEditPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入搜索内容");
            return;
        }
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.SEARCH_PARENT_OR_STUDNET, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", phone);
            jsonObject.put("part", MyApplication.getUser().getPart().equals("S") ? "J" : "S");
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
                            JSONObject json = object.getJSONObject("object");
                            mUser = gson.fromJson(json.toString(), User.class);
                        } else {
                            showToast("用户不存在!");
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
                setView();
            }


        });

    }

    private void setView() {
        if (mUser != null) {
            mResultLayout.setVisibility(View.VISIBLE);
            if (MyApplication.getUser().getPart().equals("S")) {
                mTextName.setText("家长姓名：");
                mTextPhone.setText("家长号码：");
                mNameTV.setText(  mUser.getRealname());
                mPhoneTV.setText(mUser.getUsername());
            } else if (MyApplication.getUser().getPart().equals("J")) {
                mTextName.setText("学生姓名：");
                mTextPhone.setText("学生编号：");
                mNameTV.setText( mUser.getRealname());
                mPhoneTV.setText( mUser.getUsername());
            }

        }
    }


    //申请加入班级
    private void httpBind() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.APPLY_BIND_STU_PAR, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", mUser.getUuid());
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
                            showToast("申请成功，请等待审核");
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

    @OnClick({R.id.back, R.id.submit_btn, R.id.bind_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit_btn:
                httpSearch();
                break;
            case R.id.bind_btn:
                httpBind();
                break;
        }
    }
}
