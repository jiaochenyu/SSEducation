package com.ss.education.ui.fragment.homework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseFragment;
import com.ss.education.base.ConnectUrl;
import com.ss.education.entity.HomeworkFeedback;
import com.ss.education.ui.activity.classes.homework.TeacherSeeHwFeedbackActivity;
import com.ss.education.utils.ImageUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JCY on 2017/11/9.
 * 说明：
 */

public class HomeworkCompletionFragment extends BaseFragment {
    public static final String ARGS_LIST = "args_list";
    public static final String ARGS_PAGE = "args_page";
    public static final String ARGS_FLAG = "args_flag";

    @Bind(R.id.listview)
    ListView mListview;
    @Bind(R.id.null_bg)
    RelativeLayout mNullBg;

    private View mView;
    private List<HomeworkFeedback> mList;
    private String flag = "F";  // 该作业  是否设置了学生需要提交作业。
    private int page = 0;

    private MyAdapter mAdapter;

    public static HomeworkCompletionFragment newInstance(List<HomeworkFeedback> list, String flag, int p) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_LIST, (Serializable) list);
        args.putInt(ARGS_PAGE, p);
        args.putString(ARGS_FLAG, flag);
        HomeworkCompletionFragment fragment = new HomeworkCompletionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = (List<HomeworkFeedback>) getArguments().getSerializable(ARGS_LIST);
        flag = getArguments().getString(ARGS_FLAG);
        page = getArguments().getInt(ARGS_PAGE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_homework_completion, null);
        ButterKnife.bind(this, mView);
        initData();
        initListener();
        return mView;

    }


    private void initData() {
        mAdapter = new MyAdapter(getActivity(), R.layout.item_class_student, mList);
        mListview.setAdapter(mAdapter);
        if (adjustList(mList)) {
            mListview.setVisibility(View.VISIBLE);
            mNullBg.setVisibility(View.GONE);
        } else {
            mListview.setVisibility(View.GONE);
            mNullBg.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("点击了" + position);
                if (flag.equals("T")) {
                    if (page == 0) {
                        Bundle b = new Bundle();
                        b.putSerializable("data", mList.get(position));
                        go(TeacherSeeHwFeedbackActivity.class,b);
                    }

                } else {
                    showToast("没有布置作业！");
                }
            }
        });

    }


    class MyAdapter extends CommonAdapter {

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object item, int position) {
            ImageView icon = holder.getView(R.id.icon);
            TextView name = holder.getView(R.id.name);
            TextView content = holder.getView(R.id.content);

            try {
                name.setText("姓名：" + mList.get(position).getStudent().getRealname());
                content.setText("学号：" + mList.get(position).getStudent().getXjh());

            } catch (Exception e) {
                e.printStackTrace();
            }
            int p = position % 10;
            switch (p) {
                case 1:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header1);
                    break;
                case 2:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header2);
                    break;
                case 3:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header3);
                    break;
                case 4:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header4);
                    break;
                case 5:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header5);
                    break;
                case 6:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header6);
                    break;
                case 7:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header7);
                    break;
                case 8:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header8);
                    break;
                case 9:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header9);
                    break;
                case 0:
                    ImageUtils.setCircleDefImage(icon, ConnectUrl.PICURL + mList.get(position).getStudent().getImgpath(), R.drawable.icon_header10);
                    break;
            }


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
