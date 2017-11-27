package com.ss.education.ui.activity.classes;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ss.education.utils.ImageUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.R.id.position;

public class StudentManageActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refuse)
    TextView mDeleteTV;

    List<Student> mList;
    String classid = "";
    String classbh = "";

    private RequestQueue mQueue = NoHttp.newRequestQueue();
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_manage);
        ButterKnife.bind(this);
        initView();
        initData();
        ininListener();
    }


    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mList = (List<Student>) extras.getSerializable("data");
            classid = extras.getString("classid", "");
            classbh = extras.getString("classbh", "");
        }
    }

    private void initView() {
        mTitle.setText("学生管理");
    }

    private void initData() {
        mAdapter = new MyAdapter(this, R.layout.item_class_student, mList);
        mListview.setAdapter(mAdapter);
    }

    private void ininListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).isCheck()) {
                    mList.get(position).setCheck(false);
                } else {
                    mList.get(position).setCheck(true);
                }
                mAdapter.notifyDataSetChanged();

                if (TextUtils.isEmpty(getStudentId())) {
                    mDeleteTV.setBackgroundDrawable(getDrawable(R.color.grayBA));
                } else {
                    mDeleteTV.setBackgroundDrawable(getDrawable(R.color.colorTopic));
                }
            }
        });

    }

    @OnClick({R.id.back, R.id.refuse, R.id.agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refuse:
                String ids = getStudentId();
                if (!TextUtils.isEmpty(ids)) {
                    httpDeleteStudent(ids);
                } else {
                    showToast("请选择学生！");
                }
                break;
            case R.id.agree:
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void httpDeleteStudent(String ids) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.DELETE_CLASS_STUDENT, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuids", ids);
            jsonObject.put("classbh", classbh);
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
                            EventBus.getDefault().post(new EventFlag(Constant.EvStudentOutClass, ""));
                            removeItem();
                            finish();
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
        mAdapter.notifyDataSetChanged();
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
            checkBox.setVisibility(View.VISIBLE);
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
}
