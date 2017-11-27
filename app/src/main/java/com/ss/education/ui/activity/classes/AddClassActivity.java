package com.ss.education.ui.activity.classes;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ss.education.listener.DialogListener;
import com.ss.education.utils.ImageUtils;
import com.ss.education.weight.DialogClassCode;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yongchun.library.view.ImageSelectorActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_CROP;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_PREVIEW;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_MAX_SELECT_NUM;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SELECT_MODE;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SHOW_CAMERA;
import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_IMAGE;

public class AddClassActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.name)
    EditText mNameET;
    @BindView(R.id.beizhu)
    EditText mBeizhuET;
    @BindView(R.id.class_pic)
    ImageView mClassPic;
    @BindView(R.id.image_layout)
    LinearLayout mImgLayout;
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    private Classes mClasses;
    private String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mClasses = (Classes) extras.getSerializable("classes");
        }
    }

    private void initView() {
        if (mClasses == null) {
            mTitle.setText("添加班级");
            mImgLayout.setVisibility(View.GONE);
        } else {
            mTitle.setText("修改详情");
            mImgLayout.setVisibility(View.VISIBLE);
            ImageUtils.setCircleDefImage(mClassPic, ConnectUrl.PICURL + mClasses.getImgpath(), R.drawable.icon_class8);
            mNameET.setText(mClasses.getClassname());
            mBeizhuET.setText(mClasses.getRemarks());
        }


    }

    private void initData() {

    }

    @OnClick({R.id.back, R.id.submit, R.id.class_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                if (mClasses == null) {
                    httpUploadData();
                } else {
                    httpUpdateData();
                }
                break;
            case R.id.class_pic:
                pickImage();
                break;
        }
    }

    private void httpUploadData() {
        String name = mNameET.getText().toString();
        String beizhu = mBeizhuET.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入班级名称");
            return;
        }
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.ADD_CLASS, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classname", name);
            jsonObject.put("remarks", beizhu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", jsonObject.toString());
        request.add("useruuid", MyApplication.getUser().getUuid());
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
                        String classbh = object.getString("classbh"); //编号
                        if (status.equals("100")) {
                            showDialog(classbh);
                        } else {
                            showToast("提交失败请稍后再试");
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

    private void showDialog(final String classbh) {
        DialogClassCode.showDialog(this, classbh);
        DialogClassCode.setmDialogListener(new DialogListener() {
            @Override
            public void yesClickListener() {
                EventBus.getDefault().post(new EventFlag(Constant.EvAddClass, ""));
                finish();
            }

            @Override
            public void noClickListener() {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(classbh);
                showToast("已经成功复制到粘贴板");
                EventBus.getDefault().post(new EventFlag(Constant.EvAddClass, ""));
                finish();
            }
        });
    }


    private void httpUpdateData() {
        String name = mNameET.getText().toString();
        String beizhu = mBeizhuET.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入班级名称");
            return;
        }
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPDATE_CLASS, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", mClasses.getClassbh());
            jsonObject.put("classname", name);
            jsonObject.put("remarks", beizhu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", jsonObject.toString());
        request.add("useruuid", MyApplication.getUser().getUuid());
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
                            showToast("修改成功");
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateClassDetail, ""));
                            finish();
                        } else {
                            showToast("提交失败请稍后再试");
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


    private void pickImage() {
        Intent intent = new Intent(AddClassActivity.this, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, 1);
        intent.putExtra(EXTRA_SELECT_MODE, 2);
        intent.putExtra(EXTRA_SHOW_CAMERA, true);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, true);
        intent.putExtra(EXTRA_ENABLE_CROP, true);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            // do something
            imgPath = images.get(0);
            Log.e("图片", imgPath);
            if (!TextUtils.isEmpty(imgPath)) {
                httpUploadImage();
            }


        }
    }

    private void httpUploadImage() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPLOAD_CLASS_IAMGE_HEADER, RequestMethod.POST);
        JSONObject object = new JSONObject();
        try {
            object.put("classid", mClasses.getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("useruuid", MyApplication.getUser().getUuid());
        request.add("img", new FileBinary(new File(imgPath)));
        request.add("data", object.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            mClasses.setImgpath(jsonObject.getString("filepath"));
                            ImageUtils.setCircleDefImage(mClassPic, ConnectUrl.PICURL + mClasses.getImgpath(), R.drawable.icon_add_pic_def);
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateClassDetail, ""));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("图片上传", response.get().toString());
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Log.e("图片上传sss", "失败");
                }
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
            }
        });

    }


}
