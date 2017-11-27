package com.ss.education.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.ss.education.entity.Classes;
import com.ss.education.entity.EventFlag;
import com.ss.education.ui.activity.classes.AddClassActivity;
import com.ss.education.ui.activity.classes.ClassDetailActivity;
import com.ss.education.ui.activity.classes.JoinClassActivity;
import com.ss.education.ui.activity.classes.TeacherClassDetailActivity;
import com.ss.education.utils.ImageUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class ClassFragment extends BaseFragment {
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mRightText;
    @BindView(R.id.add_class)
    TextView mAddClassTV;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.null_bg)
    RelativeLayout mNullBg;
    private View mView;

    private List<Classes> mList;
    private MyAdapter mAdapter;
    private RequestQueue mQueue = NoHttp.newRequestQueue();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_class, null);
        ButterKnife.bind(this, mView);
        initView();
        initData();
        initListener();
        return mView;
    }


    private void initView() {
        mTitle.setText("我的班级");
        mBack.setVisibility(View.GONE);
        mRightText.setVisibility(View.VISIBLE);
        mSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
        if (MyApplication.getUser().getPart().equals("S")) {
            mRightText.setText("加入");
            mAddClassTV.setText("+加入班级");
        } else if (MyApplication.getUser().getPart().equals("T")) {
            mAddClassTV.setText("+创建班级");
            mRightText.setText("创建");
        } else {
            mAddClassTV.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new MyAdapter(getActivity(), R.layout.item_class, mList);
        mListview.setAdapter(mAdapter);
        httpGetData();
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                httpGetData();
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).getState() != 1 && MyApplication.getUser().getPart().equals("S")) {
                    showToast("审核通过才能查看");
                    return;
                } else if (MyApplication.getUser().getPart().equals("T")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("classbh", mList.get(position).getClassbh());
                    bundle.putString("classid", mList.get(position).getUuid());
                    bundle.putInt("position", position);
                    go(TeacherClassDetailActivity.class, bundle);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("classbh", mList.get(position).getClassbh());
                bundle.putString("classid", mList.get(position).getUuid());
                bundle.putInt("position", position);
                go(ClassDetailActivity.class, bundle);
            }
        });

    }

    private void httpGetData() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_CLASS_LIST, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
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
                                Classes c = gson.fromJson(array.getJSONObject(i).toString(), Classes.class);
                                mList.add(c);
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
                Log.e("array", mList.size() + "");
                setView();
            }
        });
    }

    public void setView() {
        mSwipeRefresh.setRefreshing(false);
        if (adjustList(mList)) {
            mListview.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
        } else {
            mNullBg.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvAddClass)) {
            mList.clear();
            httpGetData();
        }
//        if (eventFlag.getFlag().equals(Constant.EvUpadeClassS)) {
//            int p = eventFlag.getPosition();
//            int num = (int) eventFlag.getObject();
//            Log.e("num", num + "");
//            mList.get(p).setClassrs(mList.get(p).getClassrs() + num);
//            mAdapter.notifyDataSetChanged();
//        }
        if (eventFlag.getFlag().equals(Constant.EvUpdateClassDetail) ||
                eventFlag.getFlag().equals(Constant.EvStudentOutClass)
                || eventFlag.getFlag().equals(Constant.EvJoinClass)
        || eventFlag.getFlag().equals(Constant.EvUpadeClassS)) {
            mList.clear();
            httpGetData();
        }
    }


    @OnClick({R.id.right_text, R.id.add_class})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.right_text:
                if (MyApplication.getUser().getPart().equals("S")) {
                    go(JoinClassActivity.class);
                } else if (MyApplication.getUser().getPart().equals("T")) {
                    go(AddClassActivity.class);
                }

                break;
            case R.id.add_class:
                if (MyApplication.getUser().getPart().equals("S")) {
                    go(JoinClassActivity.class);
                } else if (MyApplication.getUser().getPart().equals("T")) {
                    go(AddClassActivity.class);
                }
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
            TextView num = holder.getView(R.id.num);
            try {
                time.setText("" + mList.get(position).getCreatetime().split(" ")[0]);
                name.setText("名称：" + mList.get(position).getClassname());
                content.setText("班级号：" + mList.get(position).getClassbh());
                if (MyApplication.getUser().getPart().equals("S")) {
                    //审核状态判断
                    if (mList.get(position).getState() == 0) {
                        shuoming.setTextColor(getResources().getColor(R.color.redFE46));
                        shuoming.setText("入班申请中，请等待老师审核通过");
                    } else if (mList.get(position).getState() == 2) {
                        shuoming.setTextColor(getResources().getColor(R.color.redFE46));
                        shuoming.setText("入班申请被拒绝，请联系老师");
                    } else {
                        shuoming.setText("老师：" + mList.get(position).getTeachername());
                        shuoming.setTextColor(getResources().getColor(R.color.colorTopic));
                    }
                } else if (MyApplication.getUser().getPart().equals("T")) {
                    shuoming.setText("备注：" + mList.get(position).getRemarks());
                } else {
                    //家长
                }
                num.setText("人数：" + mList.get(position).getClassrs());

            } catch (Exception e) {
                e.printStackTrace();
            }
            ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getImgpath(), R.drawable.icon_class8);
            int p = position % 9;
//            switch (p) {
//                case 1:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class1));
//                    break;
//                case 2:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class2));
//                    break;
//                case 3:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class3));
//                    break;
//                case 4:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class4));
//                    break;
//                case 5:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class5));
//                    break;
//                case 6:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class6));
//                    break;
//                case 7:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class7));
//                    break;
//                case 8:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class8));
//                    break;
//                case 0:
//                    icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_class9));
//                    break;
//            }


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mQueue.cancelAll();
    }

}
