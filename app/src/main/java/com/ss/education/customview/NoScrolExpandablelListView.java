package com.ss.education.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 自定义ListView，<span style="font-family: Arial, Helvetica, sans-serif;">解决ScrollView嵌套冲突问题</span>
 * 
 * @author king
 * 
 */

public class NoScrolExpandablelListView extends ExpandableListView {
	public NoScrolExpandablelListView(Context context) {
		super(context);

	}

	public NoScrolExpandablelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** 
         * 设置不滚动 
         */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}