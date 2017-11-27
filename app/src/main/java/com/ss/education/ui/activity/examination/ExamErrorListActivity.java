package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.Exam;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamErrorListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    private List<Exam> mList;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String mSectionId = "";
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_error_list);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mSectionId = extras.getString("sectionid");
        }
    }

    private void initView() {
        mTitle.setText("错题列表");
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_error_list, mList);
        mListview.setAdapter(mAdapter);
        httpGetExamData();
    }

    private void initListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("originList", (Serializable) mList);
                bundle.putString("pageFlag", Constant.RECORD_PAGE_FLAG_RECORD);
                bundle.putInt("position", position);
                go(AnswerActivity.class, bundle);
            }
        });
    }


    /**
     * 获取考试集合
     */
    public void httpGetExamData() {

        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_EXAM_ERROR_RECORD_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("appuserid", MyApplication.getUser().getUuid());
            jsonObject.put("sectionid", mSectionId);
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
                            JSONArray array = object.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                Exam exam = gson.fromJson(array.getJSONObject(i).toString(), Exam.class);
                                mList.add(exam);
                            }


                        } else {
                            showToast("请求失败,请稍后再试");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                showToast("请求失败,请稍后再试");
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
                setView();
            }
        });
    }

    public void setView() {
        if (adjustList(mList)) {
            mListview.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
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


    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            TextView name = holder.getView(R.id.name);
            name.setText(mList.get(position).getContent());


        }
    }
}
