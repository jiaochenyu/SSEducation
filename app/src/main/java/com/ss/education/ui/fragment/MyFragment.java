package com.ss.education.ui.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseFragment;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.listener.DialogListener;
import com.ss.education.ui.activity.examination.ExamErrorSectionListActivity;
import com.ss.education.ui.activity.examination.ExamRecordActivity;
import com.ss.education.ui.activity.my.UserInfoActivity;
import com.ss.education.ui.activity.parent.MyChildActivity;
import com.ss.education.ui.activity.parent.MyParentActivity;
import com.ss.education.ui.activity.photo.BigPhotoActivity;
import com.ss.education.ui.activity.setting.AboutUsActivity;
import com.ss.education.ui.activity.teacher.StudentsListActivity;
import com.ss.education.utils.ImageUtils;
import com.ss.education.weight.CommentDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class MyFragment extends BaseFragment {
    //    public static final int RIGHT = 1;
    public static final int EXAM_ERROR = 0;//错题
    @BindView(R.id.header_iv)
    ImageView mHeaderIv;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.search_layout)
    RelativeLayout mSearchLayout;
    @BindView(R.id.my_teacher)
    RelativeLayout mMyTeacherLayout;
    @BindView(R.id.my_parent)
    RelativeLayout mMyParentLayout;
    @BindView(R.id.my_child)
    RelativeLayout mChildLayout;
    @BindView(R.id.invitation_code)
    RelativeLayout mInvitationCodeRL;

    @BindView(R.id.layout_error)
    LinearLayout mErrorLayout;
    @BindView(R.id.layout_record)
    LinearLayout mRecordLayout;
    private View mView;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String role = MyApplication.getUser().getPart();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, mView);
        initView();
        return mView;
    }

    private void initView() {
        try {
            Log.e("图片", MyApplication.getUser().getImgpath());
            ImageUtils.setCircleDefImage(mHeaderIv, MyApplication.getUser().getImgpath(), R.drawable.touxiang);
            if (TextUtils.isEmpty(MyApplication.getUser().getRealname())) {
                mName.setText(MyApplication.getUser().getUsername());
            } else {
                mName.setText(MyApplication.getUser().getRealname());
            }
            if (role.equals("T")) {
                mSearchLayout.setVisibility(View.VISIBLE);
                mMyTeacherLayout.setVisibility(View.GONE);
                mInvitationCodeRL.setVisibility(View.VISIBLE);
            } else if (role.equals("S")) {
                mSearchLayout.setVisibility(View.GONE);
                mMyTeacherLayout.setVisibility(View.GONE);
                mMyParentLayout.setVisibility(View.VISIBLE);
                mInvitationCodeRL.setVisibility(View.GONE);
            } else {
                mChildLayout.setVisibility(View.VISIBLE);
                mSearchLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                mRecordLayout.setVisibility(View.GONE);
                mInvitationCodeRL.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvUpdateUserInfo)) {
            initView();
        }
    }


    @OnClick({R.id.header_iv, R.id.userinfo_layout, R.id.search_layout, R.id.layout_error, R.id.layout_record, R.id.my_parent, R.id.my_child, R.id.invitation_code, R.id.about_us})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.header_iv:
                Bundle bundle = new Bundle();
                List<String> list = new ArrayList<>();
                list.add(MyApplication.getUser().getImgpath());
                bundle.putSerializable("pathList", (Serializable) list);
                bundle.putInt("position", 0);
                go(BigPhotoActivity.class, bundle);
                getActivity().overridePendingTransition(0, 0);
                break;

            case R.id.userinfo_layout:
                go(UserInfoActivity.class);
                break;
            case R.id.search_layout:
                go(StudentsListActivity.class);
                break;

            case R.id.layout_error:
                go(ExamErrorSectionListActivity.class);
                break;
            case R.id.layout_record:
                go(ExamRecordActivity.class);
                break;
            case R.id.my_parent:
                go(MyParentActivity.class);
                break;
            case R.id.my_child:
                go(MyChildActivity.class);
                break;
            case R.id.invitation_code:
                httpGetTCode();
                break;

            case R.id.about_us:
                go(AboutUsActivity.class);
                break;
        }
    }

    /**
     * 获取老师邀请码
     */
    private void httpGetTCode() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.INVITATION_TEACHER_CODE, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
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
                        Log.e("邀请码", object.toString());
                        String status = object.getString("status");
                        if (status.equals("100")) {
                            String code = object.getString("invitationCode");
                            String effectivetime = object.getString("effectivetime");
                            String s = "" + effectivetime + "分钟有效！";
                            showCode(code, s);
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
    }

    private void showCode(final String code, String time) {
        CommentDialog.showInvitaionCodeDialog(getActivity(), code, time);
        CommentDialog.setmDialogListener(new DialogListener() {
            @Override
            public void yesClickListener() {

            }

            @Override
            public void noClickListener() {
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(code);
                showToast("已经成功复制到粘贴板");
            }
        });
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
