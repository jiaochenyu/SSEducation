package com.ss.education.ui.activity.classes.homework;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.HomeworkFeedback;
import com.ss.education.entity.Student;
import com.ss.education.ui.fragment.homework.HomeworkCompletionFragment;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeworkCompletionActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.tablayout)
    TabLayout mTablayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    String workId = "";
    String submitFlag = "F";
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    List<HomeworkFeedback> mFeedbacks1;
    List<HomeworkFeedback> mFeedbacks2;
    MyFragmentPagerAdapter mAdapter;
    private String[] titles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_complete);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            workId = extras.getString("id", "");
            submitFlag = extras.getString("submitFlag", "F");
        }
    }

    private void initView() {
        mTitle.setText("完成情况");
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        if (submitFlag.equals("F")) {
            mTablayout.addTab(mTablayout.newTab().setText("已查看"));
            mTablayout.addTab(mTablayout.newTab().setText("未查看"));
        } else {
            mTablayout.addTab(mTablayout.newTab().setText("已提交"));
            mTablayout.addTab(mTablayout.newTab().setText("未提交"));
        }
        titles = new String[]{
                mTablayout.getTabAt(0).getText().toString(),
                mTablayout.getTabAt(1).getText().toString()
        };

        mTablayout.setupWithViewPager(mViewpager);
    }

    private void initData() {
        mFeedbacks1 = new ArrayList<>();
        mFeedbacks2 = new ArrayList<>();

        httpGetData();
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvFinishEvaluate)) {
            mFeedbacks1.clear();
            mFeedbacks2.clear();
            httpGetData();
        }

    }

    private void httpGetData() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_HOME_WORK_FEEDBACK_TEACHER, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        JSONObject object = new JSONObject();
        try {
            object.put("workid", workId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", object.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            JSONObject object1 = jsonObject.getJSONArray("array").getJSONObject(0);
                            JSONObject object2 = jsonObject.getJSONArray("array").getJSONObject(1);
                            JSONArray array1 = object1.getJSONArray("array");
                            JSONArray array2 = object2.getJSONArray("array");
                            for (int i = 0; i < array1.length(); i++) {
                                HomeworkFeedback h = gson.fromJson(array1.getJSONObject(i).toString(), HomeworkFeedback.class);
                                Student s = gson.fromJson(array1.getJSONObject(i).getJSONObject("object").toString(), Student.class);
                                h.setStudent(s);
                                mFeedbacks1.add(h);
                            }
                            for (int i = 0; i < array2.length(); i++) {
                                HomeworkFeedback h = gson.fromJson(array2.getJSONObject(i).toString(), HomeworkFeedback.class);
                                Student s = gson.fromJson(array2.getJSONObject(i).getJSONObject("object").toString(), Student.class);
                                h.setStudent(s);
                                mFeedbacks2.add(h);
                            }
                            setView();

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("上传", response.get().toString());
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {

                }
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
            }
        });
    }

    private void setView() {
        int a = mFeedbacks1.size();
        int b = mFeedbacks2.size();
        String str1 = "";
        String str2 = "";
        if (submitFlag.equals("F")) {
            str1 = "已查看(" + a + ")";
            str2 = "未查看(" + b + ")";

        } else {
            str1 = "已提交(" + a + ")";
            str2 = "未提交(" + b + ")";
        }
        mTablayout.getTabAt(0).setText(str1);
        mTablayout.getTabAt(1).setText(str2);
        titles[0] = str1;
        titles[1] = str2;
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return HomeworkCompletionFragment.newInstance(mFeedbacks1, submitFlag, position);
            } else if (position == 1) {
                return HomeworkCompletionFragment.newInstance(mFeedbacks2, submitFlag, position);
            }
            return HomeworkCompletionFragment.newInstance(new ArrayList<HomeworkFeedback>(), submitFlag, position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


}
