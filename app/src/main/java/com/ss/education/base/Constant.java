package com.ss.education.base;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class Constant {
    public static String RONGCLOUDE = "3argexb630loe";
    public static int REQUEST_WHAT = 0x100F; //网络请求what值
    public static int LIMIT_PIC_NUM = 8; //图片选择器 限制图片

    public static int ONE = 1; //
    public static int TWO = 2; //
    public static String RECORD_PAGE_FLAG_RECORD = "record";  //做题记录
    public static String PAGE_FLAG_ERROR = "finish_error";  //考试结束 查看错题
    public static String SHUXUE = "数学";  //考试结束 查看错题
    public static String YINGYU = "英语";  //考试结束 查看错题
    public static String YUWEN = "语文";  //考试结束 查看错题
    public static String WULI = "物理";  //考试结束 查看错题
    public static String HUAXUE = "化学";  //考试结束 查看错题
    public static String LISHI = "历史";  //考试结束 查看错题
    public static String ZHENGZHI = "政治";  //考试结束 查看错题
    public static String DILI = "地理";  //考试结束 查看错题
    public static String SHENGWU = "生物";  //考试结束 查看错题
    //EventBus
    public static String EvUpadeClassS = "updataClassStudent"; //老师审核更新上一个界面的数据
    public static String EvAddClass = "addClass"; //创建班级
    public static String EvUpdateClassDetail = "editClass"; //修改班级详情
    public static String EvStudentOutClass = "outClass"; //学生退出班级
    public static String EvUpdateUserInfo = "updateUserInfo"; //修改个人信息
    public static String EvJoinClass = "joinClass"; //加入班级
    public static String EvStudentAgreeP = "studentAgreeP"; //学生同意家长申请 绑定
    public static String EvParentAgreeS = "parentAgreeS"; //家长同意学生申请 绑定
    public static String EvBindParentStu = "bindStuAndPar"; //家长和学生 绑定
    public static String EvUpdateHeader = "updateHeader"; //更新头像
    public static String EvUpdateClassGroup = "updateClassGroup"; //更新分组信息
    public static String EvFindFileComplete = "findFileFinish"; //遍历文件结束
    public static String EvFinishOfficeSelectFile = "finishSelectOfficeFile"; //选择文件 将文件信息返回给发布Actiity
    public static String EvSelectReceive_homework = "receive_stdent"; //发布作业接收人
    public static String EvSelectAll_Receive = "receive_stdent_all"; //发布作业 选择了全部接收人
    public static String EvReleaseSucc = "releaseSuccess"; //发布作业成功
    public static String EvMoveStudentFromGroup = "moveStudent"; //分组内成员调动成功
    public static String EvFinishEvaluate = "evFinishEvaluate"; //评价成功
    public static String EvFinishShenhe = "evFinishShenhe"; //获取审核列表成功


    public static String LOGOUT = "LOGOUT"; //极光推送  下线通知
    public static String TEACHER_CLASS_SHENHE = "TEACHER_CLASS_SHENHE"; //极光推送  老师班级里的审核操作  包括 同意学生加入班级， 同意/拒绝 学生退出班级
    public static String JZORXS_BANGDING_SHENHE = "JZORXS_BANGDING_SHENHE"; //极光推送  学生审核家长 家长审核学生
    public static String STDENT_CLASS_XIANGQING = "STDENT_CLASS_XIANGQING"; //极光推送  学生 您的加入班级申请已经通过
    public static String STDENT_CLASS_LIEBIAO = "STDENT_CLASS_LIEBIAO"; //极光推送  学生 学生跳转到班级列表
    public static String STUDENT_WORK_LIEBIAO = "STUDENT_WORK_LIEBIAO"; //极光推送  学生 学生跳转到作业列表
    public static String STDENT_CLASS_WORK_JIEXI = "STDENT_CLASS_WORK_JIEXI"; //极光推送 老师将作业解析修改成显示，通知学生 作业解析有显示


    public static List<String> XUEKELSIT = Arrays.asList(
            new String[]{"全部",
                    "数学",
                    "语文",
                    "英语",
                    "物理",
                    "化学",
                    "政治",
                    "历史",
                    "地理",
                    "生物",
            });
    public static List<String> XUEKELSITVALUES = Arrays.asList(
            new String[]{"全部",
                    "数学",
                    "语文",
                    "英语",
                    "物理",
                    "化学",
                    "政治",
                    "历史",
                    "地理",
                    "生物",
            });

    public static class Urls {
        //        外网地址,ll
        public static final String API_URL = "http://61.155.169.45:8081/CzfService/";
        //        //图片链接地址
        public static final String PIC_URL = "http://61.155.169.45:8081/SZCZF/";

//        //内网地址
//        public static final String API_URL = "http://192.168.0.179:8080/CZF/";
////        图片链接地址
//        public static final String PIC_URL = "http://192.168.0.179:8080/CZF/";


        //默认头像地址
        public static final String DEFAULT_AVATAT = "avatar_default.png";
        //版本升级地址
        public static String VERSION_DOWNLOAD = API_URL + "app-release.apk";
        //哦了下载地址
        public static final String OL_DOWNLOAD = API_URL + "officeOnline.apk";
        //知了下载地址
        public static final String ZL_DOWNLOAD = API_URL + "studyOnline.apk";

        /**
         * 客户端文件夹结构：CZF/files/，
         * 下包括video、photo（采用CZF拍照的照片都会保存到该目录下，不会删除）、logs、temp（临时文件夹）。
         * 录音和压缩的图片均属于临时文件，每次重新APP进程，都会清空temp文件夹。
         */
        //客户端存放根路径。
        public static final String CZF_ROOT_URL = "SS/files/";
        //客户端缓存文件路径。下次启动【APP进程】时会删除所有该文件夹下所有文件。不要在该文件夹放需要长久的文件。
        public static final String CZF_TEMP_URL = CZF_ROOT_URL + "temp/";


    }
}
