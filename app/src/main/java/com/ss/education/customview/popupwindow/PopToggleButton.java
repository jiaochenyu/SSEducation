package com.ss.education.customview.popupwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ss.education.R;


/**
 * �Զ���ToggleButton
 * @author mz
 *
 */
public class PopToggleButton extends RelativeLayout {

	private TextView tvTitle;
	private ImageView ivPressBottomLine;
	private ImageView ivToggle;
	private View rlCusToggle,verticalDivider,vBottomDivider;
	private boolean checked;
	private OnToggleListener onToggleListener;
	private Context context;

	public PopToggleButton(Context context) {
		super(context);
		this.context = context;
	}
	public PopToggleButton(Context context, int ivRes, int textRes) {
		super(context);
		this.context = context;
	}

	public  void setIVRes(){
		ivToggle.setImageDrawable(getResources().getDrawable(R.drawable.icon_down_arrow_white));
	}

	public void setTextRes() {
		tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));

	}

	public PopToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.custom_toggle_button, this);
		tvTitle = (TextView) findViewById(R.id.title);
		verticalDivider = findViewById(R.id.verticalDivider);
		rlCusToggle = findViewById(R.id.rlCusToggle);
		vBottomDivider = findViewById(R.id.v_tab_bottom_divider);
		ivToggle = (ImageView) findViewById(R.id.image_toggle);
		rlCusToggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				if (checked) {
					checked = false;
					setChecked(checked);
				} else {
					checked = true;
					setChecked(checked);
				}
				onToggleListener.onClick(PopToggleButton.this);
			}
		});
	}

	public void setText(String lable) {
		if (tvTitle != null) {
			this.tvTitle.setText(lable);
		}
	}

	public String getText() {
		if(tvTitle!=null){
			return tvTitle.getText().toString();
		}
		return null;
	}

	public void setChecked(boolean isChecked) {
		this.checked = isChecked;
		if (tvTitle == null || ivPressBottomLine == null) {
			return;
		}
//		if (checked) {
//			tvTitle.setTextColor(Color.rgb(255, 114, 1));
//			Drawable drawable = context.getResources().getDrawable(
//					R.drawable.icon_up);
//			tvTitle.setCompoundDrawablePadding(3);
//			tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
//					drawable, null);
//			ivPressBottomLine.setVisibility(View.VISIBLE);
//			vBottomDivider.setVisibility(View.GONE);
//		} else {
//			tvTitle.setTextColor(Color.rgb(72, 77, 81));
//			Drawable drawable = context.getResources().getDrawable(
//					R.drawable.icon_down);
//			tvTitle.setCompoundDrawablePadding(3);
//			tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
//					drawable, null);
//			ivPressBottomLine.setVisibility(View.GONE);
//			vBottomDivider.setVisibility(View.VISIBLE);
//		}
	}

	public boolean isChecked() {
		return checked;
	}

	public void hideVerticalDivider(){
		if(verticalDivider!=null){
			verticalDivider.setVisibility(View.GONE);
		}
	}
	
	public void showVerticalDivider(){
		if(verticalDivider!=null){
			verticalDivider.setVisibility(View.VISIBLE);
		}
	}
	
	public void setOnToggleListener(OnToggleListener onToggleListener) {
		this.onToggleListener = onToggleListener;
	}

	interface OnToggleListener {
		void onClick(View v);
	}
}
