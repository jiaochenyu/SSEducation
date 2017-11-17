package com.ss.education.ui.activity.classes;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.dragListView.DragListAdapter;
import com.ss.education.customview.dragListView.DragListView;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.StudentGroup;
import com.ss.education.listener.DialogListener;
import com.ss.education.listener.DragListViewRemoveItemListener;
import com.ss.education.weight.CommentDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupManageActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.right_text)
    TextView mSaveTV;
    @Bind(R.id.drag_list_view)
    DragListView mDragListView;
    List<StudentGroup> mList;
    DragListAdapter mAdapter;
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    String classid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mList = (List<StudentGroup>) extras.getSerializable("groupList");
            classid = extras.getString("classid");
        }
    }

    private void initListener() {
        mAdapter.setRemoveItemListener(new DragListViewRemoveItemListener() {
            @Override
            public void removeItem(int position) {
                httpDeleteGroup(position);
            }
        });
        mDragListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    showUpdateGroup(position, mList.get(position).getGroupname());
                }

                return false;
            }
        });
    }


    private void initView() {
        mTitle.setText("分组管理");
        mSaveTV.setVisibility(View.VISIBLE);
        mSaveTV.setText("保存");
    }

    private void initData() {
        mAdapter = new DragListAdapter(GroupManageActivity.this, mList);
        mDragListView.setAdapter(mAdapter);
    }


    private void httpDeleteGroup(final int position) {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GROUP_DELETE, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupid", mList.get(position).getGroupid());
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
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateClassGroup, ""));
                            mAdapter.removeItem(position);
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
                hideLoading();
            }


        });
    }


    @OnClick({R.id.back, R.id.right_text, R.id.add_group_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                httpUpdateSort();
                break;
            case R.id.add_group_layout:
                showAddGroup();
                String s = "upload\\/96c61dc30b8a4e98b5e9aa4466264a2c.jpg";
                break;
        }
    }

    private JSONArray sortGroup() {
        JSONArray array = new JSONArray();
        try {
            if (mList.size() > 1) {
                for (int i = 1; i < mList.size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put("groupid", mList.get(i).getGroupid());
                    object.put("seq", i);
                    array.put(object);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    private void showAddGroup() {
        CommentDialog.showEditDialog(this, "添加分组", "请输入新的分组名称", "", "取消", "确认");
        CommentDialog.setmDialogListener(new DialogListener() {
            @Override
            public void yesClickListener() {
                httpUpdateGroup(0, CommentDialog.editStr, 1);
            }

            @Override
            public void noClickListener() {

            }
        });
    }

    private void showUpdateGroup(final int position, String groupName) {
        CommentDialog.showEditDialog(this, "编辑分组", "请输入新的分组名称", groupName, "取消", "确认");
        CommentDialog.setmDialogListener(new DialogListener() {
            @Override
            public void yesClickListener() {
                httpUpdateGroup(position, CommentDialog.editStr, 0);
            }

            @Override
            public void noClickListener() {

            }
        });
    }

    /**
     * @param position
     * @param option   1添加 0修改
     */
    private void httpUpdateGroup(final int position, final String groupName, final int option) {
        Request<JSONObject> request = null;
        JSONObject jsonObject = new JSONObject();
        try {
            if (option == 1) {
                request = NoHttp.createJsonObjectRequest(ConnectUrl.GROUP_ADD, RequestMethod.POST);
                jsonObject.put("classid", classid);
                jsonObject.put("groupname", groupName);
            } else {
                request = NoHttp.createJsonObjectRequest(ConnectUrl.GROUP_UPDATE, RequestMethod.POST);
                jsonObject.put("groupid", mList.get(position).getGroupid());
                jsonObject.put("groupname", groupName);
            }
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
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateClassGroup, ""));
                            if (option == 1) {
                                StudentGroup group = new StudentGroup();
                                group.setGroupid(object.getString("groupid"));
                                group.setGroupname(groupName);
                                mList.add(group);
                            } else {
                                StudentGroup group = new StudentGroup();
                                mList.get(position).setGroupname(groupName);
                            }
                            mAdapter.notifyDataSetChanged();
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
                hideLoading();
            }


        });
    }


    private void httpUpdateSort() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GROUP_SAVE_SORT, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("array", sortGroup());
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
                            showToast("保存成功");
                            EventBus.getDefault().post(new EventFlag(Constant.EvUpdateClassGroup, ""));
                            finish();
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
                hideLoading();
            }


        });
    }

}
