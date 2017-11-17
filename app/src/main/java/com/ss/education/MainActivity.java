package com.ss.education;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shcyd.lib.base.BaseAppManager;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.Apk;
import com.ss.education.listener.DialogListener;
import com.ss.education.ui.adapter.ConversationListAdapterEx;
import com.ss.education.ui.fragment.AnalysisFragment;
import com.ss.education.ui.fragment.ClassFragment;
import com.ss.education.ui.fragment.HomeFragment;
import com.ss.education.ui.fragment.MyFragment;
import com.ss.education.ui.service.UpdateVersionService;
import com.ss.education.utils.BottomNavigationViewHelper;
import com.ss.education.utils.JPushUtils;
import com.ss.education.weight.CommentDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity implements IUnReadMessageObserver {

    private static final int HOME = 1;
    public static final int CLASS = 2;
    private static final int MESSAGE = 3;
    private static final int MY = 4;
    private static final int ANALYSIS = 5;//分析
    @Bind(R.id.fragment)
    FrameLayout mFragment;
    @Bind(R.id.navigation)
    BottomNavigationView mNavigation;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private ClassFragment classFragment;
    private ConversationListFragment messageFragment;
    private AnalysisFragment analysisFragment;
    private MyFragment myFragment;
    //    private ScheduleFragment scheduleFragment;
    private RequestQueue mQueue = NoHttp.newRequestQueue();
    private Apk mApk;
    private boolean isDebug;
    private Conversation.ConversationType[] mConversationsTypes = null;

    int page = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseAppManager.getInstance().clearBackActivities();
        ButterKnife.bind(this);
        initView();
        initData();
//        initRongyunUser();
        initJpush();
        initListener();
        updateApk();
        applyPower();

    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            page = extras.getInt("page", -1);
        }
    }

    //申请权限
    private void applyPower() {
        AndPermission.with(this)
                .permission(
                        Permission.STORAGE,
                        Permission.CAMERA
                ).start();
    }


    private void initView() {
        BottomNavigationViewHelper.disableShiftMode(mNavigation);

    }

    private void initData() {
        isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
        fragmentManager = getSupportFragmentManager();

        if (page != -1) {
            showFragment(CLASS);
        } else {
            showFragment(HOME);
        }


    }


    private void initListener() {
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home://主页
                        showFragment(1);
                        return true;
//                    case R.id.navigation_shishuo://师说
//                        showFragment(2);
//                        return true;
                    case R.id.navigation_class://班级
                        showFragment(2);
                        return true;
                    case R.id.navigation_message://消息
                        showFragment(3);
                        return true;
                    case R.id.navigation_me://我的
                        showFragment(4);
                        return true;
                    case R.id.navigation_analysis:
                        showFragment(5);
                        return true;
                }
                return false;
            }
        });
    }

    private void initJpush() {
        Set<String> tags = new HashSet<>();
        tags.add(MyApplication.getUser().getSex());
        JPushUtils.jPushMethod(this, MyApplication.getUser().getUuid(), tags);
    }


    private Fragment initConversationList() {
        if (messageFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
//            TextView t = (TextView) listFragment.getView().findViewById(R.id.title);
//            t.setText("修改消息");
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
                    .build();
            listFragment.setUri(uri);
            messageFragment = listFragment;
            return listFragment;
        } else {
            return messageFragment;
        }
    }

    private void showFragment(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //隐藏所有的Fragment
        hideFragment(fragmentTransaction);
        //显示指定的Fragment

        switch (index) {
            //首页
            case HOME:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.fragment, homeFragment);
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;
//            //师说
//            case SAID:
//                if (saidFragment == null) {
//                    saidFragment = new MessageFragment();
//                    fragmentTransaction.add(R.id.fragment, saidFragment);
//                } else {
//                    fragmentTransaction.show(saidFragment);
//                }
//                break;
            //班级
            case CLASS:
                if (classFragment == null) {
                    classFragment = new ClassFragment();
                    fragmentTransaction.add(R.id.fragment, classFragment);
                } else {
                    fragmentTransaction.show(classFragment);
                }
                break;
            //消息
            case MESSAGE:
                if (messageFragment == null) {
//                    messageFragment = new MessageFragment();
                    messageFragment = (ConversationListFragment) initConversationList();
                    fragmentTransaction.add(R.id.fragment, messageFragment);
                } else {
                    fragmentTransaction.show(messageFragment);
                }
                break;
            //我的
            case MY:
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    fragmentTransaction.add(R.id.fragment, myFragment);
                } else {
                    fragmentTransaction.show(myFragment);
                }
                break;
            case ANALYSIS:
                if (analysisFragment == null) {
                    analysisFragment = new AnalysisFragment();
                    fragmentTransaction.add(R.id.fragment, analysisFragment);
                } else {
                    fragmentTransaction.show(analysisFragment);
                }
                break;

        }

        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }
        if (classFragment != null) {
            fragmentTransaction.hide(classFragment);
        }
        if (messageFragment != null) {
            fragmentTransaction.hide(messageFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }
        if (analysisFragment != null) {
            fragmentTransaction.hide(analysisFragment);
        }
    }

    private void updateApk() {
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPDATE_APK, RequestMethod.POST);
        JSONObject jsonObject = new JSONObject();
        request.add("useruuid", MyApplication.getUser().getUuid());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {

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
                            mApk = gson.fromJson(json.toString(), Apk.class);
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
                isUpdateApk();
            }
        });
    }

    private void isUpdateApk() {
        if (mApk != null) {
            if (getVersion() < mApk.getVersioncode()) {
                CommentDialog.showComfirmDialog(this, "是否更新到最新版本？", "取消", "确认");
                CommentDialog.setmDialogListener(new DialogListener() {
                    @Override
                    public void yesClickListener() {
                        Intent i = new Intent(MainActivity.this, UpdateVersionService.class);
                        i.setAction("updateVersionService");
                        i.putExtra("apk", mApk);
                        startService(i);
                    }

                    @Override
                    public void noClickListener() {

                    }
                });

            }
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll();
    }

    @Override
    public void onCountChanged(int i) {
        //融云消息数量
    }

}
