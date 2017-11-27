package com.ss.education.ui.fragment.classes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseFragment;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JCY on 2017/11/13.
 * 说明：
 */

public class ClassesShenheFragment extends BaseFragment {
    public static final String ARGS_LIST = "args_list";
    public static final String ARGS_PAGE = "args_page";
    public static final String ARGS_FLAG = "args_flag";
    public static final String ARGS_BH = "args_classbh";
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;


    private View mView;
    private List<Student> mList;
    private int flag = 0;  ////0 加入班级申请   3 退出班级申请
    private int page = 0;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private MyAdapter mAdapter;
    private String classbh = ""; //班级编号

    public static ClassesShenheFragment newInstance(int flag, int p, String bh) {
        Bundle args = new Bundle();
//        args.putSerializable(ARGS_LIST, (Serializable) list);
        args.putInt(ARGS_PAGE, p);
        args.putInt(ARGS_FLAG, flag);
        args.putString(ARGS_BH, bh);
        ClassesShenheFragment fragment = new ClassesShenheFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mList = (List<Student>) getArguments().getSerializable(ARGS_LIST);
        flag = getArguments().getInt(ARGS_FLAG);
        page = getArguments().getInt(ARGS_PAGE);
        classbh = getArguments().getString(ARGS_BH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_classes_shenhe, null);
        ButterKnife.bind(this, mView);
        initView();
        initData();
        initListener();
        return mView;
    }

    private void initView() {
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(getActivity(), R.layout.item_class_student, mList);
        mListview.setAdapter(mAdapter);
        httpGetListData();
    }


    //获取班级学生列表
    private void httpGetListData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_STUDENT_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("classbh", classbh);
            jsonObject.put("state", flag); //0 加入班级申请   3 退出班级申请
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
            mListview.setVisibility(View.VISIBLE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);

        }
        mAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new EventFlag(Constant.EvFinishShenhe, mList.size(), page));
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


    /**
     * 审核之后移除item
     * state  0 同意操作 1 拒绝操作
     */
    public void removeItem() {
        int num = 0;
        List<Student> l = new ArrayList<>();
        l.addAll(mList);
        for (int i = l.size() - 1; i >= 0; i--) {
            if (l.get(i).isCheck()) {
                mList.remove(i);

            }
        }
        EventBus.getDefault().post(new EventFlag(Constant.EvUpadeClassS));
        setView();
    }

    public String getStudentId() {
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
    public void onDestroyView() {
        super.onDestroyView();

    }
}
