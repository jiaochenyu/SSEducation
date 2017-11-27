package com.ss.education.ui.activity.classes;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.NoScrolExpandablelListView;
import com.ss.education.entity.Classes;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.Parent;
import com.ss.education.entity.Student;
import com.ss.education.entity.StudentGroup;
import com.ss.education.ui.activity.classes.homework.HomeWorkListActivity;
import com.ss.education.utils.ImageUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.R.id.j_name;

public class TeacherClassDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mSettingTV;
    @BindView(R.id.time)
    TextView mTimeTV;
    @BindView(R.id.num)
    TextView mStudentNumTV;
    @BindView(R.id.name)
    TextView mNameTV;
    @BindView(R.id.content)
    TextView mCodeTV;
    @BindView(R.id.shuoming)
    TextView mShuomingTV;
    @BindView(R.id.daishenhe_num)
    TextView mDaishenheNumTV;
    @BindView(R.id.daishenhe)
    TextView mDaishenheTV;
    @BindView(R.id.icon)
    ImageView mClassIconIV;

    @BindView(R.id.exapandablelistview)
    NoScrolExpandablelListView mExapandablelistview;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    private PopupWindow mPopupWindow;

    private List<StudentGroup> mGroupList;
    private String classbh = "";
    private int position = 0;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private MyExpandableListViewAdapter mAdapter;
    private int countStudents = 0;
    private Classes mClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_detail);
        ButterKnife.bind(this);
        initView();
        initPop();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classbh = extras.getString("classbh");
            position = extras.getInt("position", 0);
        }
    }

    private void initView() {
        mTitle.setText("班级详情");
        mSettingTV.setVisibility(View.VISIBLE);
        mSettingTV.setText("设置");
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
        mExapandablelistview.setGroupIndicator(null); // 指示器

    }

    private void initData() {
        mGroupList = new ArrayList<>();
        mAdapter = new MyExpandableListViewAdapter();
        mExapandablelistview.setAdapter(mAdapter);
        httpGetClassData();
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGroupList.clear();
                httpGetClassData();
            }
        });
        //父布局
        mExapandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });

        // 子布局
        mExapandablelistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Bundle bundle = new Bundle();
                bundle.putString("studentId", mGroupList.get(groupPosition).getStudents().get(childPosition).getUuid());
                bundle.putSerializable("student", mGroupList.get(groupPosition).getStudents().get(childPosition));
                bundle.putSerializable("groupList", (Serializable) mGroupList);
                bundle.putString("belongGroupId", mGroupList.get(groupPosition).getGroupid());
                bundle.putString("classId", mClasses.getUuid());
                go(StudentDetailActivity.class, bundle);
                return false;

            }
        });
    }

    private void initPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_setting, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        TextView mStudentManageTV = (TextView) popupView.findViewById(R.id.student_manage);
        final TextView mGroupManage = (TextView) popupView.findViewById(R.id.group_manager);
        mGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if (adjustList(mGroupList)) {
                    Bundle b = new Bundle();
                    b.putSerializable("groupList", (Serializable) mGroupList);
                    b.putString("classid", mClasses.getUuid());
                    go(GroupManageActivity.class, b);
                }

            }
        });
        mStudentManageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if (adjustList(mGroupList.get(0).getStudents())) {
                    Bundle b = new Bundle();
                    b.putSerializable("data", (Serializable) mGroupList.get(0).getStudents());
                    b.putString("classid", mClasses.getUuid());
                    b.putString("classbh", mClasses.getClassbh());
                    go(StudentManageActivity.class, b);
                }
            }
        });
    }

    private void httpGetClassData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_DETAIL, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", classbh);
            jsonObject.put("state", 1); //1同意
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
                            JSONObject json = object.getJSONObject("object");
                            mClasses = gson.fromJson(json.toString(), Classes.class);
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
                httpGetListData();
            }
        });


    }


    //获取班级学生列表
    private void httpGetListData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_STUDENT_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", classbh);
            jsonObject.put("state", 1); //1同意
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
                                StudentGroup sGroup = gson.fromJson(array.getJSONObject(i).toString(), StudentGroup.class);
                                JSONArray studentArray = array.getJSONObject(i).getJSONArray("array");
                                if (i == 0) {//全部 学生
                                    countStudents = studentArray.length();
                                }
                                List<Student> sList = new ArrayList();
                                for (int j = 0; j < studentArray.length(); j++) {

                                    Student s = gson.fromJson(studentArray.getJSONObject(j).toString(), Student.class);
                                    JSONObject pObject = null;
                                    if (studentArray.getJSONObject(j).has("bdObject")) {
                                        pObject = studentArray.getJSONObject(j).getJSONObject("bdObject");
                                        Parent p = gson.fromJson(pObject.toString(), Parent.class);
                                        s.setParent(p);
                                    }
                                    sList.add(s);
                                }
                                sGroup.setStudents(sList);
                                mGroupList.add(sGroup);
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
                setView();

            }
        });
    }

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mGroupList)) {
            mNullBg.setVisibility(View.GONE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
        try {

            mTimeTV.setText("" + mClasses.getCreatetime().split(" ")[0]);
            mStudentNumTV.setText("人数：" + mClasses.getClassrs());
            mNameTV.setText("名称：" + mClasses.getClassname());
            mCodeTV.setText("班级号：" + mClasses.getClassbh());
            ImageUtils.setCircleDefImage(mClassIconIV, ConnectUrl.PICURL + mClasses.getImgpath(), R.drawable.icon_class8);
            if (MyApplication.getUser().getPart().equals("T")) {
                mShuomingTV.setText(mClasses.getRemarks());
                int shrs = mClasses.getShrs();
                if (shrs <= 0) {
                    mDaishenheNumTV.setVisibility(View.GONE);
                    mDaishenheTV.setVisibility(View.GONE);
                } else {
                    mDaishenheNumTV.setVisibility(View.VISIBLE);
                    mDaishenheTV.setVisibility(View.VISIBLE);
                    if (shrs > 99) {
                        mDaishenheNumTV.setText("99+");
                    } else {
                        mDaishenheNumTV.setText(shrs + "");
                    }
                }
            } else if (MyApplication.getUser().getPart().equals("S")) {
                mShuomingTV.setText("老师：" + mClasses.getTeachername());
            } else {
                //家长
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvUpadeClassS)) {
            mGroupList.clear();
            httpGetClassData();
        }
        if (eventFlag.getFlag().equals(Constant.EvUpdateClassDetail)) {
            mGroupList.clear();
            httpGetClassData();
        }
        if (eventFlag.getFlag().equals(Constant.EvUpdateClassGroup)) {
            mGroupList.clear();
            httpGetClassData();
        }
        if (eventFlag.getFlag().equals(Constant.EvMoveStudentFromGroup)) {
            mGroupList.clear();
            httpGetClassData();
        }
    }

    /**
     * 弹出popupwindow
     **/
    private void showPop() {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
//            mPopupWindow.showAsDropDown(mSettingTV, Gravity.BOTTOM, 0, 0);
            mPopupWindow.showAsDropDown(findViewById(R.id.top_layout));
        }
    }

    @OnClick({R.id.back, R.id.right_text, R.id.edit_detail, R.id.daishenhe, R.id.fenxi_layout, R.id.homework_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                showPop();
                break;
            case R.id.daishenhe:
                if (mClasses!=null){
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("classbh", mClasses.getClassbh());
                    bundle2.putString("classid", mClasses.getUuid());
                    bundle2.putInt("position", position);
                    go(TeacherClassesShenheActivity.class, bundle2);
                }

                break;
            case R.id.edit_detail:
                //修改详情
                if (mClasses != null) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("classes", mClasses);
                    go(AddClassActivity.class, bundle1);
                }
                break;
            case R.id.fenxi_layout:

                break;
            case R.id.homework_layout:
                if (mClasses != null) {
                    Bundle b = new Bundle();
                    b.putSerializable("classid", mClasses.getUuid());
                    b.putSerializable("dataList", (Serializable) mGroupList);
                    go(HomeWorkListActivity.class, b);
                }

                break;

        }
    }

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).getStudents().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).getStudents().get(childPosition);
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
                convertView = LayoutInflater.from(TeacherClassDetailActivity.this).inflate(R.layout.item_class_group, null);
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

            groupHolder.mNameTV.setText(mGroupList.get(groupPosition).getGroupname());
            try {
                if (groupPosition == 0) {
                    groupHolder.mNumTV.setText(countStudents + "");
                } else {
                    groupHolder.mNumTV.setText(mGroupList.get(groupPosition).getStudents().size() + "/" + countStudents);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ChildHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(TeacherClassDetailActivity.this).inflate(R.layout.item_class_student, null);
                holder = new ChildHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.j_name = (TextView) convertView.findViewById(j_name); //家长
                holder.content = (TextView) convertView.findViewById(R.id.content);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            holder.checkBox.setFocusable(false);
            holder.checkBox.setClickable(false);

            if (mGroupList.get(groupPosition).getStudents().get(childPosition).getParent() != null) {
                holder.j_name.setVisibility(View.VISIBLE);
                holder.j_name.setText("家长：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getParent().getRealname());
            } else {
                holder.j_name.setVisibility(View.GONE);
            }

            try {
                holder.name.setText("姓名：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getRealname());
                holder.content.setText("学号：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getXjh());
                ImageUtils.setCircleDefImage(
                        holder.icon,
                        ConnectUrl.PICURL + mGroupList.get(groupPosition).getStudents().get(childPosition).getImgpath(),
                        R.drawable.icon_header4
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private class GroupHolder {
            public TextView mNameTV;
            public ImageView imgArrow;
            public TextView mNumTV;
        }

        private class ChildHolder {
            ImageView icon;
            TextView name;
            TextView j_name; //家长
            TextView content;
            CheckBox checkBox;

        }
    }


}
