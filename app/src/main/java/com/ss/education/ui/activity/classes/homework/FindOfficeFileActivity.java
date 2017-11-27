package com.ss.education.ui.activity.classes.homework;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.Constant;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.FileSort;
import com.ss.education.entity.LocationFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindOfficeFileActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mFinishTV;
    @BindView(R.id.expandlistview)
    ExpandableListView mExpandlistview;

    int limitCount = 5;

    int type = 0;

    ProgressDialog mProgressDialog;
    private List<LocationFile> mFileList;
    MyExpandableListViewAdapter mAdapter;
    List<FileSort> mFileSortList = new ArrayList<>();
    List<LocationFile> mSelectFiles;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initFileSort();
                    mProgressDialog.dismiss();
//                    Log.e("进去了么","yes");
//                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_office_file);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            limitCount = limitCount - extras.getInt("alreadySelectCount", 0);
            type = extras.getInt("type");
        }
    }

    private void initView() {
        mTitle.setText("我的文件");
        mFinishTV.setVisibility(View.VISIBLE);
        mFinishTV.setText("完成");
        initDialog();
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("数据加载中");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();  //将进度条显示出来
    }

    private void initData() {
        mFileList = new ArrayList<>();
        mFileSortList = new ArrayList<>();
        mSelectFiles = new ArrayList<>();
        mAdapter = new MyExpandableListViewAdapter();
        mExpandlistview.setAdapter(mAdapter);
        new Thread() {
            @Override
            public void run() {
                super.run();
//                readOfficeFile(new String[]{".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf", "txt"});
                readOfficeFile(new String[]{".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf"});
                mHandler.sendEmptyMessage(1);
            }
        }.start();
//        EventBus.getDefault().post(Constant.EvFinishFindFile);
//        readOfficeFile(new String[]{".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf"});

    }


    private void initListener() {

    }

//    @Override
//    protected boolean isBindEventBusHere() {
//        return true;
//    }
//
//    @Subscribe
//    public void onEVent(String flag) {
//        if (flag.equals(Constant.EvFinishFindFile)) {
//            initFileSort();
//            mProgressDialog.dismiss();
//        }
//    }

    /**
     * 初始化文件分类
     * //文件分类 1.xlsx  2.pptx， 3.docx ,4pdf
     */
    private void initFileSort() {
        List<LocationFile> xlsxFiles = new ArrayList<>();
        List<LocationFile> pptFiles = new ArrayList<>();
        List<LocationFile> docFiles = new ArrayList<>();
        List<LocationFile> pdfFiles = new ArrayList<>();
        List<LocationFile> txtFiles = new ArrayList<>();
        for (int i = 0; i < mFileList.size(); i++) {
            LocationFile item = mFileList.get(i);
            Log.e("文件数据", item.getFlag() + "");
            if (item.getFlag() == 1) {
                xlsxFiles.add(item);
            } else if (item.getFlag() == 2) {
                pptFiles.add(item);
            } else if (item.getFlag() == 3) {
                docFiles.add(item);
            } else if (item.getFlag() == 4) {
                pdfFiles.add(item);
            } else if (item.getFlag() == 5) {
                txtFiles.add(item);
            }
        }

        Log.e("数据excel", xlsxFiles.size() + "");
        Log.e("数据word", docFiles.size() + "");
        Log.e("数据ppt", pptFiles.size() + "");
        Log.e("数据pdf", pdfFiles.size() + "");

        if (adjustList(docFiles)) {//1.xlsx  2.pptx， 3.docx ,4pdf
            mFileSortList.add(new FileSort("WORD", 3, docFiles));
        }
        if (adjustList(xlsxFiles)) {
            mFileSortList.add(new FileSort("EXCEL", 1, xlsxFiles));
        }
        if (adjustList(pptFiles)) {
            mFileSortList.add(new FileSort("PPT", 2, pptFiles));
        }
        if (adjustList(pdfFiles)) {
            mFileSortList.add(new FileSort("PDF", 4, pdfFiles));
        }
        if (adjustList(txtFiles)) {
            mFileSortList.add(new FileSort("TXT", 5, txtFiles));
        }


        Log.e("一级分类", mFileSortList.size() + "");
        mAdapter.notifyDataSetChanged();
    }

    private void readOfficeFile(String[] extension) {
        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE};
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToLast()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                if (new File(data).exists()) {
                    Log.e("文件data", data + "");
                    int nameFlag = data.lastIndexOf('.');
                    LocationFile file = new LocationFile();
                    file.setPath(data);
                    file.setName(new File(data).getName());
                    file.setExtension(data.substring(nameFlag));
                    if (data.substring(nameFlag).equalsIgnoreCase(".pptx") || data.substring(nameFlag).equalsIgnoreCase(".ppt")) {
                        file.setFlag(2);

                    }
                    if (data.substring(nameFlag).equalsIgnoreCase(".xlsx") || data.substring(nameFlag).equalsIgnoreCase(".xls")) {
                        file.setFlag(1);
                    }
                    if (data.substring(nameFlag).equalsIgnoreCase(".doc") || data.substring(nameFlag).equalsIgnoreCase(".docx")) {
                        file.setFlag(3);
                    }
                    if (data.substring(nameFlag).equalsIgnoreCase(".pdf")) {
                        file.setFlag(4);
                    }
                    if (data.substring(nameFlag).equalsIgnoreCase(".txt")) {
                        file.setFlag(5);
                    }

                    mFileList.add(file);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        for (LocationFile item : mFileList) {
            Log.e("遍历文件数据", item.getFlag() + "");

        }
        Log.e("文件列表集合", mFileList.size() + "");
    }

    @OnClick({R.id.back, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                saveSelect();
                break;
        }
    }

    //选择完成
    private void saveSelect() {
        for (int j = 0; j < mFileSortList.size(); j++) {
            for (int i = 0; i < mFileSortList.get(j).getLocationFiles().size(); i++) {
                if (mFileSortList.get(j).getLocationFiles().get(i).isChecked()) {
                    mSelectFiles.add(mFileSortList.get(j).getLocationFiles().get(i));
                }
            }
        }
        EventBus.getDefault().post(new EventFlag(Constant.EvFinishOfficeSelectFile, mSelectFiles,type));
        finish();
    }


    public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mFileSortList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mFileSortList.get(groupPosition).getLocationFiles().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mFileSortList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mFileSortList.get(groupPosition).getLocationFiles().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(FindOfficeFileActivity.this).inflate(R.layout.item_class_group, null);
                groupHolder = new GroupHolder();
                groupHolder.mNameTV = (TextView) convertView.findViewById(R.id.group_name);
                groupHolder.mNumTV = (TextView) convertView.findViewById(R.id.num_student);
                groupHolder.imgArrow = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            if (!isExpanded) {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_group_right);
            } else {
                groupHolder.imgArrow.setBackgroundResource(R.drawable.icon_down_arrow);
            }

            groupHolder.mNameTV.setText(mFileSortList.get(groupPosition).getName());
            try {

                groupHolder.mNumTV.setText("共" + mFileSortList.get(groupPosition).getLocationFiles().size() + "个文件");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(FindOfficeFileActivity.this).inflate(R.layout.item_find_file_info, null);
                holder = new ChildHolder();
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.file_checkbox);
                holder.mImageView = (ImageView) convertView.findViewById(R.id.office_image);
                holder.mTextView = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            if (mFileSortList.get(groupPosition).getFileFlag() == 1) {
                Glide.with(mContext).load(R.drawable.icon_office_excel).into(holder.mImageView);
            }
            if (mFileSortList.get(groupPosition).getFileFlag() == 2) {
                Glide.with(mContext).load(R.drawable.icon_office_ppt).into(holder.mImageView);
            }
            if (mFileSortList.get(groupPosition).getFileFlag() == 3) {
                Glide.with(mContext).load(R.drawable.icon_office_word).into(holder.mImageView);
            }
            if (mFileSortList.get(groupPosition).getFileFlag() == 4) {
                Glide.with(mContext).load(R.drawable.icon_office_pdf).into(holder.mImageView);
            }
            if (mFileSortList.get(groupPosition).getFileFlag() == 5) {
                Glide.with(mContext).load(R.drawable.icon_office_txt).into(holder.mImageView);
            }
            holder.mTextView.setText(mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).getName());
            holder.mCheckBox.setChecked(mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).isChecked());


//            holder.mCheckBox.setFocusable(false);
//            holder.mCheckBox.setClickable(false);
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).isChecked();
                    if (flag) {
                        mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).setChecked(false);
                    } else {
                        mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).setChecked(true);
                    }
                    int selectCount = 0;  //限制文件数量
                    for (int j = 0; j < mFileSortList.size(); j++) {
                        for (int i = 0; i < mFileSortList.get(j).getLocationFiles().size(); i++) {
                            if (mFileSortList.get(j).getLocationFiles().get(i).isChecked()) {
                                selectCount++;
                            }
                        }
                    }
                    if (selectCount > limitCount) {
                        showToast("最多选择" + limitCount + "个文件！");
                        mFileSortList.get(groupPosition).getLocationFiles().get(childPosition).setChecked(false);
                    } else {
                        mFinishTV.setText("完成（" + selectCount + "/" + limitCount + "）");
                    }
                    notifyDataSetChanged();
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
        }

        private class ChildHolder {
            CheckBox mCheckBox;
            ImageView mImageView;
            TextView mTextView;


        }
    }

}
