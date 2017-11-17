package com.ss.education.weight;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by CZF on 2016/7/11.
 * 用于空间附近的PopupWindow的显示，
 */
public class MorePopupWindow {
    Context context;
    private int[] location = new int[2];
    public int popupHeight;//popup高度
    public int popupWidth;//popup宽度
    int layoutResId;//布局
    public PopupWindow pw;//自定义的popup
    private LinearLayout view;
    private View mView;
    private boolean isShow;
    private LinearLayout bgLinearLayout;

    /**
     * 构造方法：输入显示的布局样式资源文件ID，布局的宽高（也可以是LayoutParams.MATCH_Parent）
     *
     * @param context
     * @param layoutResId
     * @param width
     * @param height
     */
    public MorePopupWindow(Context context, int layoutResId, int width, int height) {
        this.context = context;
        this.layoutResId = layoutResId;
        initPopupwindow(layoutResId, width, height);
    }


    /**
     * 初始化自定义PopupWindow
     */
    private void initPopupwindow(int layoutResId, int width, int height) {

        view = (LinearLayout) LayoutInflater.from(context).inflate(layoutResId, null);
        pw = new PopupWindow(view, width, height, true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);//重新测量
//            bgLinearLayout = (LinearLayout) view.findViewById(R.id.popup_bg);//背景图层
        popupWidth = view.getMeasuredWidth();//得到popup布局的宽度
        popupHeight = view.getMeasuredHeight();
        pw.setOutsideTouchable(true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setFocusable(true);
        pw.setTouchable(true);

    }

    /**
     * 显示popupwindow在指定控件上方,根据x,y坐标可以微调位置
     *
     * @param view 相对的控件
     * @param xoff
     * @param yoff
     */
    public void showPopupUp(View view, int xoff, int yoff) {
        mView = view;
        view.getLocationOnScreen(location);
        pw.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - xoff, location[1] - popupHeight - yoff);//显示具体的屏幕位置
    }

    public View getmView() {
        return mView;
    }

    /**
     * 显示popupwindow在指定控件下方,根据x,y坐标可以微调位置
     *
     * @param view 相对的控件
     * @param xoff
     * @param yoff
     */
    public void showPopupDown(View view, int xoff, int yoff) {
        mView = view;
        view.getLocationOnScreen(location);
        pw.showAsDropDown(view, xoff, yoff);//显示具体的屏幕位置

    }


    /**
     * 显示popupwindow在父控件的顶部,根据x,y坐标可以微调位置
     *
     * @param parent 父控件
     * @param xoff
     * @param yoff
     */
    public void showTop(View parent, int xoff, int yoff) {

        pw.showAtLocation(null, Gravity.TOP, xoff, yoff);

    }


    /**
     * 显示popupwindow在父控件的底部,根据x,y坐标可以微调位置
     *
     * @param parent 父控件
     * @param xoff
     * @param yoff
     */
    public void showBottom(View parent, int xoff, int yoff) {

        pw.showAtLocation(parent, Gravity.BOTTOM, xoff, yoff);

    }

    /**
     * 隐藏PopupWindow
     */
    public void dismissPopup() {

        pw.dismiss();

    }

    /**
     * 得到popup的布局 2016.7.13
     *
     * @return
     */
    public View getLayoutView() {

        return view;
    }


    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

//    public void setDownBg(){
//        bgLinearLayout.setBackgroundDrawable(null);
//        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.huaweizhuanjia_gengduobg_pop_down));
//    }
//
//    public void setUpBg(){
//        bgLinearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.huaweizhuanjia_gengduobg));
//        view.setBackgroundDrawable(null);
//    }
}
