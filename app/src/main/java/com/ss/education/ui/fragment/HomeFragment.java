package com.ss.education.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.ss.education.R;
import com.ss.education.base.BaseFragment;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.MyApplication;
import com.ss.education.customview.CustomSwipeRefresh;
import com.ss.education.customview.NoScrollGridView;
import com.ss.education.customview.NoScrollListView;
import com.ss.education.entity.HomePractice;
import com.ss.education.entity.SimulationExam;
import com.ss.education.ui.activity.examination.PracticeActivity;
import com.ss.education.utils.NetworkImageHolderView;
import com.ss.education.utils.OfficeFileUtils;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SimpleDownloadListener;
import com.yanzhenjie.nohttp.download.SyncDownloadExecutor;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class HomeFragment extends BaseFragment implements OnItemClickListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.convenientBanner)
    ConvenientBanner mBanner;
    @Bind(R.id.gridview)
    NoScrollGridView mGridview;
    @Bind(R.id.listview_exam)
    NoScrollListView mListview; // 真题模拟
    @Bind(R.id.layout_swipe_refresh)
    CustomSwipeRefresh mLayoutSwipeRefresh;
    @Bind(R.id.mokuai_txt)
    TextView mMokuaiTxt;
    @Bind(R.id.gongneng_layout)
    LinearLayout mGongnengLaout;
    private View mView;
    private List mPictureList;
    GridviewAdapter mGridviewAdapter;
    List<HomePractice> mPracticeList;

    List<SimulationExam> mExamList; // 真题模拟;
    ExamAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private String filePaths = "";
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
//                    showToast("文件保存到" + filePaths);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //officeFile：本地文档；type：文档MIMEType类型，可以使用文件格式后缀
//                    int index = filePaths.lastIndexOf(".");
//                    String ex = filePaths.substring(index);
//                    intent.setDataAndType(Uri.fromFile(new File(filePaths)), ex);
//                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        startActivity(intent);
//                    } else {
//                        OfficeFileUtils.openFile(getActivity(), filePaths);
//                    }
                    OfficeFileUtils.openFile(getActivity(), filePaths);
                    mProgressDialog.dismiss();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, mView);
        initView();
        initData();
        initBanner();
        initListener();
        return mView;
    }

    private void initView() {
        if (MyApplication.getUser().getPart().equals("J")) {
            mGridview.setVisibility(View.GONE);
            mMokuaiTxt.setText("功能");
            mGongnengLaout.setVisibility(View.VISIBLE);
        }
        mLayoutSwipeRefresh.setEnabled(false);
        initDialog();

    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("正在打开...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }


    private void initData() {
        mPictureList = new ArrayList();
        mPracticeList = new ArrayList<>();

        mExamList = new ArrayList<>();
        testData();
        mAdapter = new ExamAdapter(getActivity(), R.layout.item_home_zhentimoni, mExamList);
        mListview.setAdapter(mAdapter);
        setData();
        mLayoutSwipeRefresh.setColorSchemeResources(R.color.colorTopic);
        mGridviewAdapter = new GridviewAdapter(getActivity(), R.layout.item_home_gridview, mPracticeList);
        mGridview.setAdapter(mGridviewAdapter);
    }

    private void testData() {
        String[] f = {
                "word/2017lsts.docx",
                "word/2017lzts.doc",
                "word/2017wzts.docx",
                "word/2017yyts.docx",
                "word/2017ywts.docx",
        };

        String[] s = {
                "2017全国Ⅰ卷高考理数试题",
                "2017全国Ⅰ卷高考理综试题",
                "2017 全国Ⅰ卷高考文综试题",
                "2017 全国Ⅰ卷高考英语试题",
                "2017 全国Ⅰ卷高考语文试题",
        };
        String[] t = {
                "2017-6-25",
                "2017-6-25",
                "2017-6-25",
                "2017-6-25",
                "2017-6-25",
        };
        for (int i = 0; i < 5; i++) {
            SimulationExam exam = new SimulationExam();
            exam.setFile(f[i]);
            exam.setName(s[i]);
            exam.setTime("2017-6-25");
            mExamList.add(exam);
        }
    }


    private void initBanner() {
        mBanner.setPages(new CBViewHolderCreator() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mPictureList)
                .startTurning(2000)
                .setPageIndicator(new int[]{R.drawable.circle_white, R.drawable.circle_topic})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnPageChangeListener(this).setOnItemClickListener(this);
    }

    private void initListener() {
        mBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mLayoutSwipeRefresh.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mLayoutSwipeRefresh.setEnabled(true);
                        break;
                }
                return false;
            }
        });
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", mPracticeList.get(position).getName());
                go(PracticeActivity.class, bundle);
            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String url = ConnectUrl.FILE_PATH + mExamList.get(position).getFile();
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


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {

    }

    class GridviewAdapter extends CommonAdapter {

        public GridviewAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);

        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            Button mIconBtn = holder.getView(R.id.icon);
            mIconBtn.setBackground(getResources().getDrawable(mPracticeList.get(position).getImageLocal()));
            mIconBtn.setFocusable(false);
            mIconBtn.setClickable(false);
        }
    }

    private void setData() {
        mPictureList.add(ConnectUrl.IMAGEURL);
        mPictureList.add(ConnectUrl.IMAGEURL2);
        mPracticeList.add(new HomePractice("语文", R.drawable.yuwen));
        mPracticeList.add(new HomePractice("物理", R.drawable.shuxue));
        mPracticeList.add(new HomePractice("英语", R.drawable.yingyu));
        mPracticeList.add(new HomePractice("物理", R.drawable.wuli));
        mPracticeList.add(new HomePractice("化学", R.drawable.huaxue));
        mPracticeList.add(new HomePractice("政治", R.drawable.zhengzhi));
        mPracticeList.add(new HomePractice("历史", R.drawable.lishi));
        mPracticeList.add(new HomePractice("地理", R.drawable.dili));
        mPracticeList.add(new HomePractice("生物", R.drawable.shengwu));
    }


    class ExamAdapter extends CommonAdapter {

        public ExamAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            TextView name = holder.getView(R.id.name);
            TextView time = holder.getView(R.id.time);
            name.setText(mExamList.get(position).getName());
            time.setText(mExamList.get(position).getTime());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
