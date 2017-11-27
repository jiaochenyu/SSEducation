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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.Classes;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.Parent;
import com.ss.education.entity.Student;
import com.ss.education.listener.DialogListener;
import com.ss.education.ui.activity.classes.homework.HomeWorkListActivity;
import com.ss.education.utils.ImageUtils;
import com.ss.education.weight.CommentDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

public class ClassDetailActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mRightText;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.name)
    TextView mNameTV;
    @BindView(R.id.content)
    TextView mCodeTV;
    @BindView(R.id.shuoming)
    TextView mShuomingTV;
    @BindView(R.id.time)
    TextView mTimeTV;
    @BindView(R.id.listview)
    NoScrollListView mListview;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.num)
    TextView mStudentNumTV;
    @BindView(R.id.daishenhe_num)
    TextView mDaishenheNumTV;
    @BindView(R.id.daishenhe)
    TextView mDaishenheTV;
    @BindView(R.id.edit_detail)
    RelativeLayout mEditDetailLayout;
    @BindView(R.id.layout_bottom)
    LinearLayout mLayoutBottom;
    @BindView(R.id.deleteOrSignout)
    TextView mdeleteOroutTV; //学生退出班级/ 老师删除学生
    @BindView(R.id.contact_t)
    TextView mContactTV; //联系老师
    @BindView(R.id.gongneng_layout)
    LinearLayout mGongnengLL; //
    private List<Student> mList;
    private MyAdapter mAdapter;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private String classbh = "";

    private Classes mClasses;

    private int position = 0;

    private boolean isShowEdit = false; // false 显示编辑，  true 显示删除
    private String classid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classbh = extras.getString("classbh");
            classid = extras.getString("classid");
            position = extras.getInt("position", 0);
        }
    }

    private void initView() {
        mTitle.setText("我的班级");

        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
        if (MyApplication.getUser().getPart().equals("T")) {
            mRightText.setVisibility(View.VISIBLE);
            mRightText.setText("编辑");
            mLayoutBottom.setVisibility(View.GONE);
            mContactTV.setVisibility(View.GONE);
        } else if (MyApplication.getUser().getPart().equals("S")) {
            mRightText.setVisibility(View.GONE);
            mDaishenheNumTV.setVisibility(View.GONE);
            mDaishenheTV.setVisibility(View.GONE);
            mLayoutBottom.setVisibility(View.VISIBLE);
        } else if (MyApplication.getUser().getPart().equals("J")) {
            mLayoutBottom.setVisibility(View.VISIBLE);
            mdeleteOroutTV.setVisibility(View.GONE);
            mContactTV.setVisibility(View.VISIBLE);
            mGongnengLL.setVisibility(View.GONE);
        }


    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(ClassDetailActivity.this, R.layout.item_class_student, mList);
        mListview.setAdapter(mAdapter);
        httpGetClassData();
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                httpGetClassData();
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowEdit) {
                    if (mList.get(position).isCheck()) {
                        mList.get(position).setCheck(false);
                    } else {
                        mList.get(position).setCheck(true);
                    }
                } else {
                    if (MyApplication.getUser().getPart().equals("T")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("studentId", mList.get(position).getUuid());
                        bundle.putSerializable("student", mList.get(position));
                        go(StudentDetailActivity.class, bundle);

                    }
                }


                mAdapter.notifyDataSetChanged();
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
                            JSONArray arra0 = object.getJSONArray("array");

                            JSONObject json = arra0.getJSONObject(0); //全部
                            JSONArray array = json.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                Student s = gson.fromJson(array.getJSONObject(i).toString(), Student.class);
                                JSONObject pObject = null;
                                if (array.getJSONObject(i).has("bdObject")) {
                                    pObject = array.getJSONObject(i).getJSONObject("bdObject");
                                    Parent p = gson.fromJson(pObject.toString(), Parent.class);
                                    s.setParent(p);
                                }
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
                setView();

            }
        });

    }

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mList)) {
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
            ImageUtils.setCircleDefImage(mIcon, ConnectUrl.PICURL + mClasses.getImgpath(), R.drawable.icon_class8);

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


    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvUpadeClassS)) {
            mList.clear();
            httpGetClassData();
        }
        if (eventFlag.getFlag().equals(Constant.EvUpdateClassDetail)) {
            mList.clear();
            httpGetClassData();
        }
    }


    @OnClick({R.id.back, R.id.right_text, R.id.daishenhe_num, R.id.daishenhe, R.id.edit_detail, R.id.deleteOrSignout, R.id.contact_t, R.id.homework_layout, R.id.fenxi_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                setEditView();
                break;
            case R.id.daishenhe_num:
                Bundle bundle = new Bundle();
                bundle.putString("classid", mClasses.getClassbh());
                bundle.putInt("position", position);
                go(ClassShenheActivity.class, bundle);
                break;
            case R.id.daishenhe:
                Bundle bundle2 = new Bundle();
                bundle2.putString("classid", mClasses.getClassbh());
                bundle2.putInt("position", position);
                go(ClassShenheActivity.class, bundle2);
                break;
            case R.id.edit_detail:
                //修改详情
                if (mClasses != null && MyApplication.getUser().getPart().equals("T")) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("classes", mClasses);
                    go(AddClassActivity.class, bundle1);
                } else {

                }
                break;
            case R.id.deleteOrSignout:
                if (MyApplication.getUser().getPart().equals("S")) {
                    CommentDialog.showComfirmDialog(this, "确认退出班级？", "确认", "取消");
                    CommentDialog.setmDialogListener(new DialogListener() {
                        @Override
                        public void yesClickListener() {
                        }

                        @Override
                        public void noClickListener() {
                            httpDeleteStudent(MyApplication.getUser().getUuid());
                        }
                    });
                } else if (MyApplication.getUser().getPart().equals("T")) {
                    if (!TextUtils.isEmpty(getStudentId())) {
                        httpDeleteStudent(getStudentId());
                    } else {
                        showToast("请选择学生！");
                    }

                } else {
                    //家长
                }
                break;
            case R.id.contact_t:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(ClassDetailActivity.this, mClasses.getTeacherid(), mClasses.getTeachername());
                }
                break;
            case R.id.homework_layout:
                Bundle b = new Bundle();
                b.putSerializable("classid", mClasses.getUuid());
//                b.putSerializable("dataList", (Serializable) mGroupList);
                go(HomeWorkListActivity.class, b);
                break;
            case R.id.fenxi_layout:
                showToast("正在努力的开发中...");
                break;
        }
    }

    /**
     * 设置 编辑按钮的状态 是删除还是编辑
     */
    private void setEditView() {
//        if (isShowEdit) {
//            if (!TextUtils.isEmpty(getStudentId())) {
//                httpDeleteStudent(getStudentId());
//                return;
//            }
//
//        }
        isShowEdit = !isShowEdit;
        if (isShowEdit) {
            mRightText.setText("取消");
            mLayoutBottom.setVisibility(View.VISIBLE);

            mdeleteOroutTV.setText("删除学生");
            for (Student s : mList) {
                s.setCheck(false);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            mLayoutBottom.setVisibility(View.GONE);
            mRightText.setText("编辑");
            mAdapter.notifyDataSetChanged();
        }

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
            return "";
        } else {
            idStr = idStr.substring(0, idStr.length() - 1);
        }
        return idStr;
    }

    /**
     * @param id 退出还是删除  0 申请退出  1删除学生
     */
    private void httpDeleteStudent(final String id) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.QUIT_CLASS_STUDENT, RequestMethod.POST);
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
                            if (id.equals(MyApplication.getUser().getUuid())) {
//                                EventBus.getDefault().post(new EventFlag(Constant.EvStudentOutClass, ""));
//                                finish();
                                showToast("申请已经提交，请耐心等待老师审核！");
                            } else {
//                                removeItem();
                            }

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

    private void removeItem() {
        int num = 0;
        List<Student> l = new ArrayList<>();
        l.addAll(mList);
        for (int i = l.size() - 1; i >= 0; i--) {
            if (l.get(i).isCheck()) {
                mList.remove(i);
                num--;
            }
        }
        EventBus.getDefault().post(new EventFlag(Constant.EvUpadeClassS, num, position));
    }


    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView icon = holder.getView(R.id.icon);
            TextView name = holder.getView(R.id.name);
            TextView j_name = holder.getView(R.id.j_name); //家长
            TextView content = holder.getView(R.id.content);
            CheckBox checkBox = holder.getView(R.id.checkbox);

            if (isShowEdit) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
            checkBox.setFocusable(false);
            checkBox.setClickable(false);
            if (mList.get(position).isCheck()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            if (!MyApplication.getUser().getPart().equals("S")) {
                if (mList.get(position).getParent() != null) {
                    j_name.setVisibility(View.VISIBLE);
                    j_name.setText("家长：" + mList.get(position).getParent().getRealname());
                }
            }
            try {
                name.setText("姓名：" + mList.get(position).getRealname());
                content.setText("学号：" + mList.get(position).getXjh());

            } catch (Exception e) {
                e.printStackTrace();
            }

            int p = position % 10;
            switch (p) {
                case 1:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header1);
                    break;
                case 2:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header2);
                    break;
                case 3:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header3);
                    break;
                case 4:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header4);
                    break;
                case 5:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header5);
                    break;
                case 6:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header6);
                    break;
                case 7:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header7);
                    break;
                case 8:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header8);
                    break;
                case 9:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header9);
                    break;
                case 0:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_header10);
                    break;
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll();
    }
}
