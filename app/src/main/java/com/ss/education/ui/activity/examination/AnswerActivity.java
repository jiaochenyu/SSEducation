package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.Exam;
import com.ss.education.entity.ExamResult;
import com.ss.education.listener.DialogListener;
import com.ss.education.ui.activity.photo.BigPhotoActivity;
import com.ss.education.utils.AZChange;
import com.ss.education.utils.ImageUtils;
import com.ss.education.utils.StringUtils;
import com.ss.education.weight.CommentDialog;
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
import cn.iwgang.countdownview.CountdownView;

public class AnswerActivity extends BaseActivity {

    @BindView(R.id.back_text)
    TextView mBackText;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.countdown)
    CountdownView mCountdown;
    @BindView(R.id.answer_title)
    TextView mAnswerTitle;
    @BindView(R.id.listview)
    NoScrollListView mListview;
    @BindView(R.id.gridview)
    GridView mGridView;
    @BindView(R.id.current_answer)
    TextView mCurrentAnswer;
    @BindView(R.id.next_answer)
    TextView mNextAnswer;
    @BindView(R.id.bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.right_answer)
    TextView mRightAnswerTV;
    @BindView(R.id.analysis)
    TextView mAnalysisTV;
    @BindView(R.id.jiexi_layout)
    LinearLayout mAnalysisLayout;
    @BindView(R.id.big_title_tv)
    TextView mBigTitleTV;
    @BindView(R.id.all_layout)
    LinearLayout mAllLayout;
    @BindView(R.id.jiexi_tv)
    TextView mJiexiTV;
    @BindView(R.id.pre_answer)
    Button mPreAnswerTV;

    private Long countTimes = 0L; //答题总时间
    //当前显示的题目
    private int current;

    private String idStr = ""; //获取到的章节id
    List<Exam> mList;
    List<Exam.AnswerBean> mAnswerList;
    List<String> mPicStrList;
    List<ExamResult> mResultList;//用于保存户答题结果的

    private MyAdapter mAdapter;
    private MyGridAdapter mPicAdapter;

    private boolean isBig = false; //题目是否放大
    long[] mHits = new long[2]; //题目双击 根据数据判断双击事件 Google用的方法

    private String userAnswer = ""; //用户的选择的选项  "1-2-3"
    private int isRight = 0; //选项是否正确 0 否，1 是
    private long useTime = 0L; //答题所用时间

    private int position = -1; //第几个错题

    private String pageFlag = "";//页面表示 //判断是否是答题记录传过来的。 // 接收到“ record ”代表是答题记录跳转过来的


    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            idStr = extras.getString("idStr"); //列表传过来的 //或者是点击再来十题传过来的。

            mList = (List<Exam>) extras.getSerializable("originList");    //考试结果传过来的
            position = extras.getInt("position", -1);  //考试结果传过来的
            if (position == -1) {
                current = 0;
            } else {
                current = position;
            }
            mResultList = (List<ExamResult>) extras.getSerializable("resultList");

            pageFlag = extras.getString("pageFlag", "");
        }
    }

    private void initView() {
        mTitle.setText("");
        if (position != -1 && !pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD)) { //错题传过来的
            mCountdown.setVisibility(View.GONE);
            mNextAnswer.setVisibility(View.GONE);
            mAnalysisLayout.setVisibility(View.VISIBLE);
            mRightAnswerTV.setText(Html.fromHtml("正确答案为"
                            + "<font color = '#32B16C'><big>" + changerOrder(mList.get(position).getRightanswer()) + "</big></font>"
                            + "，你的选项为"
                            + "<font color = '#A0CBF0'><big>" + changerOrder(mResultList.get(position).getMyanswer()) + "</big></font>"
                            + (mResultList.get(position).getWhether() == 1 ? "，回答正确。" : "，回答错误。")
                    )
            );
            mBackText.setText("返回");
            mJiexiTV.setText(mList.get(position).getRemarks());
        } else if (pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD)) {
//            mCountdown.setVisibility(View.GONE);
            mPreAnswerTV.setVisibility(View.VISIBLE);
            mAnalysisLayout.setVisibility(View.VISIBLE);
            mRightAnswerTV.setText(Html.fromHtml("正确答案为"
                            + "<font color = '#32B16C'><big>" + changerOrder(mList.get(current).getRightanswer()) + "</big></font>"
                            + "，你的选项为"
                            + "<font color = '#A0CBF0'><big>" + changerOrder(mList.get(current).getMyanswer()) + "</big></font>"
                            + (mList.get(current).getWhether() == 1 ? "，回答正确。" : "，回答错误。")
                    )
            );
            mBackText.setText("返回");
            mJiexiTV.setText(mList.get(current).getRemarks());
            mListview.setEnabled(false);
        }
    }


    private void initData() {
        mAnswerList = new ArrayList<>();
        mPicStrList = new ArrayList<>();
        if (position != -1) { //错题传过来的
            current = position;
            setData();
        } else if (!adjustList(mList)) { //正常请求数据
            current = 0;
            mList = new ArrayList<>();
            mResultList = new ArrayList<>();
            httpData();
        } else if (pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD) && adjustList(mList)) {  //做题记录传过来的
            current = position;
            setData();
        }

    }

    private void initListener() {
        mCountdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                jugeNext(true);
                showToast("时间超时，自动跳转到下一题！");
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectOption(position);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goPhotoView(position); //图片放大
            }
        });
    }


    private void httpData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_EXAM_CONTENT, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuids", idStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", jsonObject.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                Gson gson = new Gson();
                JSONObject json = response.get();
                Log.d("考试数据：", json.toString());
                try {
                    String status = json.getString("status");
                    if (status.equals("100")) {
                        JSONArray array = json.getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            Exam exam = gson.fromJson(array.getJSONObject(i).toString(), Exam.class);
                            mList.add(exam);
                        }
                        setData();

                    } else {
                        showToast("获取失败");
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                finish();
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
            }
        });
    }

    /**
     * 绑定数据到布局中
     */
    private void setData() {
        if (position == -1 && !pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD)) {  // 答题 开启倒计时  不等于-1 是查看错题 不开启倒计时
            mCountdown.start(mList.get(current).getTimes());
        } else {
            mCountdown.updateShow(mList.get(current).getTimes());
        }
        mCurrentAnswer.setText(Html.fromHtml("<font color = '#D52D2C'><big>" + (current + 1) + "</big></font>" + "/" + mList.size()));
        justSigleText();
        mAnswerList = mList.get(current).getAnswer();
        mBigTitleTV.setText(mList.get(current).getContent());
        mAdapter = new MyAdapter(AnswerActivity.this, R.layout.item_answer, mAnswerList);
        mListview.setAdapter(mAdapter);
//        mPicList = mList.get(current).getImgpath();
        setPicStrList(mList.get(current).getImgpath());
        mPicAdapter = new MyGridAdapter(AnswerActivity.this, R.layout.item_answer_pic, mPicStrList);
        mGridView.setAdapter(mPicAdapter);
    }


    @OnClick({R.id.back, R.id.back_text, R.id.next_answer, R.id.answer_title, R.id.big_title_tv, R.id.pre_answer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                showDialog();
                break;
            case R.id.back_text:
                showDialog();
                break;
            case R.id.next_answer:
                if (pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD)) {
                    nextRecordAnswer();
                } else {
                    jugeNext(false);
                }
                break;
            case R.id.pre_answer:
                jugePre();
                break;
            case R.id.big_title_tv:
                showBigTitle();
                break;
            case R.id.answer_title:

                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (500 > (SystemClock.uptimeMillis() - mHits[0])) {
                    //此处执行双击事件
                    showBigTitle();
                }
                break;
        }
    }


    /**
     * 上一题
     */
    private void jugePre() {
        if (current > 0) {//是否允许上一题
            current--;
            if (current < mList.size() - 1)
                mNextAnswer.setText(R.string.next_answer);
            justSigleText();
            mCurrentAnswer.setText(Html.fromHtml("<font color = '#D52D2C'><big>" + (current + 1) + "</big></font>" + "/" + mList.size()));
            mBigTitleTV.setText(mList.get(current).getContent());
            mAnswerList = mList.get(current).getAnswer();
            mAdapter.notifyDataSetChanged();
            setPicStrList(mList.get(current).getImgpath());
            mPicAdapter.notifyDataSetChanged();

            mRightAnswerTV.setText(Html.fromHtml("正确答案为"
                            + "<font color = '#32B16C'><big>" + changerOrder(mList.get(current).getRightanswer()) + "</big></font>"
                            + "，你的选项为"
                            + "<font color = '#A0CBF0'><big>" + changerOrder(mList.get(current).getMyanswer()) + "</big></font>"
                            + (mList.get(current).getWhether() == 1 ? "，回答正确。" : "，回答错误。")
                    )
            );
        } else if (current <= 0) {
//            current--;
            showToast("没有上一题啦！");
//            httpUploadInfo();
        }
    }

    /**
     * 转换顺序
     */
    public String changerOrder(String a) {
        String var = "";
        String[] ra = a.split("-");
        List<Exam.AnswerBean> answers = mList.get(current).getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            for (int j = 0; j < ra.length; j++) {
                if (answers.get(i).getId().equals(ra[j])) {
                    //记录选中位置
                    var += (i + 1) + "-";
                }
            }
        }
        var = var.substring(0, var.length() - 1);
        var = StringUtils.getSortString(var);
        var = StringUtils.getLetterAZ(var);
        return var;
    }

    /**
     * 考试记录的下一题
     */
    private void nextRecordAnswer() {
        if (current < mList.size() - 1) {//是否允许下一题
            current++;
            if (current == mList.size() - 1)
                mNextAnswer.setText(R.string.finish_look);
            justSigleText();
            mCurrentAnswer.setText(Html.fromHtml("<font color = '#D52D2C'><big>" + (current + 1) + "</big></font>" + "/" + mList.size()));
            mBigTitleTV.setText(mList.get(current).getContent());
            mAnswerList = mList.get(current).getAnswer();
            mAdapter.notifyDataSetChanged();
            setPicStrList(mList.get(current).getImgpath());
            mPicAdapter.notifyDataSetChanged();
            mRightAnswerTV.setText(Html.fromHtml("正确答案为"
                            + "<font color = '#32B16C'><big>" + changerOrder(mList.get(current).getRightanswer()) + "</big></font>"
                            + "，你的选项为"
                            + "<font color = '#A0CBF0'><big>" + changerOrder(mList.get(current).getMyanswer()) + "</big></font>"
                            + (mList.get(current).getWhether() == 1 ? "，回答正确。" : "，回答错误。")
                    )
            );
        } else if (current >= mList.size() - 1) {
            finish();
//            current++;
//            showToast("最后一题" + "答题时间：" + countTimes / 1000 + "s");
//            httpUploadInfo();
        }
    }

    /**
     * 判断下一题
     * timeout 判断：是点击进入下一题，还是答题时间到进行下一题！ true: 超时  f：点击
     */
    private void jugeNext(boolean isTimeOut) {
        boolean flag = false;  //判断该题是否选择了。
        String userAnswerS = "";
        for (int i = 0; i < mAnswerList.size(); i++) {
            if (mAnswerList.get(i).isChecked()) {
                flag = true;
                userAnswerS += mAnswerList.get(i).getId() + "-";
            }
        }
        if (!flag && !isTimeOut) { //点击
            showToast(getString(R.string.toast_select_option));
            return;
        }
        if (current <= mList.size() - 1) {
            useTime = mList.get(current).getTimes() - mCountdown.getRemainTime();//答题所用时间
            setUserAnswer(userAnswerS); //设置用户选择的答案，和设置正确答案
            savaAnswers(); //保存该题的选择状态
            countTimes += useTime; //答题时间累加
        }
        mCountdown.stop();
        if (current < mList.size() - 1) {//是否允许下一题
            current++;
            if (current == mList.size() - 1)
                mNextAnswer.setText(R.string.submit_exam);
            mCountdown.start(mList.get(current).getTimes());
            justSigleText();
            mCurrentAnswer.setText(Html.fromHtml("<font color = '#D52D2C'><big>" + (current + 1) + "</big></font>" + "/" + mList.size()));
            mBigTitleTV.setText(mList.get(current).getContent());
            mAnswerList = mList.get(current).getAnswer();
            mAdapter.notifyDataSetChanged();
            setPicStrList(mList.get(current).getImgpath());
            mPicAdapter.notifyDataSetChanged();
        } else if (current >= mList.size() - 1) {
            current++;
//            showToast("最后一题" + "答题时间：" + countTimes / 1000 + "s");
            httpUploadInfo();

        }

    }

    /**
     * 保存答案操作
     */
    private void savaAnswers() {
        Exam exam = mList.get(current);
        ExamResult result = new ExamResult();
        result.setQuizid(exam.getUuid());
        result.setAnswer(new Gson().toJson(exam.getAnswer()).toString());//保存answer集合 转成JSONArray 然后在转成String
        result.setMyanswer(userAnswer);
        result.setWhether(isRight);
        result.setTimes(useTime);
        mResultList.add(result);
    }

    private void showDialog() {
        if (position == -1 && !pageFlag.equals(Constant.RECORD_PAGE_FLAG_RECORD)) {
            CommentDialog.showDialog(AnswerActivity.this);
            CommentDialog.setmDialogListener(new DialogListener() {
                @Override
                public void yesClickListener() {
                    finish();
                }

                @Override
                public void noClickListener() {

                }
            });
        } else {
            finish();
        }


    }

    /**
     * 放大题目
     */
    public void showBigTitle() {
        isBig = !isBig;
        if (isBig) {
            mBigTitleTV.setVisibility(View.VISIBLE);
            mAllLayout.setVisibility(View.GONE);
        } else {
            mBigTitleTV.setVisibility(View.GONE);
            mAllLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断单选还是多选 做出提示
     */
    private void justSigleText() {
        String html = "";
        if (mList.get(current).getIssingle() == 0) { //0：多选 1单选
            html = "<font color = '#D52D2C'>" + "（多选）" + "</font>";
        } else if (mList.get(current).getIssingle() == 1) {
            html = "<font color = '#D52D2C'>" + "（单选）" + "</font>";
        }
        mAnswerTitle.setText(Html.fromHtml(html + mList.get(current).getContent()));
    }

    /**
     * 单选多选的 操作 逻辑
     *
     * @param position
     */
    private void selectOption(int position) {
        if (mList.get(current).getIssingle() == 0) { //0：多选 1单选
            if (mAnswerList.get(position).isChecked()) {
                mAnswerList.get(position).setChecked(false);
            } else {
                mAnswerList.get(position).setChecked(true);
            }
        } else if (mList.get(current).getIssingle() == 1) { //单选
            if (!mAnswerList.get(position).isChecked()) {
                for (int i = 0; i < mAnswerList.size(); i++) {
                    if (i == position) {
                        mAnswerList.get(i).setChecked(true);
                    } else {
                        mAnswerList.get(i).setChecked(false);
                    }
                }
            }
        }
        mAdapter.notifyDataSetInvalidated();
    }

    private void goPhotoView(int position) {
//        Intent intent = new Intent(AnswerActivity.this, DragPhotoActivity.class);
//        int location[] = new int[2];
//        mGridView.getLocationOnScreen(location);
//        intent.putExtra("left", location[0]);
//        intent.putExtra("top", location[1]);
//        intent.putExtra("height", mGridView.getHeight());
//        intent.putExtra("width", mGridView.getWidth());
//
//        intent.putStringArrayListExtra("pathList", (ArrayList<String>) mPicStrList);
//        intent.putExtra("position", position);
//        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putSerializable("pathList", (Serializable) mPicStrList);
        bundle.putInt("position", position);
        go(BigPhotoActivity.class, bundle);
        overridePendingTransition(0, 0);
    }

    /**
     * 获取图片实体类中图片路径 转换成List<String>
     *
     * @param mPicList
     */
    public void setPicStrList(List<Exam.ImgpathBean> mPicList) {
        mPicStrList.clear();
        for (int i = 0; i < mPicList.size(); i++) {
            mPicStrList.add(ConnectUrl.PICURL + mPicList.get(i).getPath());
        }
    }

    public void setUserAnswer(String answer) { //赋值操作
        if (TextUtils.isEmpty(answer)) {
            this.userAnswer = "null";
            this.isRight = 0;
            return;
        }
        this.userAnswer = StringUtils.getSortString(answer.substring(0, answer.length() - 1));
        this.isRight = userAnswer.equals(mList.get(current).getRightanswer()) ? 1 : 0;
    }


    public void httpUploadInfo() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.SAVE_EXAM_RECORD, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        request.add("data", new Gson().toJson(mResultList).toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                Log.d("答题提交", response.get().toString());
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {

            }

            @Override
            public void onFinish(int what) {
                hideLoading();
                Bundle bundle = new Bundle();
                bundle.putLong("time", countTimes);
                bundle.putSerializable("resultList", (Serializable) mResultList);
                bundle.putSerializable("originList", (Serializable) mList);
                bundle.putString("idStr", idStr);
                goAndFinish(FinishExamActivity.class, bundle);
            }
        });

    }

    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            TextView t = holder.getView(R.id.title);
            TextView content = holder.getView(R.id.content);
            if (mAnswerList.get(position).isChecked()) {
                t.setTextColor(getResources().getColor(R.color.colorTopic));
                content.setTextColor(getResources().getColor(R.color.colorTopic));
            } else {
                t.setTextColor(getResources().getColor(R.color.black));
                content.setTextColor(getResources().getColor(R.color.blackText));
            }
            t.setText(AZChange.getLetter(position) + ".");
            content.setText(mAnswerList.get(position).getName() + "");
        }
    }

    class MyGridAdapter extends CommonAdapter {

        public MyGridAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView image = holder.getView(R.id.image);
            ImageUtils.setDefaultNormalImage(image, mPicStrList.get(position), R.drawable.pic_fail);
        }
    }


    @Override
    public void onBackPressed() {
        showDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
