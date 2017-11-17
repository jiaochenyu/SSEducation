package com.ss.education.ui.activity.classes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassShenheActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.right_text)
    TextView mRightText;
    @Bind(R.id.listview)
    ListView mListview;
    @Bind(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.null_bg)
    RelativeLayout mNullBg;
    @Bind(R.id.layout_bottom)
    LinearLayout mLayoutBottom;
    private List<Student> mList;
    private MyAdapter mAdapter;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String classid = "";
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_shenhe);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classid = extras.getString("classid");
            position = extras.getInt("position", 0);
        }
    }


    private void initView() {
        mTitle.setText("审核列表");
        mRightText.setVisibility(View.GONE);
        mRightText.setText("批量操作");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_class_student, mList);
        mListview.setAdapter(mAdapter);
        httpGetListData();
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                httpGetListData();
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).isCheck()) {
                    mList.get(position).setCheck(false);
                } else {
                    mList.get(position).setCheck(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    //获取班级学生列表
    private void httpGetListData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_STUDENT_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", classid);
            jsonObject.put("state", 0); //1同意
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
                            JSONArray arra0 = object.getJSONArray("array");
                            JSONObject json = arra0.getJSONObject(0); //全部
                            JSONArray array = json.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                Student s = gson.fromJson(array.getJSONObject(i).toString(), Student.class);
                                mList.add(s);
                            }
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
                mSwipeRefresh.setRefreshing(false);
                setView();
            }
        });

    }

    private void setView() {
        if (adjustList(mList)) {
            mNullBg.setVisibility(View.GONE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.back, R.id.right_text, R.id.refuse, R.id.agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.refuse:
                httpAgreeOrRefuse(2);
                break;
            case R.id.agree:
                httpAgreeOrRefuse(1);
                break;
            case R.id.right_text:
                break;
        }
    }

    //申请加入班级  state 1同意  2拒绝
    private void httpAgreeOrRefuse(final int state) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.TEACHER_SHENHE_CLASS, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuids", getStudentId());
            jsonObject.put("classbh", classid);
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
                            removeItem(state);
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

    /**
     * 审核之后移除item
     */
    private void removeItem(int state) {
        int num = 0;
        List<Student> l = new ArrayList<>();
        l.addAll(mList);
        for (int i = l.size()-1; i >=0; i--) {
            if (l.get(i).isCheck()) {
                mList.remove(i);
                if (state == 1) {
                    num++;
                }
            }
        }
        EventBus.getDefault().post(new EventFlag(Constant.EvUpadeClassS, num, position));
        setView();
    }

    /**
     * 获取学生id集合
     *
     * @return
     */
    private String getStudentId() {
        String idStr = "";
        for (Student s : mList) {
            if (s.isCheck()) {
                idStr += s.getUuid() + "-";
            }
        }
        if (TextUtils.isEmpty(idStr)) {
            showToast(getString(R.string.toast_select_student));
            return "";
        } else {
            idStr = idStr.substring(0, idStr.length() - 1);
        }
        return idStr;
    }


    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {

            ImageView icon = holder.getView(R.id.icon);
            TextView name = holder.getView(R.id.name);
            TextView content = holder.getView(R.id.content);
            CheckBox checkBox = holder.getView(R.id.checkbox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setFocusable(false);
            checkBox.setClickable(false);
            if (mList.get(position).isCheck()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            try {
                name.setText("姓名：" + mList.get(position).getRealname());
                content.setText("编号：" + mList.get(position).getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }

            int p = position % 10;
            switch (p) {
                case 1:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header1));
                    break;
                case 2:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header2));
                    break;
                case 3:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header3));
                    break;
                case 4:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header4));
                    break;
                case 5:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header5));
                    break;
                case 6:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header6));
                    break;
                case 7:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header7));
                    break;
                case 8:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header8));
                    break;
                case 9:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header9));
                    break;
                case 0:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_header10));
                    break;
            }


        }
    }
}
