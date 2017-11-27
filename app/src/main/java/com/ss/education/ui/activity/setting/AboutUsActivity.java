package com.ss.education.ui.activity.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.ss.education.R;
import com.ss.education.base.BaseActivity;
import com.ss.education.base.ConnectUrl;
import com.ss.education.base.Constant;
import com.ss.education.listener.PayResult;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    private static final int SDK_PAY_FLAG = 3;
    @BindView(R.id.title)
    TextView mTitle;
    private RequestQueue mQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mTitle.setText("关于我们");

    }


    @OnClick({R.id.back, R.id.pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.pay:
                httpAlipay();
                break;
        }
    }

    private void httpAlipay() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(ConnectUrl.PAY_ALIPAY, RequestMethod.POST);
//        request.add("order_sn", orderSnStr);
//        request.add("order_amount", 0.01);
//        request.add("order_amount", DecimalUtils.decimalFormat(totalPrcie));
        mQueue.add(Constant.REQUEST_WHAT, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {
                showLoading(false);
            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == Constant.REQUEST_WHAT) {
                    String resultCode = null;
                    try {
                        JSONObject json = response.get();
                        String orderInfo = json.getString("object");
                        Log.e("ali", orderInfo);
                        goAlipay(orderInfo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {
                hideLoading();
            }

            @Override
            public void onFinish(int what) {
                hideLoading();

            }
        });
    }

    private void goAlipay(final String orderInfo) {

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(AboutUsActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.d("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.d("alipay", resultInfo);
                    Log.d("alipaystatus", resultStatus);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showToast("支付成功,谢谢您的支持");

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("您取消了支付");
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };
}
