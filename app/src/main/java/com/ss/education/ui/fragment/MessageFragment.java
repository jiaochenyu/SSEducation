package com.ss.education.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.education.R;

import butterknife.ButterKnife;
import io.rong.imkit.fragment.ConversationListFragment;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class MessageFragment extends ConversationListFragment {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, mView);

        return mView;
    }
}
