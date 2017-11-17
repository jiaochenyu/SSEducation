package com.ss.education.customview.popupwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ss.education.R;
import com.ss.education.listener.PopViewBaseActionListener;

import java.util.List;


public class PopViewList extends LinearLayout implements PopViewBaseActionListener {

	private Context mContext;
	private ListView mListView;
	private ImageView ivUpOne,ivUpTwo,ivUpThree,ivUpFour;
	private PopListTextAdapter adapter;
	private List<String> items ;//= new String[] {"不限","出售","出租","求购","求租"};//显示字段
	private List<String> itemsVaule ;//= new String[] { "1", "2", "3", "4", "5"};//隐藏id
	private String showText;
	private OnSelectListener mOnSelectListener;
	private int showWhichUp=1;
	
	public PopViewList(Context context, List<String> items, List<String> itemsVaule, int showWhichUp) {
		super(context);
		this.items = items;
		this.itemsVaule = itemsVaule;
		this.showWhichUp = showWhichUp;
		init(context);
	}
	
	public PopViewList(Context context, AttributeSet attrs,
                       List<String> items, List<String> itemsVaule, int showWhichUp){
		super(context, attrs);
		this.items = items;
		this.itemsVaule = itemsVaule;
		this.showWhichUp = showWhichUp;
		init(context);
	}
	
	public void init(Context context){
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_list_popuwindow, this, true);
		mListView = (ListView) findViewById(R.id.list);

		
//		ivUpOne.setImageResource(R.color.transparent);
//		ivUpTwo.setImageResource(R.color.transparent);
//		ivUpThree.setImageResource(R.color.transparent);
//		ivUpFour.setImageResource(R.color.transparent);
		if(showWhichUp==1){
//			ivUpOne.setImageResource(R.drawable.icon_popuwindow_up);
		}else if(showWhichUp==2){
//			ivUpTwo.setImageResource(R.drawable.icon_popuwindow_up);
		}else if(showWhichUp==3){
//			ivUpThree.setImageResource(R.drawable.icon_popuwindow_up);
		}else if(showWhichUp==4){
//			ivUpFour.setImageResource(R.drawable.icon_popuwindow_up);
		}
		if(items!=null&&itemsVaule!=null){
			adapter = new PopListTextAdapter(mContext, items, R.color.colorWhite, R.color.colorWhite,0);
			adapter.setTextSize(14);
			adapter.setTextColor(mContext.getResources()
					.getColor(R.color.blackText));
			mListView.setAdapter(adapter);
			adapter.setOnItemClickListener(new PopListTextAdapter.OnItemClickListener() {
				
				@Override
				public void onItemClick(View view, int position) {
					if (mOnSelectListener != null) {
						showText = items.get(position);
						mOnSelectListener.getValue(itemsVaule.get(position), items.get(position));
					}				
				}
			});
		}		
	}
	
	/**
	 * 默认选择项显示
	 * @param showText
	 */
	public void setDefaultShowText(String showText){
		this.showText = showText;
	}
	
	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String high, String low);
	}

	@Override
	public void hide() {

	}

	@Override
	public void show() {

	}

}
