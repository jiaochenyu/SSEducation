package com.ss.education.ui.activity.teacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.ss.education.entity.Student;
import com.ss.education.ui.activity.examination.ExamRecordActivity;
import com.ss.education.utils.ImageUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentsListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.edit)
    EditText mEditSearch;
    @BindView(R.id.cha)
    ImageView mChaIV;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    RequestQueue mQueue = NoHttp.newRequestQueue();
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    private String mSearchStr = "";
    List<Student> mSearchList;
    List<Student> mList;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }


    private void initData() {
        mSearchList = new ArrayList<>();
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_student_info, mList);
        mListview.setAdapter(mAdapter);
        httpGetData();
    }

    private void initView() {
        mTitle.setText("我的学生");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);

    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                mSearchList.clear();
                mEditSearch.setText("");
                httpGetData();
            }
        });
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (TextUtils.isEmpty(str)) {
                    mChaIV.setVisibility(View.GONE);
                } else {
                    mChaIV.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchMethod(mEditSearch.getText().toString());
                }
                return false;
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("studentId", mList.get(position).getUuid());
                bundle.putSerializable("student",mList.get(position));
                go(ExamRecordActivity.class, bundle);
            }
        });
    }

    //查询操作
    public void searchMethod(String var) {
        if (TextUtils.isEmpty(var)) {
            mSearchList.addAll(mList);
        } else {
            mSearchList.clear();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getNickname().contains(var) || mList.get(i).getRealname().contains(var) || mList.get(i).getUsername().contains(var)) {
                    mSearchList.add(mList.get(i));
                }
            }
        }
        setView();
    }


    private void httpGetData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_MY_STUDENT_LIST, RequestMethod.POST);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("condition", "");
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
                                Student student = gson.fromJson(array.getJSONObject(i).toString(), Student.class);
                                mList.add(student);
                            }
                            mSearchList.addAll(mList);

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

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mSearchList)) {
            mSwipeRefresh.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.back, R.id.cha, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cha:
                mEditSearch.setText("");
                searchMethod("");
                break;
            case R.id.cancel:
                mEditSearch.setText("");
                searchMethod("");
                break;
        }
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
            TextView shuoming = holder.getView(R.id.shuoming);
            TextView time = holder.getView(R.id.time);
            ImageUtils.setCircleDefImage(icon, mSearchList.get(position).getImgpath(), R.drawable.icon_touxiang);
            name.setText(mSearchList.get(position).getRealname());
            content.setText(mSearchList.get(position).getSchoolname());
            try {
                time.setText(mSearchList.get(position).getCreatetime().split(" ")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            shuoming.setText(mList.get(position).getSchoolname());


        }
    }
}
