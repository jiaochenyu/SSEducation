package com.ss.education.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shcyd.lib.widget.popup.LoadingPopup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.shcyd.lib.base.BaseAppCompatActivity.NON_CODE;

/**
 * Created by jcy on 2016/12/19.
 */
public  class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    protected LoadingPopup loadingPop;
    private int currentPercent;
    private boolean canSideOut;
    private Toast toast = null;

    private Handler myHandler = new Handler();

    private Runnable mLoadingRunnable = new Runnable() {

        @Override
        public void run() {
            if (loadingPop == null) {
                loadingPop = new LoadingPopup(getActivity());
            }
            if (canSideOut) {
                loadingPop.pickPhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                loadingPop.pickPhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingPop.dismiss();
                    }
                });
            }
            loadingPop.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    };
    private Runnable mHideLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingPop != null) {
                loadingPop.dismiss();
            }
        }
    };

    private Runnable mShowPercentRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingPop != null) {
                loadingPop.percentLyt.setVisibility(View.VISIBLE);
                loadingPop.loadLyt.setVisibility(View.GONE);
                loadingPop.percentBar.setProgress(currentPercent);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
    }

    protected boolean isBindEventBusHere() {
        return false;
    }



    public synchronized void showLoading() {
        Log.e(TAG, ">>>>>>  showLoading");
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });
    }
    public void showLoading(boolean canSideOut) {
        this.canSideOut = canSideOut;
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });
    }

    public synchronized void hideLoading() {
        // Log.e(TAG, ">>>>>>  hideLoading");
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mHideLoadingRunnable);
            }
        });
    }

    public synchronized void showPercentLoading(int percent) {
        currentPercent = percent;
        // Log.e(TAG, ">>>>>>  hideLoading");
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mShowPercentRunnable);
            }
        });
    }


    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    public String getStrgRes(int res){
        return getResources().getString(res);
    }
    // 判断list是否不为空且有值
    public boolean adjustList(List<?> list) {
        if (list != null && list.size() > 0 && (!list.isEmpty())) {
            return true;
        } else {
            return false;
        }
    }
    //PullToRefreshListView 自定义头部尾部加载信息
    public void initRefreshListView(PullToRefreshListView mListview) {
        ILoadingLayout startLables = mListview.getLoadingLayoutProxy(true,false);
        startLables.setPullLabel("下拉刷新");
        startLables.setRefreshingLabel("玩命刷新中...");
        startLables.setReleaseLabel("放开刷新");
        ILoadingLayout endLables = mListview.getLoadingLayoutProxy(false,true);
        endLables.setPullLabel("上拉加载");
        endLables.setRefreshingLabel("玩命加载中...");
        endLables.setReleaseLabel("放开加载");
    }



    protected void go(Class<? extends Activity> clazz) {
        _goActivity(clazz, null, NON_CODE, false);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz  target Activity
     * @param bundle
     */
    protected void go(Class<? extends Activity> clazz, Bundle bundle) {
        _goActivity(clazz, bundle, NON_CODE, false);
    }

    protected void goForResult(Class<? extends Activity> clazz, int requestCode) {
        _goActivity(clazz, null, requestCode, false);
    }

    protected void goForResult(Class<? extends Activity> clazz, Bundle bundle, int requestCode) {
        _goActivity(clazz, bundle, requestCode, false);
    }

    /**
     * Activity 跳转
     *
     * @param clazz  目标activity
     * @param bundle 传递参数
     * @param finish 是否结束当前activity
     */
    private void _goActivity(Class<? extends Activity> clazz, Bundle bundle, int requestCode, boolean finish) {
        if (null == clazz) {
            throw new IllegalArgumentException("you must pass a target activity where to go.");
        }
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        if (requestCode > NON_CODE) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
        if (finish) {
            getActivity().finish();
        }
    }


    public void showToast(String msg) {
        if (null != msg) {
            if (toast == null) {
                toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    public void showToast(int msg) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), getString(msg), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(msg);
        }
        toast.show();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    public void onDestroy() {

        toast = null;
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }





}
