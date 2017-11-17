package com.ss.education.utils.voiceutils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ss.education.R;

public class DialogManager {

    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;

    private RelativeLayout imgBg;

    private TextView tipsTxt;
    private RelativeLayout imgBg2;

    private TextView tipsTxt2;
    private Context mContext;

    public DialogManager(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public void showRecordingDialog() {
        // TODO Auto-generated method stub

        mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_voice_manager, null);
        mDialog.setContentView(view);
        imgBg = (RelativeLayout) view.findViewById(R.id.dm_rl_bg);
        tipsTxt = (TextView) view.findViewById(R.id.dm_tv_txt);
        imgBg2 = (RelativeLayout) view.findViewById(R.id.dm_rl_bg2);
        tipsTxt2 = (TextView) view.findViewById(R.id.dm_tv_txt2);

        mDialog.show();

    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setVisibility(View.VISIBLE);
            tipsTxt.setVisibility(View.VISIBLE);
            imgBg2.setVisibility(View.GONE);
            tipsTxt2.setVisibility(View.GONE);

            imgBg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yuyin_voice_1));
            tipsTxt.setText(R.string.shouzhishanghua);
        }
    }

    /**
     * 取消界面
     */
    public void wantToCancel() {
        // TODO Auto-generated method stub
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setVisibility(View.GONE);
            tipsTxt.setVisibility(View.GONE);
            imgBg2.setVisibility(View.VISIBLE);
            tipsTxt2.setVisibility(View.VISIBLE);

            imgBg2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yuyin_cancel));
            tipsTxt2.setText(R.string.want_to_cancle);
        }

    }

    // 时间过短
    public void tooShort() {
        // TODO Auto-generated method stub
        if (mDialog != null && mDialog.isShowing()) {
            imgBg2.setVisibility(View.VISIBLE);
            tipsTxt2.setVisibility(View.VISIBLE);
            imgBg.setVisibility(View.GONE);
            tipsTxt.setVisibility(View.GONE);
            imgBg2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yuyin_gantanhao));
            tipsTxt2.setText(R.string.tooshort);
        }

    }

    // 隐藏dialog
    public void dimissDialog() {
        // TODO Auto-generated method stub

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    public void updateVoiceLevel(int level) {
        // TODO Auto-generated method stub
        if (level > 0 && level < 6) {

        } else {
            level = 5;
        }
        if (mDialog != null && mDialog.isShowing()) {

            //先不改变它的默认状态
//			mIcon.setVisibility(View.VISIBLE);
//			mVoice.setVisibility(View.VISIBLE);
//			mLable.setVisibility(View.VISIBLE);

            //通过level来找到图片的id，也可以用switch来寻址，但是代码可能会比较长
            int resId = mContext.getResources().getIdentifier("yuyin_voice_" + level,
                    "drawable", mContext.getPackageName());
            imgBg.setBackgroundResource(resId);
        }

    }

}
