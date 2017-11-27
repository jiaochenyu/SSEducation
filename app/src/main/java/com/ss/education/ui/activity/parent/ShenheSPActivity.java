package com.ss.education.ui.activity.parent;
/**
 * 审核 列表 家长 学生
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShenheSPActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    List<User> mList;
    List<String> uuidList;
    MyAdapter mAdapter;
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shenhe_sp);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        httpGetParentOrStudent();
    }


    private void initView() {
        mTitle.setText("审核列表");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
    }

    private void initData() {
        uuidList = new ArrayList<>();
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_s_p_shenqing, mList);
        mListview.setAdapter(mAdapter);

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                uuidList.clear();
                httpGetParentOrStudent();
            }
        });
    }


    private void httpGetParentOrStudent() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.MY_PARENT_OR_STUDNET, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("state", "0");
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
                            JSONArray jsonArray = object.getJSONArray("array");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                uuidList.add(jsonArray.getJSONObject(i).getString("uuid"));
                                User user = gson.fromJson(jsonArray.getJSONObject(i).getJSONObject("object").toString(), User.class);
                                mList.add(user);
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
        if (adjustList(mList)) {
            mNullBg.setVisibility(View.GONE);
            mListview.setVisibility(View.VISIBLE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);

        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    //1是同意 2是拒绝
    private void httpRefuseOrAgree(final String uuid, final int flag, final int position) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.BIND_PARENT_OR_STUDNET_Y_N, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("state", flag);
            jsonObject.put("uuid", uuid);
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
                            if (flag == 1) { //同意
                                EventBus.getDefault().post(new EventFlag(Constant.EvBindParentStu, ""));
                                mList.remove(position);
                                uuidList.remove(position);
                            } else { //拒绝
                                EventBus.getDefault().post(new EventFlag(Constant.EvBindParentStu, ""));
                                mList.remove(position);
                                uuidList.remove(position);
                            }
                        } else {
                            showToast("请求失败！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                showToast("请求失败！");
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
                setView();
            }
        });

    }

    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, final int position) {

            TextView name = holder.getView(R.id.name);
            TextView phone = holder.getView(R.id.phone);
            TextView refuse = holder.getView(R.id.refuse);
            TextView agree = holder.getView(R.id.agree);
            TextView phoneTxt = holder.getView(R.id.phone_txt);
            TextView nametxt = holder.getView(R.id.name_txt);

            if (MyApplication.getUser().getPart().equals("S")) {
                name.setText(mList.get(position).getRealname());
                phone.setText(mList.get(position).getUsername());
                nametxt.setText("家长姓名:");
                phoneTxt.setText("家长号码:");
            } else if (MyApplication.getUser().getPart().equals("J")) {
                name.setText(mList.get(position).getRealname());
                phone.setText(mList.get(position).getUsername());
                nametxt.setText("学生姓名:");
                phoneTxt.setText("学生编号:");
            }
            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    httpRefuseOrAgree(uuidList.get(position), 2, position);
                }
            });
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    httpRefuseOrAgree(uuidList.get(position), 1, position);
                }
            });


        }
    }
}



