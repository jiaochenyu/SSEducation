package com.ss.education.ui.activity.classes;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.base.MyApplication;
import com.ss.education.entity.EventFlag;
import com.ss.education.entity.Student;
import com.ss.education.entity.StudentGroup;
import com.ss.education.ui.activity.examination.ExamRecordActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class StudentDetailActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.s_name)
    TextView mSnameTV;
    @Bind(R.id.xuehao)
    TextView mXuehao;
    @Bind(R.id.student_phone)
    TextView mStudentPhone;
    @Bind(R.id.j_name)
    TextView mJName;
    @Bind(R.id.j_phone)
    TextView mJPhone;
    @Bind(R.id.jiazhang_layout)
    LinearLayout mJiazhangLayout;

    String studentId = "";
    Student mStudent;
    @Bind(R.id.contact_j)
    TextView mContactJ;
    @Bind(R.id.fenzu)
    TextView mFenzuTV;
    private List<StudentGroup> mGroupList;
    private String classId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras != null) {
            studentId = extras.getString("studentId");
            mStudent = (Student) extras.getSerializable("student");
            mGroupList = (List<StudentGroup>) extras.getSerializable("groupList");
            classId = extras.getString("classId", "");

        }
    }

    private void initView() {
        mTitle.setText("学生详情");
        mSnameTV.setText(mStudent.getRealname());
        mXuehao.setText(mStudent.getXjh());
        mStudentPhone.setText(mStudent.getPhone());
        mFenzuTV.setText(mStudent.getGroupname());
        if (!MyApplication.getUser().getPart().equals("S")) {
            if (mStudent.getParent() != null) {
                mJiazhangLayout.setVisibility(View.VISIBLE);
                mContactJ.setVisibility(View.VISIBLE);
                mJName.setText(mStudent.getParent().getRealname());
                mJPhone.setText(mStudent.getParent().getPhone());
            } else {
                mJiazhangLayout.setVisibility(View.GONE);
                mContactJ.setVisibility(View.GONE);

            }
        }
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Subscribe
    public void onEvent(EventFlag eventFlag) {
        if (eventFlag.getFlag().equals(Constant.EvMoveStudentFromGroup)) {
            mFenzuTV.setText((String) eventFlag.getObject());
        }
    }

    @OnClick({R.id.back, R.id.look_exam, R.id.contact_s, R.id.contact_j, R.id.fenzu_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.look_exam:
                Bundle bundle = new Bundle();
                bundle.putString("studentId", studentId);
                bundle.putSerializable("student", mStudent);
                go(ExamRecordActivity.class, bundle);
                break;
            case R.id.contact_s:
                //联系学生
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(StudentDetailActivity.this, mStudent.getUuid(), mStudent.getRealname());
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(mStudent.getUuid(), mStudent.getRealname(), Uri.parse(ConnectUrl.FILE_PATH+mStudent.getImgpath())));
                }
                break;
            case R.id.contact_j:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(StudentDetailActivity.this, mStudent.getParent().getUuid(), mStudent.getParent().getRealname());
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(mStudent.getParent().getUuid(), mStudent.getParent().getRealname(), Uri.parse(ConnectUrl.FILE_PATH+mStudent.getParent().getImgpath())));
                }
                //联系家长
                break;
            case R.id.fenzu_layout:
                Bundle b = new Bundle();
                b.putSerializable("groupList", (Serializable) mGroupList);
                b.putSerializable("student", mStudent);
                b.putString("classId", classId);
                go(GroupListActivity.class, b);
                break;
        }
    }
}
