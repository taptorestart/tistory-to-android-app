package com.taptorestart.blog.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;
import com.taptorestart.blog.module.noti.NotiMO;
import com.taptorestart.blog.module.setting.PreferenceKeyMO;
import com.taptorestart.blog.module.setting.PreferenceMO;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        Logger.d("onReceive");
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        boolean isNotiOn = PreferenceMO.getPreferenceBoolean(ctx, PreferenceKeyMO.NOTI_ON, true);
        if(isNotiOn){
            NotiMO.setAlarmForWorkManager(ctx);
            NotiMO.cancelWorkManager(ctx);
            NotiMO.setWorkManager(ctx);
        }
    }
}
