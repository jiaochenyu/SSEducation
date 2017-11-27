package com.ss.education.ui.activity.classes;

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
import com.ss.education.entity.Classes;
import com.ss.education.entity.EventFlag;
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

public class JoinClassActivity extends BaseActivity {

    @BindView(R.id.edit_class_code)
    EditText mEditClassCode;
    @BindView(R.id.name)
    TextView mNameTV;
    @BindView(R.id.content)
    TextView mCodeTV;
    @BindView(R.id.num)
    TextView mStudentNumTV;
    @BindView(R.id.shuoming)
    TextView mTeacherNameTV;
    @BindView(R.id.time)
    TextView mTimeTV;
    @BindView(R.id.result_layout)
    LinearLayout mResultLayout;
    @BindView(R.id.title)
    TextView mTitle;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private Classes mClasses;
    private String code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitle.setText("加入一个班级");
        mTimeTV.setVisibility(View.GONE);
    }

    private void httpGetClassData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_DETAIL, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", code);
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
                            mClasses = gson.fromJson(json.toString(), Classes.class);
                        } else {
                            showToast("不存在此班级");
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
        if (mClasses != null) {
            mResultLayout.setVisibility(View.VISIBLE);
            mStudentNumTV.setText("人数：" + mClasses.getClassrs());
            mNameTV.setText("名称：" + mClasses.getClassname());
            mCodeTV.setText("班级号：" + mClasses.getClassbh());
            mTeacherNameTV.setText("老师：" + mClasses.getTeachername());
        }
    }

    //申请加入班级
    private void httpJoinClass() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.STUDENT_JOIN_CLASS, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", mClasses.getClassbh());
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
                            EventBus.getDefault().post(new EventFlag(Constant.EvJoinClass,""));
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

    @OnClick({R.id.back, R.id.title, R.id.submit_btn, R.id.join_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.title:
                break;
            case R.id.submit_btn:
                code = mEditClassCode.getText().toString();
                if (!TextUtils.isEmpty(code)) {
                    httpGetClassData();
                } else {
                    showToast("请输入班级号");
                }
                break;
            case R.id.join_btn:
                httpJoinClass();
                break;
        }
    }
}
