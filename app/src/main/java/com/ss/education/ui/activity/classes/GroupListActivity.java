package com.ss.education.ui.activity.classes;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.Student;
import com.ss.education.entity.StudentGroup;
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

public class GroupListActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.listview)
    ListView mListview;
    List<StudentGroup> mGroupList = new ArrayList<>();
    String belongGroupId = ""; // 用户所在分组
    MyAdapter mAdapter;
    Student mStudent;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String classId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mGroupList = (List<StudentGroup>) extras.getSerializable("groupList");
            mGroupList.remove(0);
            mStudent = (Student) extras.getSerializable("student");
            belongGroupId = mStudent.getGroupid();
            classId = extras.getString("classId", "");
        }
    }

    private void initView() {
        mTitle.setText("移至分组");
    }

    private void initData() {
        mAdapter = new MyAdapter(this, R.layout.item_class_group, mGroupList);
        mListview.setAdapter(mAdapter);
    }


    private void initListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                belongGroupId = mGroupList.get(position).getGroupid();
                mAdapter.notifyDataSetChanged();
                httpMoveGroup(mGroupList.get(position).getGroupname());
            }
        });
    }

    private void httpMoveGroup(final String groupName) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GROUP_MOVE_STUDENT, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("array", getArray());
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
                            showToast("操作成功！");
                            EventBus.getDefault().post(new EventFlag(Constant.EvMoveStudentFromGroup, groupName));
                            finish();
                        } else {
                            showToast("移至分组失败，请重新操作！");
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
                hideLoading();
            }
        });


    }

    //获取array
    private JSONArray getArray() {
        JSONArray array = new JSONArray();
        try {
            JSONObject object = new JSONObject();
            object.put("classid", classId);
            object.put("userid", mStudent.getUuid());
            object.put("groupid", belongGroupId);
            array.put(object);
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView img = holder.getView(R.id.img);
            TextView txt = holder.getView(R.id.group_name);
            CheckBox cb = holder.getView(R.id.group_check_box);
            img.setVisibility(View.GONE);
            txt.setText(mGroupList.get(position).getGroupname());
            cb.setFocusable(false);
            cb.setClickable(false);
            if (belongGroupId.equals(mGroupList.get(position).getGroupid())) {
                cb.setVisibility(View.VISIBLE);
                cb.setChecked(true);
            } else {
                cb.setVisibility(View.GONE);
            }
        }
    }


}
