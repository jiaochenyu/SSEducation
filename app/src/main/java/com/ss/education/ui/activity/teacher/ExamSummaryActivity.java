package com.ss.education.ui.activity.teacher;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.ss.education.customview.NoScrollGridView;
import com.ss.education.entity.Exam;
import com.ss.education.entity.ExamRecord;
import com.ss.education.entity.Student;
import com.ss.education.ui.activity.examination.AnswerActivity;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 老师查看学生答题之前的  答题卡界面
 */

public class ExamSummaryActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.exam_name)
    TextView mExamName;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.search)
    TextView msearchTV;
    @Bind(R.id.add_pingyu)
    TextView mAddPingyuTV;
    @Bind(R.id.bottom_vline)
    View mVline;
    @Bind(R.id.right_exam)
    TextView mRightExamTV;
    @Bind(R.id.time)
    TextView mTimeTV;
    @Bind(R.id.gridview)
    NoScrollGridView mGridview;
    @Bind(R.id.pingyu)
    EditText mPingyuEdit;
    @Bind(R.id.pingyu_t)
    TextView mPingyuTV;
    @Bind(R.id.layout_pingyu)
    LinearLayout mPingyuLL;
    private List<Exam> mExamList;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private Student mStudent;
    private ExamRecord mExamRecord;
    private GridviewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_summary);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
//            mExamList = (List<Exam>) extras.getSerializable("originList");
            mStudent = (Student) extras.getSerializable("student");
            mExamRecord = (ExamRecord) extras.getSerializable("record");
        }
    }

    private void initView() {
        mTitle.setText(R.string.datika);
        mExamName.setText(mExamRecord.getZtmainname());
        if (mStudent == null) {
            mName.setVisibility(View.GONE);
        } else {
            mName.setText("姓名：" + mStudent.getRealname());
        }
        mPingyuEdit.setText(mExamRecord.getRemarks());
        if (MyApplication.getUser().getPart().equals("T")) {
            mPingyuLL.setVisibility(View.VISIBLE);
            mAddPingyuTV.setVisibility(View.VISIBLE);
            mVline.setVisibility(View.VISIBLE);
        } else  if (MyApplication.getUser().getPart().equals("S")){
            mAddPingyuTV.setVisibility(View.GONE);
            mVline.setVisibility(View.GONE);
            mPingyuEdit.setFocusableInTouchMode(false);
            mPingyuEdit.setFocusable(false);
            mPingyuEdit.setCursorVisible(false);
            if (!TextUtils.isEmpty(mExamRecord.getRemarks())) {
                mPingyuLL.setVisibility(View.VISIBLE);
            }
        } else {
            //家长
        }
        mTimeTV.setText("时间：" + StringUtils.getTime(mExamRecord.getTimes()));
    }

    private void initData() {
        mExamList = new ArrayList<>();
        mAdapter = new GridviewAdapter(ExamSummaryActivity.this, R.layout.item_finish_gridview, mExamList);
        mGridview.setAdapter(mAdapter);
        httpGetExamData();
    }

    private void initListener() {
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("originList", (Serializable) mExamList);
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
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_EXAM_RECORD_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            if (mStudent != null) {
                jsonObject.put("appuserid", mStudent.getUuid());
            } else {
                jsonObject.put("appuserid", MyApplication.getUser().getUuid());
            }
            jsonObject.put("ztmainid", mExamRecord.getUuid());
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
                                mExamList.add(exam);
                            }
                            mExamRecord.setRemarks(object.getString("remarks"));
                            if (object.has("teachername")){
                                mExamRecord.setTeachername(object.getString("teachername"));
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

    private void setView() {

        int rightNum = 0;
        for (int i = 0; i < mExamList.size(); i++) {
            if (mExamList.get(i).getWhether() == 1) {
                rightNum++;
            }
        }
        mRightExamTV.setText(Html.fromHtml(
                "正确：" +
                        "<font color = '#32B16C'><big>" +
                        rightNum +
                        "</big></font>" +
                        "/" +
                        mExamList.size()));

        if (MyApplication.getUser().getPart().equals("T")) {
            mPingyuLL.setVisibility(View.VISIBLE);
        } else  if (MyApplication.getUser().getPart().equals("S")) {
            if (!TextUtils.isEmpty(mExamRecord.getRemarks())) {
                mPingyuLL.setVisibility(View.VISIBLE);
            } else {
                mPingyuLL.setVisibility(View.GONE);
            }
            mPingyuTV.setText(mExamRecord.getTeachername() + "的评语");
        }
        mPingyuEdit.setText(mExamRecord.getRemarks());
        mAdapter.notifyDataSetChanged();
    }


    public void httpUploadPingyu() {
        String pingyu = mPingyuEdit.getText().toString();
        if (TextUtils.isEmpty(pingyu)) {
            showToast("请输入评语！");
            return;
        }
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPLOAD_PINGYU, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("remarks", pingyu);
            jsonObject.put("ztmainid", mExamRecord.getUuid());
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
                            showToast(object.getString("msg"));
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


    @OnClick({R.id.back, R.id.search, R.id.add_pingyu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search:
                Bundle bundle = new Bundle();
                bundle.putSerializable("originList", (Serializable) mExamList);
                bundle.putString("pageFlag", Constant.RECORD_PAGE_FLAG_RECORD);
                bundle.putInt("position", 0);
                go(AnswerActivity.class, bundle);
                break;
            case R.id.add_pingyu:
                httpUploadPingyu();
                break;
        }
    }


    class GridviewAdapter extends CommonAdapter {

        public GridviewAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);

        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            Button mBtn = holder.getView(R.id.position);
            mBtn.setText("" + (position + 1));
            if (mExamList.get(position).getWhether() == 1) {
                mBtn.setBackground(getResources().getDrawable(R.drawable.bg_corner_green));
            } else {
                mBtn.setBackground(getResources().getDrawable(R.drawable.bg_corner_red));
            }
            mBtn.setFocusable(false);
            mBtn.setClickable(false);
        }
    }
}
