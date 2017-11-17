package com.ss.education.weight;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ss.education.R;
import com.ss.education.listener.DialogListener;

import static com.yanzhenjie.nohttp.tools.ResCompat.getDrawable;

/**
 * Created by JCY on 2017/9/21.
 * 说明：
 */

public class CommentDialog {
    public static String editStr = "";

    public static DialogListener getmDialogListener() {
        return mDialogListener;
    }


    public static void setmDialogListener(DialogListener mDialogListener) {
        CommentDialog.mDialogListener = mDialogListener;
    }

    static DialogListener mDialogListener;

    public static void showDialog(final Context c) {
        final Dialog dialog = new Dialog(c, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_prompt_common);
        ImageView iv = (ImageView) dialog.findViewById(R.id.image);
        iv.setImageDrawable(getDrawable(R.drawable.jieshu));
        TextView messageTV = (TextView) dialog.findViewById(R.id.dialog_message);
        messageTV.setText(R.string.dialog_finish_exam);

        TextView noTV = (TextView) dialog.findViewById(R.id.notextview);
        TextView yesTV = (TextView) dialog.findViewById(R.id.yestextview);
        yesTV.setText(R.string.yes);
        noTV.setText(R.string.continue_exam);
        noTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.noClickListener();
            }
        });
        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.yesClickListener();
            }
        });
        dialog.show();
    }


    /**
     * 通用二次确认框
     *
     * @param c
     */
    public static void showComfirmDialog(final Context c, String message, String left, String right) {
        final Dialog dialog = new Dialog(c, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_prompt_common);
        ImageView iv = (ImageView) dialog.findViewById(R.id.image);
        iv.setVisibility(View.GONE);
        iv.setImageDrawable(getDrawable(R.drawable.jieshu));
        TextView messageTV = (TextView) dialog.findViewById(R.id.dialog_message);
        messageTV.setText(message);
        TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
        title.setVisibility(View.VISIBLE);
        TextView leftTV = (TextView) dialog.findViewById(R.id.notextview);
        TextView rightTV = (TextView) dialog.findViewById(R.id.yestextview);
        leftTV.setText(left);
        rightTV.setText(right);
        leftTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.noClickListener();
            }
        });
        rightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.yesClickListener();
            }
        });
        dialog.show();
    }


    public static void showEditDialog(final Context c, String titleStr, String message, final String mEditStr, String left, String right) {

        final Dialog dialog = new Dialog(c, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_prompt_edit);
        TextView messageTV = (TextView) dialog.findViewById(R.id.dialog_message);
        TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView leftTV = (TextView) dialog.findViewById(R.id.notextview);
        TextView rightTV = (TextView) dialog.findViewById(R.id.yestextview);
        final EditText editText = (EditText) dialog.findViewById(R.id.edit);
        editText.setText(mEditStr);
        title.setText(titleStr);
        messageTV.setText(message);
        leftTV.setText(left);
        rightTV.setText(right);
        leftTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialogListener.noClickListener();
            }
        });
        rightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStr = editText.getText().toString();
                if (TextUtils.isEmpty(editStr)){
                    Toast.makeText(c, "请输入内容！", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    dialog.dismiss();
                    mDialogListener.yesClickListener();
                }

            }
        });
        dialog.show();
    }

    public String getEditStr() {
        return editStr;
    }

    public void setEditStr(String editStr) {
        this.editStr = editStr;
    }
}
