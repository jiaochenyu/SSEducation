package com.ss.education.base;

/**
 * Created by JCY on 2017/9/18.
 * 说明：
 */

public class ConnectUrl {

    public static String IMAGEURL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505735621694&di=b54dce7baa41bb064fe5da059bb18cbb&imgtype=0&src=http%3A%2F%2Fimg.eastcy.com%2Fimages%2Fueditor%2Fupload%2F20170208%2F61471486546856245.jpg";
    public static String IMAGEURL2 = "https://assets.entrepreneur.com/content/3x2/1300/20160927052319-shutterstock-261534164.jpeg?width=750&crop=16:9";
    public static String IMAGEURL_HEADER = "http://img0.imgtn.bdimg.com/it/u=3806841454,2251366770&fm=27&gp=0.jpg";


//    public static String HTTPURL = "http://192.168.0.114:8080/appservice";  //本地
//    public static String PICURL = "http://192.168.0.114:8080/appservice/"; // 本地 图片
//    public static String FILE_PATH = "http://192.168.0.114:8080/appservice/"; // 本地 文件


    public static String HTTPURL = "http://101.89.139.185:8082/appservice";  //服务器
    public static String PICURL = "http://101.89.139.185:8082/appservice/"; //服务器
    public static String FILE_PATH = "http://101.89.139.185:8082/appservice/"; // 服务器 文件

    public static String LOGIN = HTTPURL + "/user-checkUserAndPwd.cyc";
    public static String REGISTER = HTTPURL + "/app-toregister.cyc";
    public static String SIGN_OUT = HTTPURL + "/user-userLogout.cyc";  //用户退出登录

    public static String RONGCLOUD = "https://api.cn.ronghub.com/user/getToken.json";

    public static String GET_SECTION_LIST = HTTPURL + "/home-getAllSections.cyc";//获取章节列表
    public static String GET_EXAM_CONTENT = HTTPURL + "/home-getRandomQuiz.cyc";//获取题目
    public static String SAVE_EXAM_RECORD = HTTPURL + "/app-addZTRecord.cyc";//保存做题记录
    public static String GET_MY_STUDENT_LIST = HTTPURL + "/app-loadMyStudents.cyc";//老师角色获取我的学生列表
    public static String GET_MY_EXAM_GROUP_RECORD_LIST = HTTPURL + "/app-loadZtmain.cyc";//获取做题组记录
    public static String GET_EXAM_RECORD_LIST = HTTPURL + "/app-loadZtrecord.cyc";//获取做题记录 全部记录
    public static String GET_EXAM_ERROR_RECORD_LIST = HTTPURL + "/app-LoadAppCtrecord.cyc";//获取错题记录
    public static String GET_EXAM_ERROR_RECORD_SECTION_LIST = HTTPURL + "/app-loadCtrecordSection.cyc";//获取错题记录的章节
    public static String GET_CLASS_LIST = HTTPURL + "/app-loadAppClass.cyc";//老师/学生获取班级列表
    public static String ADD_CLASS = HTTPURL + "/app-addAppClass.cyc";//老师/创建班级
    public static String GET_CLASS_DETAIL = HTTPURL + "/app-findAppClass.cyc";//查询班级
    public static String GET_CLASS_STUDENT_LIST = HTTPURL + "/app-loadClassStudent.cyc";//查询班级学生
    public static String STUDENT_JOIN_CLASS = HTTPURL + "/app-addClassStudent.cyc";//申请加入班级
    public static String TEACHER_SHENHE_CLASS = HTTPURL + "/app-updateClassStudentState.cyc";// 老师同意/拒绝加入班级
    public static String UPDATE_CLASS = HTTPURL + "/app-updateAppClass.cyc";// 修改班级详情
    public static String DELETE_CLASS_STUDENT = HTTPURL + "/app-deleteClassStudent.cyc";// 删除班级学生
    public static String QUIT_CLASS_STUDENT = HTTPURL + "/app-studentQuitClass.cyc";// 学生申请退出班级
    public static String UPLOAD_PINGYU = HTTPURL + "/app-updateAppZtmain.cyc";// 评语
    public static String UPDATE_USER_INFO = HTTPURL + "/app-updateAppUser.cyc";// 评语
    public static String SEARCH_PARENT_OR_STUDNET = HTTPURL + "/app-findUser.cyc";// 家长查找我的学生，学生找家长
    public static String MY_PARENT_OR_STUDNET = HTTPURL + "/app-loadMyJiazhangStudent.cyc";// 家长查找我的学生，学生找家长
    public static String BIND_PARENT_OR_STUDNET_Y_N = HTTPURL + "/app-updateAppJiazhangStudent.cyc";// 家长学生 同意/拒绝绑定申请
    public static String APPLY_BIND_STU_PAR = HTTPURL + "/app-addJiazhangStudent.cyc";// 家长/学生绑定关系申请

    public static String FIND_ALL_USER_RONG_CLOUD = HTTPURL + "/app-findAllUser.cyc";// 获取全部用户信息缓存到融云中


    public static String UPLOAD_FILE = HTTPURL + "/app-uploadUserImg.cyc";// 上传头像
    public static String UPLOAD_COMMON_FILE = HTTPURL + "/app-uploadFiles.cyc";// 文件上传都放到这里
    public static String UPLOAD_CLASS_IAMGE_HEADER = HTTPURL + "/app-uploadClassImg.cyc";// 上传班级头像


    public static String UPDATE_APK = HTTPURL + "/app-inspectVersion.cyc";// 获取最新apk


    /**
     * 班级分组接口
     */
    public static String GROUP_DELETE = HTTPURL + "/app-deleteClassGroup.cyc";// 删除分组
    public static String GROUP_ADD = HTTPURL + "/app-addClassGroup.cyc";// 删除分组
    public static String GROUP_UPDATE = HTTPURL + "/app-updateClassGroup.cyc";// 修改分组信息
    public static String GROUP_SAVE_SORT = HTTPURL + "/app-saveClassGroupSeq.cyc";// 保存分组排序
    public static String GROUP_MOVE_STUDENT = HTTPURL + "/app-updateUserClassGroup.cyc";// 分组内成员调动


    /**
     * 作业
     */

    public static String RELEASE_HOME_WORK = HTTPURL + "/app-addAppWork.cyc";// 发布作业
    public static String UPDATE_HOME_WORK = HTTPURL + "/app-updateAppWork.cyc";// 修改作业
    public static String GET_HOME_WORK_LIST = HTTPURL + "/app-loadAppWork.cyc";// 获取作业列表
    public static String GET_HOME_WORK_INFO = HTTPURL + "/app-findAppWorkOne.cyc";// 获取作业详情
    public static String SUBMIT_HOME_WORK = HTTPURL + "/app-updateAppWorkFeedBack.cyc";// 学生完成作业(第一个data)/老师写评语(第二个data)
    public static String GET_HOME_WORK_FEEDBACK_TEACHER = HTTPURL + "/app-loadAppWorkFeedbackT.cyc";// 老师获取作业反馈
    public static String GET_HOME_WORK_FEEDBACK_STUDENT = HTTPURL + "/app-loadAppWorkFeedbackS.cyc";// 学生获取作业反馈


    /**
     * 我的模块
     */
    public static String INVITATION_TEACHER_CODE= HTTPURL + "/app-buildInvitationCode.cyc";// 老师生成教师邀请码

}
