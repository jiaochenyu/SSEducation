package com.ss.education.ui.activity.classes.homework;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewOfficeActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.webview)
    WebView urlWebView;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_office);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        path = extras.getString("path");
    }

    private void initView() {
        mTitle.setText("预览");
        urlWebView.setVisibility(View.VISIBLE);
//        mLvAttach.setVisibility(View.GONE);
        urlWebView.setWebViewClient(new AppWebViewClients());
        urlWebView.getSettings().setJavaScriptEnabled(true);
        urlWebView.getSettings().setUseWideViewPort(true);
        // https://view.officeapps.live.com/op/view.aspx?src
        urlWebView.loadUrl("http://view.officeapps.live.com/op/view.aspx?src=" + ConnectUrl.FILE_PATH + path);
        urlWebView.loadUrl("http://view.officeapps.live.com/op/view.aspx?src=" + ConnectUrl.FILE_PATH + path);
    }

    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
