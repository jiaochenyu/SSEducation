package com.ss.education.ui.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.ss.education.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SSChatActivity extends FragmentActivity {

    @Bind(R.id.title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sschat);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        mTitle.setText("正在和" + intent.getData().getQueryParameter("title") + "聊天");
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
