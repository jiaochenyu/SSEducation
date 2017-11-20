package com.ss.education.ui.activity.classes.homework;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.base.VoiceListInDetail;
import com.ss.education.customview.NestedListView;
import com.ss.education.customview.NoScrollGridView;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.FileInfoBean;
import com.ss.education.entity.HomeWorkInfo;
import com.ss.education.entity.HomeworkFeedback;
import com.ss.education.entity.LocationFile;
import com.ss.education.entity.RecordModel;
import com.ss.education.entity.WpsModel;
import com.ss.education.ui.activity.photo.BigPhotoActivity;
import com.ss.education.utils.DensityUtil;
import com.ss.education.utils.ImageUtils;
import com.ss.education.utils.OfficeFileUtils;
import com.ss.education.utils.voiceutils.AudioRecordButton;
import com.ss.education.weight.MorePopupWindow;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SimpleDownloadListener;
import com.yanzhenjie.nohttp.download.SyncDownloadExecutor;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yongchun.library.view.ImageSelectorActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.base.Constant.LIMIT_PIC_NUM;
import static com.yanzhenjie.nohttp.NoHttp.getContext;
import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_IMAGE;

public class HomeWorkDetailActivity extends BaseActivity {

    private static final int IMAGE_ALBUM = 2;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.right_text)
    TextView mSettingTV;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.name)
    TextView mNameTV;
    @Bind(R.id.release_name)
    TextView mReleasePeoTV;
    @Bind(R.id.time)
    TextView mTimeTV;
    @Bind(R.id.content)
    TextView mContentTV;
    @Bind(R.id.pic_listview)
    NestedListView mPicListview;
    @Bind(R.id.voice_listview)
    NoScrollListView mVoiceListview;
    @Bind(R.id.file_listview)
    NoScrollListView mFileListview;
    @Bind(R.id.scrollview)
    ScrollView mScrollView;

    List<String> mPicList;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.back_text)
    TextView mBackText;
    @Bind(R.id.search_iv)
    ImageView mSearchIv;
    @Bind(R.id.message_num)
    TextView mMessageNum;
    @Bind(R.id.message)
    RelativeLayout mMessage;
    @Bind(R.id.close_iv)
    ImageView mCloseIv;
    @Bind(R.id.img_picbtn)
    ImageView mImgPicbtn;
    @Bind(R.id.img_voicebtn)
    ImageView mImgVoicebtn;
    @Bind(R.id.img_filebtn)
    ImageView mImgFilebtn;
    @Bind(R.id.submit_time)
    TextView mSubmitTimeTV;
    @Bind(R.id.submit_promt_layout)
    RelativeLayout mSubmitPromtLayout;
    @Bind(R.id.submit_homework_layout)
    LinearLayout mSubmitHomeworkLayout;
    @Bind(R.id.analysis_layout)
    RelativeLayout mAnalysisLayout;
    @Bind(R.id.completion_layout_teacher)
    RelativeLayout mCompletionTeacherRL;
    @Bind(R.id.submit_layout)
    RelativeLayout mSubmitLayout;
    @Bind(R.id.gridview)
    NoScrollGridView mSubmitPhotoGridview;
    @Bind(R.id.submit_voice_listview)
    NoScrollListView mSubmitVoiceListview;
    @Bind(R.id.submit_file_listview)
    NoScrollListView mSubmitFileListview;
    @Bind(R.id.submit_homework_content_edit)
    EditText mSubmitHomeworkContentET;
    @Bind(R.id.teacher_evaluate_layout)
    RelativeLayout mTeacherEvaluateLL; // 学生查看 老师的评价

    private List<LocationFile> mOfficeFiles;
    List<RecordModel> mVoices;

    HomeWorkInfo mHomeWorkInfo;
    MyPicAdatper mPicAdatper;
    MyFileAdatper mFileAdatper;

    MySubmitPhotoAdapter mSubPicAdapter;
    MyFileAdatper mSubFileAdatper;

    //语音
    public VoiceListInDetail voiceListInDetail;
    MediaPlayer mediaPlayer;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mUploadDialog;
    private Dialog mDialog; //录音
    String filePaths = "";

    private List<String> mSubmitPhotoPaths;  // 提交作业  选择图片
    private List<LocationFile> mSubmitOfficeFiles; // 提交作业  选择文件
    private List<RecordModel> mSubmitVoiceModels;  //提交作业 录音

    private String mSubHomeworkContentStr = "";

    private RequestQueue mQueue = NoHttp.newRequestQueue();

    private List<FileInfoBean> mServerFiles; //服务器文件地址


    private HomeworkFeedback mFeedback; // 老师的评价内容 学生当时提交的作业

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
//                    showToast("文件保存到" + filePaths);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
//                    int index = filePaths.lastIndexOf(".");
//                    String ex = filePaths.substring(index);
//                    intent.setDataAndType(Uri.fromFile(new File(filePaths)), ex);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(intent);
//                    } else {
//                        openFile(filePaths);
//                    }
                    HashMap<String, String> params = new HashMap<>();
//                    params.put("topBarBgColor", "#A0CBF0");
                    QbSdk.openFileReader(HomeWorkDetailActivity.this, filePaths, params, new ValueCallback<String>() {
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
        setContentView(R.layout.activity_home_work_detail);
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
            mHomeWorkInfo = (HomeWorkInfo) extras.getSerializable("homeDetail");
        }
    }

    private void initView() {
        mTitle.setText("作业详情");
        mSettingTV.setText("修改作业");
        mScrollView.smoothScrollBy(0, 0);
        try {
            if (MyApplication.getUser().getPart().equals("T")) {
                mSettingTV.setVisibility(View.VISIBLE);
            } else {
                mSettingTV.setVisibility(View.GONE);
            }
            mNameTV.setText(mHomeWorkInfo.getTitles());
            mContentTV.setText(mHomeWorkInfo.getRemarks());
            int index = mHomeWorkInfo.getCreatetime().lastIndexOf(":");
            mTimeTV.setText(mHomeWorkInfo.getCreatetime().substring(0, index));
            mReleasePeoTV.setText(mHomeWorkInfo.getFbrname());
            if (mHomeWorkInfo.getSubmitis().equals("F")) {
                mSubmitPromtLayout.setVisibility(View.GONE);
                mSubmitHomeworkLayout.setVisibility(View.GONE);
            } else if (mHomeWorkInfo.getSubmitis().equals("T") && MyApplication.getUser().getPart().equals("S")) {
                mSubmitPromtLayout.setVisibility(View.VISIBLE);
                mSubmitHomeworkLayout.setVisibility(View.VISIBLE);
                String time = mHomeWorkInfo.getJztimes();
                Log.e("结束时间", time);
                mSubmitTimeTV.setText("请在" + time.split("\\.")[0] + "之前提交作业");
            }
            if (MyApplication.getUser().getPart().equals("T")) {
                mAnalysisLayout.setVisibility(View.VISIBLE);
            } else if (MyApplication.getUser().getPart().equals("S")) {
                if (mHomeWorkInfo.getShowis().equals("T")) {
                    mAnalysisLayout.setVisibility(View.VISIBLE);
                } else {
                    mAnalysisLayout.setVisibility(View.GONE);
                }
            }

            if (MyApplication.getUser().getPart().equals("T")) {
                mCompletionTeacherRL.setVisibility(View.VISIBLE);
            } else {
                mCompletionTeacherRL.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initData() {
        mPicList = new ArrayList<>();
        mOfficeFiles = new ArrayList<>();
        mVoices = new ArrayList<>();

        mSubmitPhotoPaths = new ArrayList<>();
        mSubmitOfficeFiles = new ArrayList<>();
        mSubmitVoiceModels = new ArrayList<>();

        mServerFiles = new ArrayList<>();
        for (int i = 0; i < mHomeWorkInfo.getWorkfile().size(); i++) {
            HomeWorkInfo.WorkfileBean workfileBean = mHomeWorkInfo.getWorkfile().get(i);
            String path = mHomeWorkInfo.getWorkfile().get(i).getFile();
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
                mVoices.add(
                        new RecordModel(mediaPlayer.getDuration() / 1000 + 1, path)
                );

            }
        }

        mPicAdatper = new MyPicAdatper(this, R.layout.item_home_detail_pic, mPicList);
        mPicListview.setAdapter(mPicAdatper);
//        mPicListview.setSelection(View.FOCUS_DOWN);
        mFileAdatper = new MyFileAdatper(this, R.layout.item_find_file_info, mOfficeFiles);
        mFileListview.setAdapter(mFileAdatper);

        voiceListInDetail = new VoiceListInDetail(this);
        voiceListInDetail.initVoice(mVoiceListview, mVoices);


        mSubPicAdapter = new MySubmitPhotoAdapter(this, R.layout.item_photo, mSubmitPhotoPaths);
        mSubmitPhotoGridview.setAdapter(mSubPicAdapter);

        mSubFileAdatper = new MyFileAdatper(this, R.layout.item_find_file_info, mSubmitOfficeFiles);
        mSubmitFileListview.setAdapter(mSubFileAdatper);

        if (mHomeWorkInfo.getSubmitis().equals("F")) {   //是否需要提交//设置该作业已读未读
            httpUploadHomework(new JSONArray());
        } else {
            if (MyApplication.getUser().getPart().equals("S")) {
                httpGetMySubmit();  // 获取我当时提交的作业 和老师的评价
            }

        }


    }

    private void initListener() {

        mPicListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goPhotoView(view, position);
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

        mSubmitPhotoGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goBasePhotoView(mSubmitPhotoPaths, position);
            }
        });


        mSubmitFileListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MorePopupWindow morePopupWindow = new MorePopupWindow(HomeWorkDetailActivity.this, R.layout.popup_voice_del, DensityUtil.dip2px(getContext(), 60), DensityUtil.dip2px(getContext(), 30));
                final LinearLayout delBtn = (LinearLayout) morePopupWindow.getLayoutView().findViewById(R.id.pvd_ll_voicedel);
                morePopupWindow.showPopupUp(view, 0, DensityUtil.dip2px(getContext(), -8));
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //首先隐藏语音条栏，然后判断是否在播放，最后在删除数据
                        morePopupWindow.dismissPopup();
                        mSubmitOfficeFiles.remove(position);
                        mSubFileAdatper.notifyDataSetChanged();
                    }
                });

                return false;
            }
        });

        mSubmitFileListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
                intent.setDataAndType(Uri.fromFile(new File(mSubmitOfficeFiles.get(position).getPath())), mSubmitOfficeFiles.get(position).getExtension());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    OfficeFileUtils.openFile(HomeWorkDetailActivity.this, mSubmitOfficeFiles.get(position).getPath());
                }
            }
        });
    }


    private void initDialog() {
        mDialog = new Dialog(HomeWorkDetailActivity.this, R.style.style_dialog_full);
        mDialog.setContentView(R.layout.dialog_record_);
        AudioRecordButton recordBtn = (AudioRecordButton) mDialog.findViewById(R.id.fav_iv_voicebtn);
        recordBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, String filePath) {
                RecordModel recordModel = new RecordModel();
                recordModel.times = ((int) seconds) != 0 ? ((int) seconds) : 1;
                recordModel.path = filePath;

                mSubmitVoiceModels.add(recordModel);
                voiceListInDetail.initVoice(mSubmitVoiceListview, mSubmitVoiceModels);


                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) (mScreenHeight * 0.4); // 高度
        dialogWindow.setAttributes(lp);


        mUploadDialog = new ProgressDialog(this);
        mUploadDialog.setMessage("数据上传中...");
        mUploadDialog.setCanceledOnTouchOutside(false);
        mUploadDialog.setCancelable(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在打开...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    private void goPhotoView(View view, int position) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mPicList.size(); i++) {
            list.add(ConnectUrl.PICURL + mPicList.get(i));
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("pathList", (Serializable) list);
        bundle.putInt("position", position);
        go(BigPhotoActivity.class, bundle);
        overridePendingTransition(0, 0);
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


    boolean openFile(String path) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
        bundle.putString(WpsModel.THIRD_PACKAGE, getPackageName()); // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
        // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

        File file = new File(path);
        if (file == null || !file.exists()) {
            System.out.println("文件为空或者不存在");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
//            System.out.println("打开wps异常：" + e.toString());
            Log.e("打开wps异常", "true");
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @OnClick({R.id.back, R.id.right_text, R.id.close_iv, R.id.analysis_layout, R.id.img_picbtn, R.id.img_voicebtn, R.id.img_filebtn, R.id.submit_home_btn, R.id.completion_layout_teacher, R.id.teacher_evaluate_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", mHomeWorkInfo);
//                bundle.putSerializable("classid", classid);
                go(ReleaseHomeWorkActivity.class, bundle);
                break;
            case R.id.close_iv:
                mSubmitPromtLayout.setVisibility(View.GONE);
                break;
            case R.id.analysis_layout:
                Bundle b = new Bundle();
                b.putSerializable("data", mHomeWorkInfo);
                go(LookAnaysisActivity.class, b);
                break;
            case R.id.img_picbtn:
                selectorImage();
                break;
            case R.id.img_voicebtn:
                if (mSubmitVoiceModels.size() >= 3) {
                    showToast(R.string.max_voice_record);
                    return;
                }
                showDialogVoice();
                break;
            case R.id.img_filebtn:
                if (mSubmitOfficeFiles.size() == 5) {
                    showToast("最多选5个文件！");
                } else {
                    Bundle bf = new Bundle();
                    bf.putInt("alreadySelectCount", mOfficeFiles.size());
                    go(FindOfficeFileActivity.class, bf);
                }
                break;
            case R.id.submit_home_btn:
                juge();
                break;
            case R.id.completion_layout_teacher:
                Bundle bh = new Bundle();
                bh.putString("id", mHomeWorkInfo.getUuid());
                bh.putString("submitFlag", mHomeWorkInfo.getSubmitis());
                go(HomeworkCompletionActivity.class, bh);
                break;
            case R.id.teacher_evaluate_layout:
                Bundle be = new Bundle();
                be.putSerializable("data", mFeedback);
                go(TeacherEvaluateActivity.class, be);
                break;
        }
    }


    /**
     * 相册选择
     *
     * @param
     */
    private void selectorImage() {
        int count = LIMIT_PIC_NUM - mSubmitPhotoPaths.size();

        Intent intent = new Intent(HomeWorkDetailActivity.this, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.EXTRA_MAX_SELECT_NUM, count);
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, ImageSelectorActivity.MODE_MULTIPLE);
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_PREVIEW, true);
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_CROP, false);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showDialogVoice() {
        boolean isGrant = AndPermission.hasPermission(this, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!isGrant) {
            AndPermission.with(HomeWorkDetailActivity.this)
                    .requestCode(100)
                    .permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                            AlertDialog.newBuilder(HomeWorkDetailActivity.this)
                                    .setTitle("友好提醒")
                                    .setMessage("请开启麦克风权限！")
                                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            rationale.resume();
                                        }
                                    })
                                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    rationale.cancel();
                                                }
                                            }
                                    ).show();
                        }
                    })
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            if (requestCode == 200) {
                                mDialog.show();
                            }
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            if (requestCode == 200) {
                                showToast("开启权限失败");
                                mDialog.dismiss();
                            }
                        }
                    })
                    .start();
        } else {
            mDialog.show();

        }


    }


    private void juge() {
        mSubHomeworkContentStr = mSubmitHomeworkContentET.getText().toString();
        if (TextUtils.isEmpty(mSubHomeworkContentStr)
                && !adjustList(mSubmitPhotoPaths)
                && !adjustList(mSubmitOfficeFiles)
                && !adjustList(mSubmitVoiceModels)) {
            showToast("请完善作业内容！");
            return;
        }
        mServerFiles = new ArrayList<>();
        List<FileBinary> fileBinaries = new ArrayList<>();
        for (int i = 0; i < mSubmitPhotoPaths.size(); i++) {
            if (mSubmitPhotoPaths.get(i).contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mSubmitPhotoPaths.get(i)));
            } else {
                fileBinaries.add(new FileBinary(new File(mSubmitPhotoPaths.get(i))));
            }
        }
        for (int i = 0; i < mSubmitVoiceModels.size(); i++) {
            if (mSubmitVoiceModels.get(i).path.contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mSubmitVoiceModels.get(i).path));
            } else {
                fileBinaries.add(new FileBinary(new File(mSubmitVoiceModels.get(i).path)));
            }
        }
        for (int i = 0; i < mSubmitOfficeFiles.size(); i++) {
            if (mSubmitOfficeFiles.get(i).getPath().contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mSubmitOfficeFiles.get(i).getPath(), mSubmitOfficeFiles.get(i).getName()));
            } else {
                fileBinaries.add(new FileBinary(new File(mSubmitOfficeFiles.get(i).getPath())));
            }
        }
        if (!adjustList(fileBinaries)) {

            try {
                JSONArray a = new JSONArray(new Gson().toJson(mServerFiles));
                httpUploadHomework(a);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            httpUploadFile(fileBinaries);
        }


    }

    private void httpUploadFile(List<FileBinary> fileBinaries) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.UPLOAD_COMMON_FILE, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        for (int i = 0; i < fileBinaries.size(); i++) {
            request.add("file", fileBinaries.get(i));
        }

        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mUploadDialog.show();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            JSONArray array = jsonObject.getJSONArray("array");
                            for (int i = 0; i < array.length(); i++) {
                                FileInfoBean file = gson.fromJson(array.getJSONObject(i).toString(), FileInfoBean.class);
                                mServerFiles.add(file);
                            }
                            JSONArray a = new JSONArray(new Gson().toJson(mServerFiles));
                            Log.e("array", a.toString());
                            httpUploadHomework(a);
                        } else if (jsonObject.getString("status").equals("-102")) {
                            mUploadDialog.dismiss();
                            showToast("发布失败，请重新发布！");
                        } else {
                            mUploadDialog.dismiss();
                            showToast("发布失败，请重新发布！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("上传", response.get().toString());
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Log.e("上传sss", "失败");
                    mUploadDialog.dismiss();

                }
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    private void httpUploadHomework(JSONArray fileArray) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.SUBMIT_HOME_WORK, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        JSONObject object = new JSONObject();
        try {
            object.put("workid", mHomeWorkInfo.getUuid());
            object.put("readis", "T");
            if (mHomeWorkInfo.getShowis().equals("T")) {
                object.put("feedback", mSubHomeworkContentStr);
                object.put("feedbackfile", fileArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", object.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mUploadDialog.show();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            if (mHomeWorkInfo.getSubmitis().equals("T")) {
                                showToast("提交作业成功");
                                finish();
                            }
//                            EventBus.getDefault().post(new EventFlag(Constant.EvReleaseSucc));

                        } else {
                            if (mHomeWorkInfo.getShowis().equals("T")) {
                                showToast("提交失败，请重新提交！");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("上传", response.get().toString());
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    Log.e("发布失败", "失败");
                }
            }

            @Override
            public void onFinish(int what) {
                mUploadDialog.dismiss();
            }
        });
    }

    private void httpGetMySubmit() {  //学生获取作业反馈
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_HOME_WORK_FEEDBACK_STUDENT, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        final JSONObject object = new JSONObject();
        try {
            object.put("workid", mHomeWorkInfo.getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", object.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mUploadDialog.show();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            mFeedback = gson.fromJson(jsonObject.getJSONObject("object").toString(), HomeworkFeedback.class);
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {

                }
            }

            @Override
            public void onFinish(int what) {
                mUploadDialog.dismiss();
                setView();
            }
        });
    }

    private void setView() {
        if (mFeedback.getEvaluateis().equals("F")) {
            mTeacherEvaluateLL.setVisibility(View.GONE);
        } else {
            mTeacherEvaluateLL.setVisibility(View.VISIBLE);
        }
        mSubmitHomeworkContentET.setText(mFeedback.getFeedback());
        for (int i = 0; i < mFeedback.getFeedbackfile().size(); i++) {
            HomeworkFeedback.FeedbackfileBean workfileBean = mFeedback.getFeedbackfile().get(i);
            String path = mFeedback.getFeedbackfile().get(i).getFile();
            int index = path.lastIndexOf(".");
            String p = path.substring(index);
            if (p.equalsIgnoreCase(".jpg") || p.equalsIgnoreCase(".jpeg") || p.equalsIgnoreCase(".png") || p.equalsIgnoreCase(".gif") || p.equalsIgnoreCase(".bmp") || p.equalsIgnoreCase(".tiff")) {
                mSubmitPhotoPaths.add(path);
            } else if (p.equalsIgnoreCase(".pptx") || p.equalsIgnoreCase(".ppt")) {
                mSubmitOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 2));
            } else if (p.equalsIgnoreCase(".xls") || p.equalsIgnoreCase(".xlsx")) {
                mSubmitOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 1));
            } else if (p.equalsIgnoreCase(".doc") || p.equalsIgnoreCase(".docx")) {
                mSubmitOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 3));
            } else if (p.equalsIgnoreCase(".pdf")) {
                mSubmitOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 4));
            } else if (p.equalsIgnoreCase(".txt")) {
                mSubmitOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 5));
            } else if (p.equalsIgnoreCase(".amr")) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(ConnectUrl.FILE_PATH + path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSubmitVoiceModels.add(
                        new RecordModel(mediaPlayer.getDuration() / 1000 + 1, path)
                );

            }
        }
        mSubPicAdapter.notifyDataSetChanged();
        mSubFileAdatper.notifyDataSetChanged();
        voiceListInDetail.initVoice(mSubmitVoiceListview, mSubmitVoiceModels);

    }


    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (MyApplication.getUser().getPart().equals("S")) {
            if (eventFlag.getFlag().equals(Constant.EvFinishOfficeSelectFile)) {
                mSubmitOfficeFiles.addAll((Collection<? extends LocationFile>) eventFlag.getObject());
                mSubFileAdatper.notifyDataSetChanged();
                Log.e("detail接收", "true");
            }
        }
        if (eventFlag.getFlag().equals(Constant.EvReleaseSucc)) {
            httpGetData();
        }
    }

    private void httpGetData() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.GET_HOME_WORK_INFO, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        final JSONObject object = new JSONObject();
        try {
            object.put("workid", mHomeWorkInfo.getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.add("data", object.toString());
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            mHomeWorkInfo = gson.fromJson(jsonObject.getJSONObject("object").toString(), HomeWorkInfo.class);
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {

                }
            }

            @Override
            public void onFinish(int what) {
                hideLoading();
                initView();
                initData();
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                ArrayList<String> mSelectPath = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                for (int i = 0; i < mSelectPath.size(); i++) {
                    mSubmitPhotoPaths.add(mSelectPath.get(i));
                }
                mSubPicAdapter.notifyDataSetChanged();
            }
        }
    }


    class MyPicAdatper extends CommonAdapter {

        public MyPicAdatper(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView image = holder.getView(R.id.item_photo);
            Glide.with(HomeWorkDetailActivity.this)
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


    public class MySubmitPhotoAdapter extends CommonAdapter {
        private List<String> mlist;

        public MySubmitPhotoAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
            this.mContext = context;
            this.mlist = datas;
        }


        @Override
        protected void convert(ViewHolder viewHolder, Object item, final int position) {
            ImageView pic = viewHolder.getView(R.id.item_photo_grid);
            ImageView deleteBtn = viewHolder.getView(R.id.delete_btn);
            //逻辑判断
            if (mlist.get(position).contains("upload/")) {
                ImageUtils.setDefaultNormalImage(pic, ConnectUrl.FILE_PATH + mlist.get(position), R.drawable.icon_def_pic);
            } else {
                ImageUtils.setDefaultNormalImage(pic, mlist.get(position), R.drawable.icon_def_pic);

            }


            viewHolder.setOnClickListener(R.id.delete_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlist.remove(position);
                    notifyDataSetChanged();

                }
            });
        }


    }


    @Override
    public void onPause() {
        if (voiceListInDetail != null) {
            voiceListInDetail.stopVoice();
        }
        super.onPause();

    }


}
