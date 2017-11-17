package com.ss.education.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.ss.education.R;


/**
 * Created by Administrator on 2016/11/14.
 */
public class NetworkImageHolderView implements Holder {
    private ImageView mImageView;


    @Override
    public View createView(Context context) {
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return mImageView;
    }

    @Override
    public void UpdateUI(Context context, int position, Object data) {
        Glide.with(context).load(data).placeholder(R.drawable.pic_fail).error(R.drawable.pic_fail).into(mImageView);
    }
}
