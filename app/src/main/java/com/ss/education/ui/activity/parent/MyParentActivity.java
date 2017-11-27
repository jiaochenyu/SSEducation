package com.ss.education.ui.activity.parent;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyParentActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.j_name)
    TextView mJName;
    @BindView(R.id.j_phone)
    TextView mJPhone;
    @BindView(R.id.jiazhang_layout)
    LinearLayout mJiazhangLayout;
    @BindView(R.id.add_parent)
    TextView mAddParent;
    @BindView(R.id.message_num)
    TextView mMessageNumTV;
    @BindView(R.id.message)
    RelativeLayout mMessageLayout;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    User mUser;
    int shenheNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parent);
        ButterKnife.bind(this);
        initView();
        initListener();
        httpGetParentOrStudent();

    }


    private void initView() {
        mTitle.setText("我的家长");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUser = null;
                httpGetParentOrStudent();
            }
        });
    }


    @OnClick({R.id.back, R.id.add_parent, R.id.message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_parent:
                go(SearchParentAndStuActivity.class);
                break;
            case R.id.message:
                go(ShenheSPActivity.class);
                break;
        }
    }


    private void httpGetParentOrStudent() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.MY_PARENT_OR_STUDNET, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("state", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        request.add("useruuid", MyApplication.getUser().getUuid());
        request.add("data", jsonObject.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mSwipeRefresh.setRefreshing(true);
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        String status = object.getString("status");
                        if (status.equals("100")) {
                            shenheNum = object.getInt("shrs");
                            JSONArray jsonArray = object.getJSONArray("array");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mUser = gson.fromJson(jsonArray.getJSONObject(i).getJSONObject("object").toString(), User.class);
                            }
                        } else {
                            showToast("加载失败！");
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
                mSwipeRefresh.setRefreshing(false);
                setView();
            }


        });

    }

    private void setView() {
        if (mUser != null) {
            try {
                mMessageLayout.setVisibility(View.GONE);
                mJiazhangLayout.setVisibility(View.VISIBLE);
                mJName.setText(mUser.getRealname());
                mJPhone.setText(mUser.getUsername());
                mAddParent.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (shenheNum > 0) {
                mMessageLayout.setVisibility(View.VISIBLE);
                mMessageNumTV.setText(shenheNum + "");
            } else {
                mMessageLayout.setVisibility(View.GONE);
            }
            mJiazhangLayout.setVisibility(View.GONE);
            mAddParent.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvBindParentStu)) {
            httpGetParentOrStudent();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
