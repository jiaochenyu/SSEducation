package com.ss.education.ui.activity.classes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.ui.fragment.classes.ClassesShenheFragment;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeacherClassesShenheActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.tablayout)
    TabLayout mTablayout;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    private String classid = "";
    private String classbh = "";
    private int position = 0;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String[] titles = new String[2];
    private MyFragmentPagerAdapter mAdapter;
    List<ClassesShenheFragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classid = extras.getString("classid");
            classbh = extras.getString("classbh");
            position = extras.getInt("position", 0);
        }
    }

    private void initView() {
        mTitle.setText("审核列表");
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.addTab(mTablayout.newTab().setText("加入班级"));
        mTablayout.addTab(mTablayout.newTab().setText("退出班级"));
        titles = new String[]{
                mTablayout.getTabAt(0).getText().toString(),
                mTablayout.getTabAt(1).getText().toString()
        };
        mTablayout.setupWithViewPager(mViewpager);
    }

    private void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(ClassesShenheFragment.newInstance(0, 0, classbh));
        mFragments.add(ClassesShenheFragment.newInstance(3, 0, classbh));
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);

    }


    @OnClick({R.id.back, R.id.refuse, R.id.agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.refuse:
                if (mViewpager.getCurrentItem() == 0) {  //
                    httpAgreeOrRefuse(2);
                } else {
                    httpAgreeOrRefuse(4);
                }

                break;
            case R.id.agree:
                if (mViewpager.getCurrentItem() == 0) {  //  申请加入班级界面
                    httpAgreeOrRefuse(1);
                } else {  // 学生申请退出班级界面
                    httpDeleteStudent(0);
                }
                break;
        }
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.equals(Constant.EvFinishShenhe)) {
            int page = eventFlag.getPosition();
            int num = (int) eventFlag.getObject();
            if (page == 0) {
                mTablayout.getTabAt(0).setText("加入班级(" + num + ")");
            } else {
                mTablayout.getTabAt(1).setText("退出班级(" + num + ")");
            }
        }
    }

    /**
     * fragment 数据请求完毕，改变activity 样式
     */


    //申请加入班级  state 1同意  2拒绝
    private void httpAgreeOrRefuse(final int state) {
        final ClassesShenheFragment fragment = mFragments.get(mViewpager.getCurrentItem());
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.TEACHER_SHENHE_CLASS, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuids", fragment.getStudentId());
            jsonObject.put("classbh", classbh);
            jsonObject.put("state", state);
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
                            fragment.removeItem();
                        } else {
//                            showToast(object.getString("msg"));
                        }
                        showToast(object.getString("msg"));
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
                hideLoading();
            }


        });
    }


    private void httpDeleteStudent(final int state) {
        final ClassesShenheFragment fragment = mFragments.get(mViewpager.getCurrentItem());
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.DELETE_CLASS_STUDENT, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuids", fragment.getStudentId());
            jsonObject.put("classbh", classbh);
            jsonObject.put("type", "agree");
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
                            fragment.removeItem();
                        } else {
//                            showToast(object.getString("msg"));
                        }
                        showToast(object.getString("msg"));
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
                hideLoading();
            }


        });
    }


    class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            if (position == 0) {
//                ////0 加入班级申请   3 退出班级申请
//                return ClassesShenheFragment.newInstance(new ArrayList<Student>(), 0, position, classbh);
//            } else if (position == 1) {
//                return ClassesShenheFragment.newInstance(new ArrayList<Student>(), 3, position, classbh);
//            }
//            return ClassesShenheFragment.newInstance(new ArrayList<Student>(), 0, position, classbh);
            return mFragments.get(position);
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
