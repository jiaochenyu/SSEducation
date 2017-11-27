package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import com.ss.education.customview.popupwindow.ExpandTabView;
import com.ss.education.customview.popupwindow.PopViewList;
import com.ss.education.entity.Exam;
import com.ss.education.entity.ExamRecord;
import com.ss.education.entity.Student;
import com.ss.education.ui.activity.teacher.ExamSummaryActivity;
import com.ss.education.utils.StringUtils;
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

/**
 * 答题记录 老师查看学生的答题记录   学生查看自己的答题记录
 */
public class ExamRecordActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    @BindView(R.id.expandtabTab)
    ExpandTabView mExpandtabTab;
    @BindView(R.id.pop_bg_view)
    View v_bg;
    List<ExamRecord> mList;
    private List<ExamRecord> mNewList;
    MyAdapter mAdapter;
    RequestQueue mQueue = NoHttp.newRequestQueue();
    private String studentId = "";
    Student mStudent;

    private ArrayList<String> mExpandArray = new ArrayList<>();  //筛选
    private ArrayList<View> mViewArray = new ArrayList<View>();
    PopViewList viewXueke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_record);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            studentId = extras.getString("studentId", "");
            mStudent = (Student) extras.getSerializable("student");
        }
    }

    private void initData() {
        mList = new ArrayList<>();
        mNewList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_exam_record_group, mNewList);
        mListview.setAdapter(mAdapter);

        mExpandArray.add("全部学科");
        viewXueke = new PopViewList(this, Constant.XUEKELSIT, Constant.XUEKELSITVALUES, 1);
        mViewArray.add(viewXueke);
        mExpandtabTab.setValue(mExpandArray, mViewArray, v_bg);

        httpGetData();
    }


    private void initView() {
        mTitle.setText("做题记录");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                mNewList.clear();
                httpGetData();
                onExpandRefresh(viewXueke, "全部");
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                httpGetExamData(mList.get(position).getUuid());
                Bundle bundle = new Bundle();
                bundle.putSerializable("student", mStudent);
                bundle.putSerializable("record", mList.get(position));
                go(ExamSummaryActivity.class, bundle);
            }
        });
        viewXueke.setOnSelectListener(new PopViewList.OnSelectListener() {

            @Override
            public void getValue(String value, String showText) {
                v_bg.setVisibility(View.GONE);
                onExpandRefresh(viewXueke, showText);
                filterMethod(value);
            }
        });
    }

    private void httpGetData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_MY_EXAM_GROUP_RECORD_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appuserid", TextUtils.isEmpty(studentId) ? MyApplication.getUser().getUuid() : studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", jsonObject.toString());
        request.add("useruuid", MyApplication.getUser().getUuid());
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
                            JSONArray array = object.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                ExamRecord record = gson.fromJson(array.getJSONObject(i).toString(), ExamRecord.class);
                                mList.add(record);
                            }
                            mNewList.addAll(mList);
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
                Log.e("array", mList.size() + "");
                setView();
            }
        });
    }


    /**
     * 筛选
     */
    private void filterMethod(String value) {
        mNewList.clear();
        if (value.contains("全部")) {
            mNewList.addAll(mList);
        } else {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getCoursename().contains(value)) {
                    mNewList.add(mList.get(i));
                }

            }
        }
        setView();
    }


    /**
     * 获取考试集合
     */
    public void httpGetExamData(String id) {

        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_EXAM_RECORD_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appuserid", TextUtils.isEmpty(studentId) ? MyApplication.getUser().getUuid() : studentId);
            jsonObject.put("ztmainid", id);
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
                    List<Exam> exams = new ArrayList<Exam>();
                    JSONObject object = response.get();
                    Gson gson = new Gson();
                    try {
                        String status = object.getString("status");
                        if (status.equals("100")) {
                            JSONArray array = object.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                Exam exam = gson.fromJson(array.getJSONObject(i).toString(), Exam.class);
                                exams.add(exam);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("originList", (Serializable) exams);
                            bundle.putString("pageFlag", Constant.RECORD_PAGE_FLAG_RECORD);
                            bundle.putSerializable("student", mStudent);
                            go(AnswerActivity.class, bundle);
                            go(ExamSummaryActivity.class);
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
            }
        });
    }

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mNewList)) {
//            mSwipeRefresh.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);
//            mSwipeRefresh.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    private void onExpandRefresh(View view, String showText) {

        mExpandtabTab.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !mExpandtabTab.getTitle(position).equals(showText)) {
            mExpandtabTab.setTitle(showText, position);
        }
//		Use.showToast(mContext, showText);

    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {

        if (!mExpandtabTab.onPressBack()) {
            finish();
        }

    }

    class MyAdapter extends CommonAdapter {
        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            TextView name = holder.getView(R.id.name);
            TextView createTime = holder.getView(R.id.c_time);
            TextView rightPer = holder.getView(R.id.right_per);
            TextView userTime = holder.getView(R.id.use_time);
            Log.e("adapterlsit", mList.size() + "");
            try {
                name.setText(mList.get(position).getZtmainname());
                String time = mList.get(position).getCreatetime();
                time = time.substring(0, time.length() - 1 - 3);
                createTime.setText(time);
                rightPer.setText("正确率：" + mList.get(position).getAccuracy());
                userTime.setText("用时：" + StringUtils.getTime(mList.get(position).getTimes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            shuoming.setText(mList.get(position).getSchoolname());

        }
    }
}
