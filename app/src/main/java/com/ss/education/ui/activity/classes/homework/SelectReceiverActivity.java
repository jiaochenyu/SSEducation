package com.ss.education.ui.activity.classes.homework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.customview.NoScrolExpandablelListView;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.Student;
import com.ss.education.entity.StudentGroup;
import com.ss.education.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.R.id.j_name;

public class SelectReceiverActivity extends BaseActivity {


    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mFinishTV;
    @BindView(R.id.exapandablelistview)
    NoScrolExpandablelListView mExapandablelistview;
    MyExpandableListViewAdapter mAdapter;
    List<StudentGroup> mGroupList;
    int countNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_receiver);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initData() {
        mGroupList.remove(0);
        mAdapter = new MyExpandableListViewAdapter();
        mExapandablelistview.setAdapter(mAdapter);
    }


    private void initView() {
        mTitle.setText("选择学生");
        mFinishTV.setVisibility(View.VISIBLE);
        mFinishTV.setText("完成");
    }

    private void initListener() {
        mExapandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mGroupList = (List<StudentGroup>) extras.getSerializable("dataList");
            countNum = mGroupList.get(0).getStudents().size();
        }
    }

    @OnClick({R.id.back, R.id.right_text, R.id.select_all_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                getCheckedStu();
                break;
            case R.id.select_all_layout:
                EventBus.getDefault().post(new EventFlag(Constant.EvSelectAll_Receive));
                finish();
                break;
        }
    }

    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).getStudents().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).getStudents().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
         *
         * @return
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectReceiverActivity.this).inflate(R.layout.item_class_group, null);
                groupHolder = new GroupHolder();
                groupHolder.mNameTV = (TextView) convertView.findViewById(R.id.group_name);
                groupHolder.mNumTV = (TextView) convertView.findViewById(R.id.num_student);
                groupHolder.imgArrow = (ImageView) convertView.findViewById(R.id.img);
                groupHolder.mGroupCB = (CheckBox) convertView.findViewById(R.id.group_check_box);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.mGroupCB.setVisibility(View.VISIBLE);
            groupHolder.mGroupCB.setChecked(mGroupList.get(groupPosition).isChecked());
            if (!isExpanded) {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_group_right);
            } else {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_down_arrow);
            }
            try {
                groupHolder.mNameTV.setText(mGroupList.get(groupPosition).getGroupname());
                groupHolder.mNumTV.setText(mGroupList.get(groupPosition).getStudents().size() + "/" + countNum);

            } catch (Exception e) {
                e.printStackTrace();
            }


            groupHolder.mGroupCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = mGroupList.get(groupPosition).isChecked();
                    mGroupList.get(groupPosition).setChecked(!flag);
                    selectGroupChid(groupPosition, !flag);
                    notifyDataSetChanged();
                }
            });


            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ChildHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectReceiverActivity.this).inflate(R.layout.item_class_student, null);
                holder = new ChildHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.j_name = (TextView) convertView.findViewById(j_name); //家长
                holder.content = (TextView) convertView.findViewById(R.id.content);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            holder.checkBox.setVisibility(View.VISIBLE);
            try {
                holder.checkBox.setChecked(mGroupList.get(groupPosition).getStudents().get(childPosition).isCheck());
                if (mGroupList.get(groupPosition).getStudents().get(childPosition).getParent() != null) {
                    holder.j_name.setVisibility(View.VISIBLE);
                    holder.j_name.setText("家长：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getParent().getRealname());
                }
                holder.name.setText("姓名：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getRealname());
                holder.content.setText("学号：" + mGroupList.get(groupPosition).getStudents().get(childPosition).getXjh());
                ImageUtils.setCircleDefImage(
                        holder.icon,
                        ConnectUrl.PICURL + mGroupList.get(groupPosition).getStudents().get(childPosition).getImgpath(),
                        R.drawable.icon_header4
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = mGroupList.get(groupPosition).getStudents().get(childPosition).isCheck();
                    mGroupList.get(groupPosition).getStudents().get(childPosition).setCheck(!flag);
                    setGroupChangeState(groupPosition);
                }
            });


            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private class GroupHolder {
            public TextView mNameTV;
            public ImageView imgArrow;
            public TextView mNumTV;
            public CheckBox mGroupCB;
        }

        private class ChildHolder {
            ImageView icon;
            TextView name;
            TextView j_name; //家长
            TextView content;
            CheckBox checkBox;

        }
    }

    //设置父节点未选中 或选中
    private void setGroupChangeState(int groupP) {
        int flag = 0;
        int size = mGroupList.get(groupP).getStudents().size();
        for (int i = 0; i < size; i++) {
            if (mGroupList.get(groupP).getStudents().get(i).isCheck()) {
                flag++;
            }
        }
        if (flag == size) {
            mGroupList.get(groupP).setChecked(true);
        } else {
            mGroupList.get(groupP).setChecked(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    //全选或者全不选 该组下的全部数据
    public void selectGroupChid(int groupP, boolean flag) {
        for (int i = 0; i < mGroupList.get(groupP).getStudents().size(); i++) {
            mGroupList.get(groupP).getStudents().get(i).setCheck(flag);
        }
        mAdapter.notifyDataSetChanged();
    }


    public void getCheckedStu() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < mGroupList.size(); i++) {
            for (int j = 0; j < mGroupList.get(i).getStudents().size(); j++) {
                if (mGroupList.get(i).getStudents().get(j).isCheck()) {
                    list.add((mGroupList.get(i).getStudents().get(j)));
                }
            }
        }
        if (adjustList(list)) {
            EventBus.getDefault().post(new EventFlag(Constant.EvSelectReceive_homework, list));
            finish();
        } else {
            showToast("请选择学生！");
        }

    }


}
