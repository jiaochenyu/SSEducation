package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.ss.education.customview.popupwindow.ExpandTabView;
import com.ss.education.customview.popupwindow.PopViewList;
import com.ss.education.entity.ExamErrorSection;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamErrorSectionListActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.expandtabTab)
    ExpandTabView mExpandtabTab;
    @Bind(R.id.listview)
    ListView mListview;
    @Bind(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.pop_bg_view)
    View v_bg;
    @Bind(R.id.null_bg)
    RelativeLayout mNullBg;
    PopViewList viewXueke;

    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private List<ExamErrorSection> mList;
    private List<ExamErrorSection> mNewList;
    private MyAdapter mAdapter;


    private ArrayList<String> mExpandArray = new ArrayList<>();  //筛选
    private ArrayList<View> mViewArray = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_error_section_list);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        mTitle.setText("我的错题");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
    }

    private void initData() {
        mList = new ArrayList<>();
        mNewList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_error_section, mNewList);
        mListview.setAdapter(mAdapter);
        mExpandArray.add("全部学科");
        viewXueke = new PopViewList(this, Constant.XUEKELSIT, Constant.XUEKELSITVALUES, 1);
        mViewArray.add(viewXueke);
        mExpandtabTab.setValue(mExpandArray, mViewArray, v_bg);

        httpGetData();

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                mNewList.clear();
                httpGetData();
                onExpandRefresh(viewXueke,"全部");
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
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("sectionid", mNewList.get(position).getUuid());
                go(ExamErrorListActivity.class, bundle);
            }
        });
    }


    private void httpGetData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_EXAM_ERROR_RECORD_SECTION_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("whether", "0");
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
                                ExamErrorSection section = gson.fromJson(array.getJSONObject(i).toString(), ExamErrorSection.class);
                                mList.add(section);
                            }
                            mNewList.addAll(mList);
                            Log.e("章节大小", mList.size() + "");

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

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mNewList)) {
//            mSwipeRefresh.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
            mListview.setVisibility(View.VISIBLE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
//            mSwipeRefresh.setVisibility(View.GONE);
            mListview.setVisibility(View.GONE);
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
            TextView xueke = holder.getView(R.id.xueke);
            TextView zhangjie = holder.getView(R.id.zhangjie);
            TextView time = holder.getView(R.id.time);

            xueke.setText("学科:" + mNewList.get(position).getCoursename());
            zhangjie.setText("章节:" + mNewList.get(position).getSectionname());

        }
    }
}
