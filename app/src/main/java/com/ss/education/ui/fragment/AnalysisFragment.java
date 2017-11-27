package com.ss.education.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.ss.education.R;
import com.ss.education.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class AnalysisFragment extends BaseFragment {
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    AgentWeb mAgentWeb;
    @BindView(R.id.parentLL)
    LinearLayout mParentLL;


    private View mView;


    String Url = "http://101.89.139.185:8090/spotfire/wp/render/2cK5czBQvxhM1hhdXS/analysis?file=/%E5%AD%A6%E7%94%9F%E6%88%90%E7%BB%A9_Mobile&waid=Xb-K5jK-DEadI33J8jUPK-2714277d43gfFK&wavid=0";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_analysis, null);
        ButterKnife.bind(this, mView);
        initView();
        initAgentWeb();
        initListener();

        return mView;
    }

    private void initAgentWeb() {

        mAgentWeb = AgentWeb.with(getActivity())//传入Activity or Fragment
                .setAgentWebParent(mParentLL, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(new ChromeClientCallbackManager.ReceivedTitleCallback() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {

                    }
                }) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go(Url);

    }


    private void initView() {
        mTitle.setText("分析");
        mBack.setVisibility(View.GONE);
//        mSwipeRefresh.setColorSchemeColors(R.color.colorTopic);

//        WebSettings webSettings = webView.getSettings();
//
////如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        webSettings.setJavaScriptEnabled(true);
//
////支持插件
////        webSettings.setEnableSmoothTransition(true);
//
////设置自适应屏幕，两者合用
////        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
////        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//
////缩放操作
////        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
////        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
////        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//
////其他细节操作
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
//        webSettings.setAllowFileAccess(true); //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
////        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                webView.loadUrl(Url);
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view,
//                                           SslErrorHandler handler, SslError error) {
//                handler.proceed();  //接受所有证书
//            }
//        });
//
////        webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    mProgressBar.setVisibility(View.INVISIBLE);
//                } else {
//                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
//                        mProgressBar.setVisibility(View.VISIBLE);
//                    }
//                    mProgressBar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//
//        webView.loadUrl(Url);
    }

    private void initListener() {
//        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
////                webView.loadUrl(Url);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
