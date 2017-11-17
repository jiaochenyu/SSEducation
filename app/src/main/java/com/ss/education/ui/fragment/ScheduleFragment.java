package com.ss.education.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.education.R;
import com.ss.education.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class ScheduleFragment extends BaseFragment {
    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_schedule, null);
        ButterKnife.bind(this, mView);
        
        return mView;
    }
}
