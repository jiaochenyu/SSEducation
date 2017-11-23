package com.ss.education.utils;

import android.util.Log;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.PrivateConversationProvider;

/**
 * Created by timor.fan on 2017/2/21.
 * *项目名：CZF
 * 类描述：
 */

@ConversationProviderTag(
        conversationType = "private",
        portraitPosition = 1
)

public class MyPrivateConversationProvider extends PrivateConversationProvider {


    @Override
    public String getTitle(String userId) {
        Log.e("融云的用户Provider","userId" + userId);
        String name;
        if(RongUserInfoManager.getInstance().getUserInfo(userId) == null) {
            name = "null";
        } else {
            name = RongUserInfoManager.getInstance().getUserInfo(userId).getName();
        }

        return name;
    }
}
