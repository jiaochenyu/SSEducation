package com.ss.education.ui.activity.examination;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.Section;
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

/**
 * 章节列表
 */
public class PracticeActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.listview)
    ListView mListView;
    @BindView(R.id.practice_next)
    Button mPracticeBtn;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    private String title;
    private List<Section> mList;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private MyAdapter mAdapter;
    String idStr = ""; //拼接的章节id " ** - ***"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            title = extras.getString("title");
        }
    }


    private void initView() {
        mTitle.setText(title);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this, R.layout.item_practice_listview, mList);
        mListView.setAdapter(mAdapter);
        httpData();
    }


    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).isChecked()) {
                    mList.get(position).setChecked(false);
                } else {
                    mList.get(position).setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void httpData() {
        Request<JSONObject> mRequest = NoHttp.createJsonObjectRequest(ConnectUrl.GET_SECTION_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("coursename", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequest.add("data", jsonObject.toString());
        mRequest.add("useruuid", MyApplication.getUser().getUuid());
        mQueue.add(Constant.REQUEST_WHAT, mRequest, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Gson gson = new Gson();
                    JSONObject json = response.get();
                    try {
                        String code = json.getString("status");
                        if (code.equals("100")) {
                            JSONArray array = json.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                Section section = gson.fromJson(array.getJSONObject(i).toString(), Section.class);
                                mList.add(section);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
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
                if (!adjustList(mList)) {
                    mNullBg.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    mPracticeBtn.setVisibility(View.GONE);
                } else {
                    mNullBg.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPracticeBtn.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @OnClick({R.id.back, R.id.practice_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.practice_next:
                dealData();
                break;
        }
    }

    //处理数据
    private void dealData() {
        idStr = "";
        for (Section s : mList) {
            if (s.isChecked()) {
                idStr += s.getUuid() + "-";
            }
        }
        if (TextUtils.isEmpty(idStr)) {
            showToast(getString(R.string.toast_select_section));
            return;
        } else {
            idStr = idStr.substring(0, idStr.length()-1);
        }
        Log.d("id拼接处理最后一个", "dealData: " + idStr);
        Bundle bundle = new Bundle();
        bundle.putSerializable("idStr", idStr);
        go(AnswerActivity.class,bundle);

    }

    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            TextView name = holder.getView(R.id.name);
            name.setText(mList.get(position).getSectionname());
            CheckBox checkBox = holder.getView(R.id.checkbox);
            checkBox.setFocusable(false);
            checkBox.setClickable(false);
            if (mList.get(position).isChecked()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }
}
