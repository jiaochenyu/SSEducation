package com.ss.education.ui.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPassActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.password_second)
    EditText mPasswordSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitle.setText(R.string.title_forgetPassword);
    }

    @OnClick({R.id.back, R.id.subimt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.subimt:
                go(LoginActivity.class);
                break;
        }
    }
}
