package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.ss.education.MainActivity;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.entity.Exam;
import com.ss.education.entity.ExamResult;
import com.ss.education.utils.StringUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.R.id.chart;

;

/**
 * 考试结束
 */
public class FinishExamActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.back_text)
    TextView mBackText;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.score)
    TextView mScoreTV;
    @BindView(R.id.error_count)
    TextView mErrorCountTV;
    @BindView(R.id.use_time)
    TextView mUseTimeTV;
    @BindView(R.id.share_btn)
    Button mShareBtn;
    @BindView(R.id.continue_btn)
    Button mContinueBtn;
    @BindView(R.id.gridview)
    GridView mGridView;
    @BindView(chart)
    PieChart mChart;
    PieData mPieData;

    private GridviewAdapter mAdapter;
    private List<ExamResult> mResultList;
    private List<Exam> mExamList;
    private Long times = 0L;
    private float quarterlyRight = 0f; //正确率
    private int rightNum = 0;

    private String idStr = ""; //再来试题 需要传章节id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_exam);
        ButterKnife.bind(this);
        dealData();
        initView();
        initListener();
    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mResultList = (List<ExamResult>) extras.getSerializable("resultList");
            mExamList = (List<Exam>) extras.getSerializable("originList");
            times = extras.getLong("time");
            idStr = extras.getString("idStr");
        }
    }

    private void dealData() {
        mAdapter = new GridviewAdapter(FinishExamActivity.this, R.layout.item_finish_gridview, mResultList);
        mGridView.setAdapter(mAdapter);
        for (int i = 0; i < mResultList.size(); i++) {
            if (mResultList.get(i).getWhether() == 1) {
                rightNum++;
            }
        }
        quarterlyRight = ((float) rightNum / mResultList.size()) * 100;

        mScoreTV.setText(StringUtils.format(quarterlyRight));
        mErrorCountTV.setText(Html.fromHtml("错题 " + "<font color = '#FFFFFF'><big>" + (mResultList.size() - rightNum) + "</big></font>" + "/" + mResultList.size()));
        mUseTimeTV.setText("用时 " + StringUtils.getTime(times));
    }

    private void initView() {
        mTitle.setText(R.string.finish_exam_ac);
        mBackText.setVisibility(View.VISIBLE);
        initChartView();
    }

    /**
     * 初始化折线图
     */
    private void initChartView() {
        setPieData();
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        mChart.setDescription(null);
        mChart.setDrawEntryLabels(false);
        mChart.setEnabled(false);
        mChart.setUsePercentValues(true);
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setData(mPieData); //绑定数据
        mChart.setHoleColor(Color.rgb(255, 255, 255));
        mChart.setDragDecelerationEnabled(false);
        mChart.setHoleRadius(90f); //半径
        mChart.setDrawCenterText(true);//饼状图中间可以添加文字
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        mChart.setUsePercentValues(false);//设置显示成比例
        mChart.setRotationAngle(270); // 初始旋转角度
        mChart.setRotationEnabled(false); // 可以手动旋转
        mChart.setTouchEnabled(false);
//        mChart.animateY(1000, Easing.EasingOption.EaseInOutQuad); //设置动画
    }

    /**
     * 设置饼图的数据
     */
    private void setPieData() {
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        for (int i = 0; i < 2; i++) {
            xValues.add("Quarterly" + (i + 1));  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
        }
        List<PieEntry> yValues = new ArrayList<PieEntry>();  //yVals用来表示封装每个饼块的实际数据
        /** // 饼图数据
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        float quarterlyError = 100 - quarterlyRight;
        yValues.add(new PieEntry(quarterlyRight, 1));
        yValues.add(new PieEntry(quarterlyError, 2));
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色
        colors.add(Color.rgb(255, 242, 0));
        colors.add(Color.rgb(195, 195, 195));
        pieDataSet.setColors(colors);
        mPieData = new PieData(pieDataSet);
        mPieData.setDrawValues(false);
    }


    private void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("originList", (Serializable) mExamList);
                bundle.putInt("position", position);
                bundle.putSerializable("resultList", (Serializable) mResultList);
                go(AnswerActivity.class, bundle);
            }
        });
    }

    @OnClick({R.id.back, R.id.back_text, R.id.share_btn, R.id.continue_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                go(MainActivity.class);
                break;
            case R.id.back_text:
                go(MainActivity.class);
                break;
            case R.id.share_btn:

                break;
            case R.id.continue_btn:
                Bundle bundle = new Bundle();
                bundle.putString("idStr", idStr);
                goAndFinish(AnswerActivity.class, bundle);
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
            if (mResultList.get(position).getWhether() == 1) {
                mBtn.setBackground(getResources().getDrawable(R.drawable.bg_corner_green));
            } else {
                mBtn.setBackground(getResources().getDrawable(R.drawable.bg_corner_red));
            }
            mBtn.setFocusable(false);
            mBtn.setClickable(false);
        }
    }


}
