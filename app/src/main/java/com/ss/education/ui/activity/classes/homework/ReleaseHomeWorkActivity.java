package com.ss.education.ui.activity.classes.homework;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.NoScrollGridView;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.FileInfoBean;
import com.ss.education.entity.HomeWorkInfo;
import com.ss.education.entity.LocationFile;
import com.ss.education.entity.RecordModel;
import com.ss.education.entity.Student;
import com.ss.education.entity.StudentGroup;
import com.ss.education.ui.activity.photo.BigPhotoActivity;
import com.ss.education.utils.DateUtils;
import com.ss.education.utils.DensityUtil;
import com.ss.education.utils.ImageUtils;
import com.ss.education.utils.LHFileUtils;
import com.ss.education.utils.voiceutils.AudioRecordButton;
import com.ss.education.utils.voiceutils.MediaManager;
import com.ss.education.utils.voiceutils.VoiceLineUtils;
import com.ss.education.weight.MorePopupWindow;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.SimpleUploadListener;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ss.education.base.Constant.LIMIT_PIC_NUM;
import static com.yanzhenjie.nohttp.NoHttp.createJsonObjectRequest;
import static com.yanzhenjie.nohttp.NoHttp.getContext;

public class ReleaseHomeWorkActivity extends BaseActivity {
    private final static int ANALYSIS = 666;  //解析
    private final static int HOME_CONTENT = 777;

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_text)
    TextView mRightText;
    @BindView(R.id.home_title_edit)
    EditText mTitleEdit;
    @BindView(R.id.home_content_edit)
    EditText mContentEdit;
    @BindView(R.id.gridview)
    NoScrollGridView mPicGridview;
    @BindView(R.id.voice_listview)
    NoScrollListView mVoiceListview;
    @BindView(R.id.file_listview)
    NoScrollListView mFileListview;
    @BindView(R.id.receiver)
    TextView mReceiverTV;
    @BindView(R.id.switch_submit_homework)
    Switch mSwitchSubmitHomework;
    @BindView(R.id.hw_end_time)
    TextView mHwEndTimeTV;
    @BindView(R.id.end_time_layout)
    RelativeLayout mEndTimeLayout;
    @BindView(R.id.online_submit_layout)
    LinearLayout mOnlineSubmitLayout;
    @BindView(R.id.switch_analysis)
    Switch mSwitchAnalysis;
    @BindView(R.id.analysis_gridview)
    NoScrollGridView mAnalysisGridview;
    @BindView(R.id.analysis_voice_listview)
    NoScrollListView mAnalysisVoiceListview;
    @BindView(R.id.analysis_file_listview)
    NoScrollListView mAnalysisFileListview;
    @BindView(R.id.analysis_img_picbtn)
    ImageView mAnalysisImgPicbtn;
    @BindView(R.id.analysis_img_voicebtn)
    ImageView mAnalysisImgVoicebtn;
    @BindView(R.id.analysis_img_filebtn)
    ImageView mAnalysisImgFilebtn;
    @BindView(R.id.switch_show_analysis)
    Switch mSwitchShowAnalysis;
    @BindView(R.id.analysis_layout)
    LinearLayout mAnalysisLayout;
    @BindView(R.id.analysis_content_edit)
    EditText mAnalysisET;
    @BindView(R.id.receive_layout)
    RelativeLayout mReceiveLL;


    MyPhotoAdapter mPicAdapter;
    MyVoiceAdapter mVoiceAdapter;
    MyOfficeFileAdapter mOfficeFileAdapter;
    MyPhotoAdapter mAnaPicAdapter;  //解析
    MyVoiceAdapter mAnaVoiceAdapter;
    MyOfficeFileAdapter mAnaOfficeFileAdapter;
    Dialog mDialog;
    Dialog mDialog2; // 解析
    List<String> mPhotoPaths;
    private List<RecordModel> mRecordModels;  //录音
    private List<LocationFile> mOfficeFiles;   //office文件

    List<String> mAnaPhotoPaths;  //解析
    private List<RecordModel> mAnaRecordModels;  //解析 录音
    private List<LocationFile> mAnaOfficeFiles;   //解析 office文件
    //帧动画控制器
    private AnimationDrawable animationDrawable;  // 语音播放时的动画
    //动画集合
    List<AnimationDrawable> mAnimationDrawables; // 语音播放时的动画
    private int pos = -1;// 语音播放 标记当前录音索引//默认没有播放任何一个
    boolean isPlaying;//是否正在播放


    String classId = "";
    List<StudentGroup> mGroupList;

    List<Student> mReceiverStus;

    ProgressDialog mProgressDialog;
    private RequestQueue mQueue = NoHttp.newRequestQueue();


    private String titleStr = "";
    private String contentStr = "";
    private long dateStamp = 0;//时间戳
    private int type = -1; // 语音录制的时候  判断是解析还是作业

    private String showIs = "T";//是否显示解析
    private String submitIs = "F";//是否需要提交
    private String anaContentStr = ""; //解析内容
//    private int fileFlag = 0;   //上传文件的时候 判断作业和解析文件表示

    private boolean addAnaysis = false;

    HomeWorkInfo mHomeWorkInfo;  //修改作业的

    private List<FileInfoBean> mAnaServerFiles;  //解析文件网络地址
    private List<FileInfoBean> mServerFiles; //服务器文件地址

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_home_work);
        ButterKnife.bind(this);
        initView();
        initData();
        initDialog();
        initListener();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            classId = extras.getString("classid", "");
            mGroupList = (List<StudentGroup>) extras.getSerializable("dataList");

            mHomeWorkInfo = (HomeWorkInfo) extras.getSerializable("data");
        }
    }

    private void initView() {
        mRightText.setText("发布");
        mRightText.setVisibility(View.VISIBLE);
        if (mHomeWorkInfo != null) {
            mTitle.setText("修改作业");
            mReceiveLL.setClickable(false);
            try {
                mTitleEdit.setText(mHomeWorkInfo.getTitles());
                mContentEdit.setText(mHomeWorkInfo.getRemarks());
                if (mHomeWorkInfo.getShowis().equals("T")) {
                    showIs = "T";
                    mSwitchShowAnalysis.setChecked(true);
                    mAnalysisET.setText(mHomeWorkInfo.getAnalysis());
                } else {
                    showIs = "F";
                    mSwitchShowAnalysis.setChecked(false);
                }
                if (TextUtils.isEmpty(mHomeWorkInfo.getAnalysis())) {
                    mSwitchAnalysis.setChecked(false);
                    mAnalysisLayout.setVisibility(View.GONE);
                } else {
                    mSwitchAnalysis.setChecked(true);
                    mAnalysisLayout.setVisibility(View.VISIBLE);
                }
                if (mHomeWorkInfo.getSubmitis().equals("T")) {
                    submitIs = "T";
                    mSwitchSubmitHomework.setChecked(true);
                    mOnlineSubmitLayout.setVisibility(View.VISIBLE);
                    mHwEndTimeTV.setText(mHomeWorkInfo.getJztimes().split(" ")[0]);
                    dateStamp = DateUtils.getStringToDate(mHomeWorkInfo.getJztimes());
                } else {
                    submitIs = "F";
                    mOnlineSubmitLayout.setVisibility(View.VISIBLE);
                    mSwitchSubmitHomework.setChecked(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mTitle.setText("发布作业");
        }
    }

    private void initData() {
        mPhotoPaths = new ArrayList<>();
        mRecordModels = new ArrayList<>();
        mOfficeFiles = new ArrayList<>();
        mAnimationDrawables = new ArrayList<>();
        mReceiverStus = new ArrayList<>();
        mServerFiles = new ArrayList<>();

        //解析
        mAnaServerFiles = new ArrayList<>();
        //解析
        mAnaPhotoPaths = new ArrayList<>();
        mAnaRecordModels = new ArrayList<>();
        mAnaOfficeFiles = new ArrayList<>();

        if (mHomeWorkInfo != null) {

            for (int i = 0; i < mHomeWorkInfo.getWorkfile().size(); i++) {
                HomeWorkInfo.WorkfileBean workfileBean = mHomeWorkInfo.getWorkfile().get(i);
                String path = mHomeWorkInfo.getWorkfile().get(i).getFile();
                int index = path.lastIndexOf(".");
                String p = path.substring(index);
                if (p.equalsIgnoreCase(".jpg") || p.equalsIgnoreCase(".jpeg") || p.equalsIgnoreCase(".png") || p.equalsIgnoreCase(".gif") || p.equalsIgnoreCase(".bmp") || p.equalsIgnoreCase(".tiff")) {
                    mPhotoPaths.add(path);
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
                    mRecordModels.add(
                            new RecordModel(mediaPlayer.getDuration() / 1000 + 1, path)
                    );

                }
            }

            if (mHomeWorkInfo.getAnalysis().equals("T")) {
                for (int i = 0; i < mHomeWorkInfo.getAnalysisfile().size(); i++) {
                    HomeWorkInfo.AnalysisfileBean workfileBean = mHomeWorkInfo.getAnalysisfile().get(i);
                    String path = mHomeWorkInfo.getAnalysisfile().get(i).getFile();
                    int index = path.lastIndexOf(".");
                    String p = path.substring(index);
                    if (p.equalsIgnoreCase(".jpg") || p.equalsIgnoreCase(".jpeg") || p.equalsIgnoreCase(".png") || p.equalsIgnoreCase(".gif") || p.equalsIgnoreCase(".bmp") || p.equalsIgnoreCase(".tiff")) {
                        mAnaPhotoPaths.add(path);
                    } else if (p.equalsIgnoreCase(".pptx") || p.equalsIgnoreCase(".ppt")) {
                        mAnaOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 2));
                    } else if (p.equalsIgnoreCase(".xls") || p.equalsIgnoreCase(".xlsx")) {
                        mAnaOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 1));
                    } else if (p.equalsIgnoreCase(".doc") || p.equalsIgnoreCase(".docx")) {
                        mAnaOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 3));
                    } else if (p.equalsIgnoreCase(".pdf")) {
                        mAnaOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 4));
                    } else if (p.equalsIgnoreCase(".txt")) {
                        mAnaOfficeFiles.add(new LocationFile(path, workfileBean.getOldname(), 5));
                    } else if (p.equalsIgnoreCase(".amr")) {
                        try {
                            mediaPlayer.setDataSource(ConnectUrl.FILE_PATH + path);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mAnaRecordModels.add(
                                new RecordModel(mediaPlayer.getDuration() / 1000 + 1, path)
                        );

                    }
                }
            }

        }
        //作业内容
        mPicAdapter = new MyPhotoAdapter(this, R.layout.item_photo, mPhotoPaths);
        mPicGridview.setAdapter(mPicAdapter);
        mVoiceAdapter = new MyVoiceAdapter(this, R.layout.item_add_voice, mRecordModels);
        mVoiceListview.setAdapter(mVoiceAdapter);
        mOfficeFileAdapter = new MyOfficeFileAdapter(this, R.layout.item_find_file_info, mOfficeFiles);
        mFileListview.setAdapter(mOfficeFileAdapter);


        mAnaPicAdapter = new MyPhotoAdapter(this, R.layout.item_photo, mAnaPhotoPaths);
        mAnalysisGridview.setAdapter(mAnaPicAdapter);
        mAnaVoiceAdapter = new MyVoiceAdapter(this, R.layout.item_add_voice, mAnaRecordModels);
        mAnalysisVoiceListview.setAdapter(mAnaVoiceAdapter);
        mAnaOfficeFileAdapter = new MyOfficeFileAdapter(this, R.layout.item_find_file_info, mAnaOfficeFiles);
        mAnalysisFileListview.setAdapter(mAnaOfficeFileAdapter);

    }


    private void initDialog() {
        mDialog = new Dialog(ReleaseHomeWorkActivity.this, R.style.style_dialog_full);
        mDialog.setContentView(R.layout.dialog_record_);
        AudioRecordButton recordBtn = (AudioRecordButton) mDialog.findViewById(R.id.fav_iv_voicebtn);
        recordBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, String filePath) {
                RecordModel recordModel = new RecordModel();
                recordModel.times = ((int) seconds) != 0 ? ((int) seconds) : 1;
                recordModel.path = filePath;
                if (type == HOME_CONTENT) {
                    mRecordModels.add(recordModel);
                    mVoiceAdapter.notifyDataSetChanged();
                } else {
                    mAnaRecordModels.add(recordModel);
                    mAnaVoiceAdapter.notifyDataSetChanged();
                }

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


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("数据上传中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    private void initListener() {
        mFileListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MorePopupWindow morePopupWindow = new MorePopupWindow(ReleaseHomeWorkActivity.this, R.layout.popup_voice_del, DensityUtil.dip2px(getContext(), 60), DensityUtil.dip2px(getContext(), 30));
                final LinearLayout delBtn = (LinearLayout) morePopupWindow.getLayoutView().findViewById(R.id.pvd_ll_voicedel);
                morePopupWindow.showPopupUp(view, 0, DensityUtil.dip2px(getContext(), -8));
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //首先隐藏语音条栏，然后判断是否在播放，最后在删除数据
                        morePopupWindow.dismissPopup();
                        mOfficeFiles.remove(position);
                        mOfficeFileAdapter.notifyDataSetChanged();
                    }
                });

                return false;
            }
        });
        mFileListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
//                intent.setDataAndType(Uri.fromFile(new File(mOfficeFiles.get(position).getPath())), mOfficeFiles.get(position).getExtension());
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
                HashMap<String, String> params = new HashMap<>();
//                    params.put("topBarBgColor", "#A0CBF0");
                QbSdk.openFileReader(ReleaseHomeWorkActivity.this, mOfficeFiles.get(position).getPath(), params, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }


        });

        mPicGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goBasePhotoView(mPhotoPaths,position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("pathList", (Serializable) mPhotoPaths);
//                bundle.putInt("position", position);
//                go(BigPhotoActivity.class, bundle);
//                overridePendingTransition(0, 0);
            }
        });
        //需要提交作业
        mSwitchSubmitHomework.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOnlineSubmitLayout.setVisibility(View.VISIBLE);
                    submitIs = "T";
                } else {
                    mOnlineSubmitLayout.setVisibility(View.GONE);
                    submitIs = "F";
                }

            }
        });
        mSwitchAnalysis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAnalysisLayout.setVisibility(View.VISIBLE);
                } else {
                    mAnalysisLayout.setVisibility(View.GONE);
                }
            }
        });
        mSwitchShowAnalysis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showIs = "T";
                } else {
                    showIs = "F";
                }
            }
        });


        mAnalysisFileListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MorePopupWindow morePopupWindow = new MorePopupWindow(ReleaseHomeWorkActivity.this, R.layout.popup_voice_del, DensityUtil.dip2px(getContext(), 60), DensityUtil.dip2px(getContext(), 30));
                final LinearLayout delBtn = (LinearLayout) morePopupWindow.getLayoutView().findViewById(R.id.pvd_ll_voicedel);
                morePopupWindow.showPopupUp(view, 0, DensityUtil.dip2px(getContext(), -8));
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //首先隐藏语音条栏，然后判断是否在播放，最后在删除数据
                        morePopupWindow.dismissPopup();
                        mAnaOfficeFiles.remove(position);
                        mAnaOfficeFileAdapter.notifyDataSetChanged();
                    }
                });

                return false;
            }
        });
        mFileListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
                intent.setDataAndType(Uri.fromFile(new File(mOfficeFiles.get(position).getPath())), mOfficeFiles.get(position).getExtension());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        mPicGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("pathList", (Serializable) mPhotoPaths);
                bundle.putInt("position", position);
                go(BigPhotoActivity.class, bundle);
                overridePendingTransition(0, 0);
            }
        });


    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvFinishOfficeSelectFile)) {
            int type = eventFlag.getPosition();
            if (type == HOME_CONTENT) {
                mOfficeFiles.addAll((Collection<? extends LocationFile>) eventFlag.getObject());
                mOfficeFileAdapter.notifyDataSetChanged();
            } else if (type == ANALYSIS) {
                mAnaOfficeFiles.addAll((Collection<? extends LocationFile>) eventFlag.getObject());
                mAnaOfficeFileAdapter.notifyDataSetChanged();
            }

        }

        if (eventFlag.getFlag().equals(Constant.EvSelectReceive_homework)) {
            mReceiverStus.clear();
            mReceiverStus.addAll((Collection<? extends Student>) eventFlag.getObject());
            mReceiverTV.setText("已选择" + mReceiverStus.size() + "人");
        }
        if (eventFlag.getFlag().equals(Constant.EvSelectAll_Receive)) {
            mReceiverStus.clear();
            mReceiverStus.addAll(mGroupList.get(0).getStudents());
            mReceiverTV.setText("已选择" + mReceiverStus.size() + "人");
        }
    }

    @OnClick({R.id.back, R.id.right_text, R.id.img_picbtn, R.id.img_filebtn, R.id.img_voicebtn, R.id.receive_layout, R.id.analysis_img_picbtn, R.id.analysis_img_voicebtn, R.id.analysis_img_filebtn, R.id.end_time_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_text:
                juge();
                break;
            case R.id.img_picbtn:
                selectorImage(HOME_CONTENT);
                break;
            case R.id.img_voicebtn:
                type = HOME_CONTENT;
                if (mRecordModels.size() >= 3) {
                    showToast(R.string.max_voice_record);
                    return;
                }
                showDialogVoice();
                break;
            case R.id.img_filebtn:
                if (mOfficeFiles.size() == 5) {
                    showToast("最多选5个文件！");
                } else {
                    Bundle b = new Bundle();
                    b.putInt("alreadySelectCount", mOfficeFiles.size());
                    b.putInt("type", HOME_CONTENT);
                    go(FindOfficeFileActivity.class, b);
                }

                break;

            case R.id.receive_layout:
                if (adjustList(mGroupList)) {
                    Bundle b = new Bundle();
                    b.putSerializable("dataList", (Serializable) mGroupList);
                    go(SelectReceiverActivity.class, b);
                } else {
                    showToast("此班级还没有学生加入！");
                }

                break;

            case R.id.analysis_img_picbtn:
                selectorImage(ANALYSIS);
                break;
            case R.id.analysis_img_voicebtn:
                type = ANALYSIS;
                if (mAnaRecordModels.size() >= 3) {
                    showToast(R.string.max_voice_record);
                    return;
                }
                showDialogVoice();
                break;
            case R.id.analysis_img_filebtn:
                if (mAnaOfficeFiles.size() == 5) {
                    showToast("最多选5个文件！");
                } else {
                    Bundle b = new Bundle();
                    b.putInt("alreadySelectCount", mAnaOfficeFiles.size());
                    b.putInt("type", ANALYSIS);
                    go(FindOfficeFileActivity.class, b);
                }
                break;
            case R.id.end_time_layout:
                pickTime();
                break;

        }
    }

    private void pickTime() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(ReleaseHomeWorkActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
                String date = (String) DateFormat.format("yyy-MM-dd", c);
                dateStamp = DateUtils.getStringToDate(date);
                Log.e("时间戳", dateStamp + "");
                mHwEndTimeTV.setText(date);
            }
        }, year, month, day);
        dialog.show();

    }


    private void showDialogVoice() {
        boolean isGrant = AndPermission.hasPermission(this, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!isGrant) {
            AndPermission.with(ReleaseHomeWorkActivity.this)
                    .requestCode(100)
                    .permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                            AlertDialog.newBuilder(ReleaseHomeWorkActivity.this)
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

    /**
     * 相册选择
     *
     * @param
     */
    private void selectorImage(int type) {
        int count = LIMIT_PIC_NUM - mPhotoPaths.size();

        Intent intent = new Intent(ReleaseHomeWorkActivity.this, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.EXTRA_MAX_SELECT_NUM, count);
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, ImageSelectorActivity.MODE_MULTIPLE);
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_PREVIEW, true);
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_CROP, false);
//        startActivityForResult(intent, REQUEST_IMAGE);
        startActivityForResult(intent, type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == HOME_CONTENT) {
                ArrayList<String> mSelectPath = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

                for (int i = 0; i < mSelectPath.size(); i++) {
                    mPhotoPaths.add(mSelectPath.get(i));
                }
                mPicAdapter.notifyDataSetChanged();
            } else if (requestCode == ANALYSIS) {
                ArrayList<String> mSelectPath = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                for (int i = 0; i < mSelectPath.size(); i++) {
                    mAnaPhotoPaths.add(mSelectPath.get(i));
                }
                mAnaPicAdapter.notifyDataSetChanged();
            }

        }
    }

    private void juge() {
        titleStr = mTitleEdit.getText().toString();
        contentStr = mContentEdit.getText().toString();
        anaContentStr = mAnalysisET.getText().toString();
        if (TextUtils.isEmpty(titleStr)) {
            showToast("请输入标题！");
            return;
        }
        if (mHomeWorkInfo == null) {
            if (!adjustList(mReceiverStus)) {
                showToast("请选择接收人！");
                return;
            }
        }

        if (submitIs.equals("T")) {
            if (dateStamp == 0) {
                showToast("请选择截止日期！");
                return;
            }
        }

        if (mSwitchAnalysis.isChecked()) {
            if (TextUtils.isEmpty(anaContentStr)) {
                showToast("请输入解析内容!");
                return;
            }
        }
        mServerFiles = new ArrayList<>();
        List<FileBinary> fileBinaries = new ArrayList<>();
        for (int i = 0; i < mPhotoPaths.size(); i++) {
            if (mPhotoPaths.get(i).contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mPhotoPaths.get(i), "", "workfile"));
            } else {
                fileBinaries.add(new FileBinary(new File(mPhotoPaths.get(i))));
            }
        }
        for (int i = 0; i < mRecordModels.size(); i++) {
            if (mRecordModels.get(i).path.contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mRecordModels.get(i).path, "", "workfile"));
            } else {
                fileBinaries.add(new FileBinary(new File(mRecordModels.get(i).path)));
            }
        }
        for (int i = 0; i < mOfficeFiles.size(); i++) {
            if (mOfficeFiles.get(i).getPath().contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mOfficeFiles.get(i).getPath(), mOfficeFiles.get(i).getName(), "workfile"));
            } else {
                fileBinaries.add(new FileBinary(new File(mOfficeFiles.get(i).getPath())));
            }
        }

        List<FileBinary> fileBinariesAna = new ArrayList<>();
        for (int i = 0; i < mAnaPhotoPaths.size(); i++) {
            if (mAnaPhotoPaths.get(i).contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mAnaPhotoPaths.get(i), "", "analysisfile"));
            } else {
                fileBinariesAna.add(new FileBinary(new File(mAnaPhotoPaths.get(i))));
            }
        }
        for (int i = 0; i < mAnaRecordModels.size(); i++) {
            if (mAnaRecordModels.get(i).path.contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mAnaRecordModels.get(i).path, "", "analysisfile"));
            } else {
                fileBinariesAna.add(new FileBinary(new File(mAnaRecordModels.get(i).path)));
            }
        }
        for (int i = 0; i < mAnaOfficeFiles.size(); i++) {
            if (mAnaOfficeFiles.get(i).getPath().contains("upload/")) {
                mServerFiles.add(new FileInfoBean(mAnaOfficeFiles.get(i).getPath(), mAnaOfficeFiles.get(i).getName(), "analysisfile"));
            } else {
                fileBinariesAna.add(new FileBinary(new File(mAnaOfficeFiles.get(i).getPath())));
            }
        }

        if (!adjustList(fileBinaries) && !adjustList(fileBinariesAna)) {
            if (adjustList(mServerFiles)) {
                try {
                    httpUploadHomework(new JSONArray(new Gson().toJson(mServerFiles)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                httpUploadHomework(new JSONArray());
            }

        } else {
            httpUploadFile(fileBinaries, fileBinariesAna);
        }


    }

    private void httpUploadFile(List<FileBinary> fileBinaries, List<FileBinary> fileBinariesAna) {
        Request<JSONObject> request = createJsonObjectRequest(ConnectUrl.UPLOAD_COMMON_FILE, RequestMethod.POST);
        request.add("useruuid", MyApplication.getUser().getUuid());
        for (int i = 0; i < fileBinaries.size(); i++) {
            fileBinaries.get(i).setUploadListener(0, new SimpleUploadListener() {
                @Override
                public void onStart(int what) {
                    super.onStart(what);
                }

                @Override
                public void onProgress(int what, int progress) {
                    super.onProgress(what, progress);
                    Log.e("上传进度", "onProgress: " + progress);
                }

                @Override
                public void onCancel(int what) {
                    super.onCancel(what);
                }

                @Override
                public void onFinish(int what) {
                    super.onFinish(what);
                }

                @Override
                public void onError(int what, Exception exception) {
                    super.onError(what, exception);
                }
            });
            request.add("workfile", fileBinaries.get(i));
        }
        for (int i = 0; i < fileBinariesAna.size(); i++) {
            request.add("analysisfile", fileBinariesAna.get(i));
        }
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mProgressDialog.show();
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
                            httpUploadHomework(new JSONArray(new Gson().toJson(mServerFiles)));
                        } else if (jsonObject.getString("status").equals("-102")) {
//                            httpUploadHomework(new JSONArray());
                            mProgressDialog.dismiss();
                            showToast("发布失败，请重新发布！");
                        } else {
                            mProgressDialog.dismiss();
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
                    mProgressDialog.dismiss();

                }
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }


    private void httpUploadHomework(JSONArray fileArray) {
        Request<JSONObject> request = null;
        try {
            JSONObject object = new JSONObject();
            if (mHomeWorkInfo == null) {
                request = NoHttp.createJsonObjectRequest(ConnectUrl.RELEASE_HOME_WORK, RequestMethod.POST);
            } else {
                request = createJsonObjectRequest(ConnectUrl.UPDATE_HOME_WORK, RequestMethod.POST);
                object.put("workid", mHomeWorkInfo.getUuid());
            }
            object.put("titles", titleStr);
            object.put("jztimes", dateStamp);
            object.put("remarks", contentStr);
            object.put("classid", classId);
            object.put("receivetype", "G");
            object.put("analysis", anaContentStr);
            object.put("showis", showIs);
            object.put("submitis", submitIs);
            object.put("receives", getRceivesJSONArray());
            object.put("file", fileArray);
            request.add("data", object.toString());
            request.add("useruuid", MyApplication.getUser().getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                mProgressDialog.show();
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    JSONObject jsonObject = response.get();
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getString("status").equals("100")) {
                            showToast("恭喜你发布成功！");
                            EventBus.getDefault().post(new EventFlag(Constant.EvReleaseSucc));
                            finish();
                        } else {
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
                    Log.e("发布失败", "失败");
                }
            }

            @Override
            public void onFinish(int what) {
                mProgressDialog.dismiss();
            }
        });

    }

    public JSONArray getRceivesJSONArray() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < mReceiverStus.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("uuid", mReceiverStus.get(i).getUuid());
                array.put(object);
            }
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }


    public class MyPhotoAdapter extends CommonAdapter {
        private List<String> mlist;

        public MyPhotoAdapter(Context context, int layoutId, List datas) {
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

    public class MyVoiceAdapter extends CommonAdapter {
        private List<LocationFile> mList;

        public MyVoiceAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
            this.mList = datas;
        }

        @Override
        protected void convert(final ViewHolder holder, final Object itemO, final int position) {
            TextView timeTxt = holder.getView(R.id.iav_tv_voicetime1);
            final RecordModel item = (RecordModel) itemO;
            timeTxt.setText(item.times <= 0 ? 1 + "''" : item.times + "''");


            //更改录音条长度
            ImageView voiceLine = holder.getView(R.id.iclv_iv_voiceLine);
            RelativeLayout.LayoutParams ps = (RelativeLayout.LayoutParams) voiceLine.getLayoutParams();
            ps.width = VoiceLineUtils.getVoiceLineWight(getContext(), item.times);
            voiceLine.setLayoutParams(ps); //更改语音长条长度
            //开始设置监听
            //定义一个参数，确定当前是否正在播放，
            voiceLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取语音信号栏，开始播放动画
                    LinearLayout voiceLine = holder.getView(R.id.iclv_ll_singer);
                    animationDrawable = (AnimationDrawable) voiceLine.getBackground();
                    mAnimationDrawables.add(animationDrawable);//add list
                    resetAnim();
                    animationDrawable.start();
                    //开始实质播放
                    String filePath;
                    if (LHFileUtils.isServiceFilesWithHttp(item.path)) {
                        filePath = ConnectUrl.FILE_PATH + item.path;
                    } else {
                        filePath = item.path;
                    }

                    // FIXME: 2016/12/9 需要语音暂停播放，请复制以下代码片段
                    //****************
                    //判断是否需要停止播放
                    if (pos == position) {
                        if (isPlaying) {
                            isPlaying = false;
                            MediaManager.release();
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(0);//reset
                            return;
                        } else {
                            isPlaying = true;
                        }
                    }
                    //记录当前位置正在播放。
                    pos = position;
                    isPlaying = true;
                    //******************


                    // 播放音频
                    MediaManager.release();//重置
                    MediaManager.playSound(filePath,
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    animationDrawable.stop();
                                    //// FIXME: 2016/11/24 引用语音的界面，注意添加这段代码。
                                    animationDrawable.selectDrawable(0);//reset
                                    //播放完毕，当前播放索引置为-1
                                    pos = -1;
                                }
                            });


                }
            });

            final MorePopupWindow morePopupWindow = new MorePopupWindow(ReleaseHomeWorkActivity.this, R.layout.popup_voice_del, DensityUtil.dip2px(getContext(), 60), DensityUtil.dip2px(getContext(), 30));
            final LinearLayout delBtn = (LinearLayout) morePopupWindow.getLayoutView().findViewById(R.id.pvd_ll_voicedel);
            voiceLine.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    morePopupWindow.showPopupUp(v, 0, DensityUtil.dip2px(getContext(), -8));
                    delBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //首先隐藏语音条栏，然后判断是否在播放，最后在删除数据
                            if (item.isPlaying) {
                                MediaManager.release();
                            }
                            morePopupWindow.dismissPopup();
                            mList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    return false;
                }
            });
        }


    }

    private void resetAnim() {
        for (AnimationDrawable animationDrawable : mAnimationDrawables) {
            animationDrawable.stop();
            //// TODO: 2016/11/24 引用语音的界面，注意添加这段代码。
            //这一句话可以重置动画的第一帧。
            animationDrawable.selectDrawable(0);
        }
    }


    /**
     * Office文件
     */
    public class MyOfficeFileAdapter extends CommonAdapter {
        private List<LocationFile> mList;

        public MyOfficeFileAdapter(Context context, int layoutId, List datas) {
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


    @Override
    protected void onStop() {
        super.onStop();
        resetAnim();
        //该方法是停止语音播放。
        MediaManager.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止动画播放
        resetAnim();
        //该方法是停止语音播放。
        MediaManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止动画播放
        resetAnim();
        //该方法是停止语音播放。
        MediaManager.release();
    }
}
