package com.ss.education.ui.activity.classes.homework;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.NoScrolExpandablelListView;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.HomeWorkDate;
import com.ss.education.entity.HomeWorkInfo;
import com.ss.education.entity.StudentGroup;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeWorkListActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.right_text)
    TextView mReleaseTV;
    @Bind(R.id.expandlistview)
    NoScrolExpandablelListView mExpandablelListView;
    @Bind(R.id.null_bg)
    RelativeLayout mNullBg;
    @Bind(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    String classid = "";
    private List<StudentGroup> mGroupList;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private MyExpandableListViewAdapter mAdapter;

    private List<HomeWorkDate> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_list);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        httpGetData();

    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classid = extras.getString("classid");
            mGroupList = (List<StudentGroup>) extras.getSerializable("dataList");
        }
    }

    private void initView() {
        mTitle.setText("作业");
        if (MyApplication.getUser().getPart().equals("T")) {
            mReleaseTV.setVisibility(View.VISIBLE);
            mReleaseTV.setText("我要发布");
        } else if (MyApplication.getUser().getPart().equals("S")) {
            mReleaseTV.setVisibility(View.GONE);

        }
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);

    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyExpandableListViewAdapter();
        mExpandablelListView.setAdapter(mAdapter);
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                httpGetData();
            }
        });
        mExpandablelListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mExpandablelListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (adjustList(mList)) {
                    Bundle b = new Bundle();
                    b.putSerializable("homeDetail", (Serializable) mList.get(groupPosition).getHomeWorkInfos().get(childPosition));
                    go(HomeWorkDetailActivity.class, b);
                }
                return false;

            }
        });


    }


    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvReleaseSucc)) {
            mList.clear();
            httpGetData();
        }
    }

    private void httpGetData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_HOME_WORK_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classid", classid);
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
                            JSONArray array = object.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                String date = o.getString("date");
                                JSONArray array2 = o.getJSONArray("array");
                                List<HomeWorkInfo> infoList = new ArrayList<HomeWorkInfo>();
                                for (int j = 0; j < array2.length(); j++) {
                                    HomeWorkInfo info = gson.fromJson(array2.getJSONObject(j).toString(), HomeWorkInfo.class);
                                    infoList.add(info);
                                }
                                HomeWorkDate workDate = new HomeWorkDate(date, infoList);
                                mList.add(workDate);
                            }
                        } else {
                            showToast(object.getString("msg"));
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


    @OnClick({R.id.back, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                Bundle b = new Bundle();
                b.putSerializable("dataList", (Serializable) mGroupList);
                b.putString("classid", classid);
                go(ReleaseHomeWorkActivity.class, b);
                break;
        }
    }


    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mList.get(groupPosition).getHomeWorkInfos().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mList.get(groupPosition).getHomeWorkInfos().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
         *
         * @return
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(HomeWorkListActivity.this).inflate(R.layout.item_class_group, null);
                groupHolder = new GroupHolder();
                groupHolder.mNameTV = (TextView) convertView.findViewById(R.id.group_name);
                groupHolder.mNumTV = (TextView) convertView.findViewById(R.id.num_student);
                groupHolder.imgArrow = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            if (!isExpanded) {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_group_right);
            } else {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_down_arrow);
            }

            try {
                groupHolder.mNameTV.setText(mList.get(groupPosition).getDate());
//                groupHolder.mNumTV.setText(mList.get(groupPosition).getHomeWorkInfos().size() + "");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ChildHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(HomeWorkListActivity.this).inflate(R.layout.item_class_student, null);
                holder = new ChildHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            try {
                holder.name.setText(mList.get(groupPosition).getHomeWorkInfos().get(childPosition).getTitles());
                holder.content.setText(mList.get(groupPosition).getHomeWorkInfos().get(childPosition).getRemarks());
//                ImageUtils.setCircleDefImage(
//                        holder.icon,
//                        ConnectUrl.PICURL + mGroupList.get(groupPosition).getStudents().get(childPosition).getImgpath(),
//                        R.drawable.icon_header4
//                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            for (int i = 0; i < mList.size(); i++) {
                mExpandablelListView.expandGroup(i);
            }
        }

        private class GroupHolder {
            public TextView mNameTV;
            public ImageView imgArrow;
            public TextView mNumTV;
        }

        private class ChildHolder {
            ImageView icon;
            TextView name;
            TextView content;

        }

    }
}
