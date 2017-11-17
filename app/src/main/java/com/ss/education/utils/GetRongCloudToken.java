package com.ss.education.utils;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import java.security.MessageDigest;

/**
 * Created by JCY on 2017/10/17.
 * 说明：
 */

public class GetRongCloudToken {
    private static RequestQueue mQueue = NoHttp.newRequestQueue();



    //SHA1加密//http://www.rongcloud.cn/docs/server.html#通用_API_接口签名规则
    public static String sha1(String data) {
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(data.getBytes());
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
        } catch (Exception e) {

        }
        return buf.toString();
    }

}
