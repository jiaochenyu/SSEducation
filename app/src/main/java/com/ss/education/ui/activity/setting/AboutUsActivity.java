package com.ss.education.ui.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mTitle.setText("关于我们");

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
