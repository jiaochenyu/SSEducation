package com.ss.education.ui.activity.parent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
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
import com.ss.education.entity.Student;
import com.ss.education.ui.activity.examination.ExamRecordActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyChildActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mShenheTV;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.message)
    RelativeLayout mMessageRelativeLayout;
    @BindView(R.id.message_num)
    TextView mShenheNumTV;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private List<Student> mList;
    private MyAdapter mAdapter;
    int shenheNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_child);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle.setText("我的孩子");
//        mShenheTV.setVisibility(View.VISIBLE);
//        mShenheTV.setText("审核");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_s_p_shenqing, mList);
        mListview.setAdapter(mAdapter);
        httpGetStudent();

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                httpGetStudent();
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("studentId", mList.get(position).getUuid());
                bundle.putSerializable("student", mList.get(position));
                go(ExamRecordActivity.class, bundle);
            }
        });
    }


    private void httpGetStudent() {
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
                                Student user = gson.fromJson(jsonArray.getJSONObject(i).getJSONObject("object").toString(), Student.class);
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
//        if (adjustList(mList)) {
//            mNullBg.setVisibility(View.GONE);
//            mListview.setVisibility(View.VISIBLE);
//        } else {
//            mNullBg.setVisibility(View.VISIBLE);
//            mListview.setVisibility(View.GONE);
//
//        }
        try {
            if (shenheNum > 0) {
                mMessageRelativeLayout.setVisibility(View.VISIBLE);
                mShenheNumTV.setText(shenheNum + "");
            } else {
                mMessageRelativeLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvBindParentStu)) {
            httpGetStudent();
        }
    }

    @OnClick({R.id.back, R.id.right_text, R.id.message, R.id.add_child})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.right_text:
//                go(ShenheSPActivity.class);
//                break;
            case R.id.message:
                go(ShenheSPActivity.class);
                break;
            case R.id.add_child:
                go(SearchParentAndStuActivity.class);
                break;
        }
    }


    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, final int position) {

            TextView name = holder.getView(R.id.name);
            TextView phone = holder.getView(R.id.phone);
            TextView phoneTxt = holder.getView(R.id.phone_txt);
            TextView nametxt = holder.getView(R.id.name_txt);
            nametxt.setText("学生姓名:");
            phoneTxt.setText("学生编号:");
            RelativeLayout bottomlayout = holder.getView(R.id.bottom_layout);
            bottomlayout.setVisibility(View.GONE);
            name.setText(mList.get(position).getRealname());
            phone.setText(mList.get(position).getUsername());

        }
    }
}
