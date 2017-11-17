package com.ss.education.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shcyd.lib.base.BaseAppManager;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.User;
import com.ss.education.ui.activity.login.LoginActivity;
import com.ss.education.ui.activity.photo.BigPhotoActivity;
import com.ss.education.utils.ImageUtils;
import com.ss.education.utils.JPushUtils;
import com.ss.education.utils.SPUtils;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yongchun.library.view.ImageSelectorActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_CROP;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_PREVIEW;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_MAX_SELECT_NUM;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SELECT_MODE;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SHOW_CAMERA;
import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_IMAGE;

public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.right_text)
    TextView mRightText;
    @Bind(R.id.name_edit)
    TextView mNameEdit;
    @Bind(R.id.email_edit)
    TextView mEmailEdit;
    @Bind(R.id.phone_edit)
    TextView mPhoneEdit;
    @Bind(R.id.school_tv)
    TextView mSchoolTV;
    @Bind(R.id.header_image)
    ImageView mHeaderIV;
    private String imgPath = ""; // 选择图片
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitle.setText("我的信息");
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("编辑");
        mNameEdit.setText(MyApplication.getUser().getRealname());
        mEmailEdit.setText(MyApplication.getUser().getEmail());
        mPhoneEdit.setText(MyApplication.getUser().getPhone());
        mSchoolTV.setText(MyApplication.getUser().getSchoolname());
        ImageUtils.setDefaultNormalImage(mHeaderIV, MyApplication.getUser().getImgpath(), R.drawable.icon_header1);


    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvUpdateUserInfo)) {
            initView();
        }
    }

    @OnClick({R.id.back, R.id.right_text, R.id.update_pass, R.id.setting_layout, R.id.touxiang_layout, R.id.header_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                go(EditUserInfoActivity.class);
                break;
            case R.id.update_pass:
                break;
            case R.id.setting_layout: // 退出登录
                RongIM.getInstance().logout();
                JPushUtils.jPushMethod(this, "", null);
                SPUtils.put(UserInfoActivity.this, "password", "");
                SPUtils.put(UserInfoActivity.this, "username", "");
                httpSignOut();
                break;
            case R.id.touxiang_layout:
                pickImage();
//                httpUploadImage();
                break;
            case R.id.header_image:
                Bundle bundle = new Bundle();
                List<String> list = new ArrayList<>();
                list.add(MyApplication.getUser().getImgpath());
                bundle.putSerializable("pathList", (Serializable) list);
                bundle.putInt("position", 0);
                go(BigPhotoActivity.class, bundle);
                overridePendingTransition(0, 0);
                break;
        }
    }

    private void pickImage() {
        Intent intent = new Intent(UserInfoActivity.this, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, 1);
        intent.putExtra(EXTRA_SELECT_MODE, 2);
        intent.putExtra(EXTRA_SHOW_CAMERA, true);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, true);
        intent.putExtra(EXTRA_ENABLE_CROP, true);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
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
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPLOAD_FILE, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        request.add("img", new FileBinary(new File(imgPath)));
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
                            User u = MyApplication.getUser();
                            u.setImgpath(jsonObject.getString("filepath"));
                            MyApplication.setUser(u);
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateUserInfo));
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


    private void httpSignOut() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.SIGN_OUT, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
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

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {

                }
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
                MyApplication.setUser(null);
                BaseAppManager.getInstance().clearBackActivities();
                go(LoginActivity.class);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll();
    }
}
