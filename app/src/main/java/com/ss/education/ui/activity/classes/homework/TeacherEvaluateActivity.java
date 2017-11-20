package com.ss.education.ui.activity.classes.homework;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.VoiceListInDetail;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.HomeworkFeedback;
import com.ss.education.entity.LocationFile;
import com.ss.education.entity.RecordModel;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SimpleDownloadListener;
import com.yanzhenjie.nohttp.download.SyncDownloadExecutor;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeacherEvaluateActivity extends BaseActivity {
    HomeworkFeedback mFeedback;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.content)
    TextView mContentTV;
    @Bind(R.id.voice_listview)
    NoScrollListView mVoiceListview;
    @Bind(R.id.pic_listview)
    NoScrollListView mPicListview;
    @Bind(R.id.file_listview)
    NoScrollListView mFileListview;
    @Bind(R.id.scrollview)
    ScrollView mScrollview;


    MyPicAdatper mPicAdatper;
    MyFileAdatper mFileAdatper;

    List<String> mPicList;
    private List<LocationFile> mOfficeFiles;
    List<RecordModel> mVoices;
    //语音
    public VoiceListInDetail voiceListInDetail;
    MediaPlayer mediaPlayer;
    private ProgressDialog mProgressDialog;

    String filePaths = "";

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
////                    showToast("文件保存到" + filePaths);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
//                    int index = filePaths.lastIndexOf(".");
//                    String ex = filePaths.substring(index);
//                    intent.setDataAndType(Uri.fromFile(new File(filePaths)), ex);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(intent);
//                    } else {
//                        OfficeFileUtils.openFile(TeacherEvaluateActivity.this, filePaths);
//                    }
                    HashMap<String, String> params = new HashMap<>();
//                    params.put("topBarBgColor", "#A0CBF0");
                    QbSdk.openFileReader(TeacherEvaluateActivity.this, filePaths, params, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                    mProgressDialog.dismiss();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_evaluate);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        initDialog();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            mFeedback = (HomeworkFeedback) extras.getSerializable("data");
        }
    }

    private void initView() {
        mTitle.setText("教师评语");
        mScrollview.smoothScrollBy(0, 0);
        try {
            mContentTV.setText(mFeedback.getEvaluate());
            int index = mFeedback.getScztime().lastIndexOf(":");
            String time = mFeedback.getScztime().substring(0, index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initData() {
        mPicList = new ArrayList<>();
        mOfficeFiles = new ArrayList<>();
        mVoices = new ArrayList<>();

        for (int i = 0; i < mFeedback.getEvaluatefile().size(); i++) {
            HomeworkFeedback.EvaluatefileBean workfileBean = mFeedback.getEvaluatefile().get(i);
            String path = mFeedback.getEvaluatefile().get(i).getFile();
            int index = path.lastIndexOf(".");
            String p = path.substring(index);
            if (p.equalsIgnoreCase(".jpg") || p.equalsIgnoreCase(".jpeg") || p.equalsIgnoreCase(".png") || p.equalsIgnoreCase(".gif") || p.equalsIgnoreCase(".bmp") || p.equalsIgnoreCase(".tiff")) {
                mPicList.add(path);
            } else if (p.equalsIgnoreCase(".pptx") || p.equalsIgnoreCase(".ppt")) {
                mOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 2));
            } else if (p.equalsIgnoreCase(".xls") || p.equalsIgnoreCase(".xlsx")) {
                mOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 1));
            } else if (p.equalsIgnoreCase(".doc") || p.equalsIgnoreCase(".docx")) {
                mOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 3));
            } else if (p.equalsIgnoreCase(".pdf")) {
                mOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 4));
            } else if (p.equalsIgnoreCase(".txt")) {
                mOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 5));
            } else if (p.equalsIgnoreCase(".amr")) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(ConnectUrl.FILE_PATH + path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mVoices.add(new RecordModel(mediaPlayer.getDuration() / 1000 + 1, path));

            }
        }

        mPicAdatper = new MyPicAdatper(this, R.layout.item_home_detail_pic, mPicList);
        mPicListview.setAdapter(mPicAdatper);

        mFileAdatper = new MyFileAdatper(this, R.layout.item_find_file_info, mOfficeFiles);
        mFileListview.setAdapter(mFileAdatper);

        voiceListInDetail = new VoiceListInDetail(this);
        voiceListInDetail.initVoice(mVoiceListview, mVoices);


    }

    private void initListener() {
        mPicListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goBasePhotoView(mPicList, position);
            }
        });
        mFileListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String url = ConnectUrl.FILE_PATH + mOfficeFiles.get(position).getPath();
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        httpDownLoadFile(url);
                    }
                }).start();

            }
        });
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在打开...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    public void httpDownLoadFile(String url) {
        String fileFolder = Environment.getExternalStorageDirectory().toString();
        DownloadRequest request = new DownloadRequest(url, RequestMethod.GET, fileFolder, true, false);
        SyncDownloadExecutor.INSTANCE.execute(0, request, new SimpleDownloadListener() {
            @Override
            public void onStart(int what, boolean resume, long range, Headers headers, long size) {
                // 开始下载，回调的时候说明文件开始下载了。
                // 参数1：what。
                // 参数2：是否是断点续传，从中间开始下载的。
                // 参数3：如果是断点续传，这个参数非0，表示之前已经下载的文件大小。
                // 参数4：服务器响应头。
                // 参数5：文件总大小，可能为0，因为服务器可能不返回文件大小。
//                mProgressDialog.show();
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                // 进度发生变化，服务器不返回文件总大小时不回调，因为没法计算进度。
                // 参数1：what。
                // 参数2：进度，[0-100]。
                // 参数3：文件总大小，可能为0，因为服务器可能不返回文件大小。
                // 参数4：下载的速度，含义为1S下载的byte大小，计算下载速度时：
                //        int xKB = (int) speed / 1024; // 单位：xKB/S
                //        int xM = (int) speed / 1024 / 1024; // 单位：xM/S
            }

            @Override
            public void onFinish(int what, String filePath) {
                // 下载完成，参数2为保存在本地的文件路径。
                filePaths = filePath;
            }
        });
        mHandler.sendEmptyMessage(1);
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }


    class MyPicAdatper extends CommonAdapter {

        public MyPicAdatper(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView image = holder.getView(R.id.item_photo);
            Glide.with(TeacherEvaluateActivity.this)
                    .load(ConnectUrl.PICURL + mPicList.get(position))
                    .centerCrop()
                    .placeholder(R.drawable.pic_fail)
                    .error(R.drawable.pic_fail)
                    .into(image);
        }
    }


    class MyFileAdatper extends CommonAdapter {
        private List<LocationFile> mList;

        public MyFileAdatper(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
            this.mList = datas;
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            CheckBox mCheckBox = holder.getView(R.id.file_checkbox);
            ImageView mImageView = holder.getView(R.id.office_image);
            TextView mTextView = holder.getView(R.id.file_name);
            mCheckBox.setVisibility(View.GONE);
            mTextView.setText(mList.get(position).getName());
            if (mList.get(position).getFlag() == 1) {
                Glide.with(mContext).load(R.drawable.icon_office_excel).into(mImageView);
            }
            if (mList.get(position).getFlag() == 2) {
                Glide.with(mContext).load(R.drawable.icon_office_excel).into(mImageView);
            }
            if (mList.get(position).getFlag() == 3) {
                Glide.with(mContext).load(R.drawable.icon_office_word).into(mImageView);
            }
            if (mList.get(position).getFlag() == 4) {
                Glide.with(mContext).load(R.drawable.icon_office_pdf).into(mImageView);
            }
            if (mList.get(position).getFlag() == 5) {
                Glide.with(mContext).load(R.drawable.icon_office_txt).into(mImageView);
            }
        }
    }
}
