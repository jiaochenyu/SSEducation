package com.ss.education.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.listener.DialogListener;

/**
 * Created by JCY on 2017/9/21.
 * 说明：
 */

public class DialogClassCode {
    public static DialogListener getmDialogListener() {
        return mDialogListener;
    }

    public static void setmDialogListener(DialogListener mDialogListener) {
        DialogClassCode.mDialogListener = mDialogListener;
    }

    static DialogListener mDialogListener;

    public static void showDialog(final Context c,String code) {
        final Dialog dialog = new Dialog(c, R.style.Dialog);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_class_code);
        TextView fuzhiTV = (TextView) dialog.findViewById(R.id.fuzhitextview);
        TextView messageTV = (TextView) dialog.findViewById(R.id.dialog_message);
        messageTV.setText(code);
        TextView yesTV = (TextView) dialog.findViewById(R.id.yestextview);
        yesTV.setText(R.string.yes);
        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.yesClickListener();
            }
        });
        fuzhiTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.noClickListener();
            }
        });
        dialog.show();

    }
}
