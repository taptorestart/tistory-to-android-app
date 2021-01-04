package com.taptorestart.blog.module.noti;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.orhanobut.logger.Logger;
import com.taptorestart.blog.act.AlarmReceiver;
import com.taptorestart.blog.module.SettingMO;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotiMO {

	public static void setAlarmForWorkManager(Context ctx){
		Logger.d("setAlarmForWorkManager");
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		if(Build.VERSION.SDK_INT >= 23){
			assert alarmManager != null;
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}else{
			assert alarmManager != null;
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}
	}

	public static void setWorkManager(Context ctx){
		Logger.d("setWorkManager");
		WorkManager.getInstance(ctx).cancelAllWork();
		// 정의할 수 있는 최소 반복 간격은 15분 https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work?hl=ko#java
//		PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(NotiWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.SECONDS).build(); //Test를 위한 코
		PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(NotiWorker.class, 1, TimeUnit.DAYS).build();
		WorkManager.getInstance(ctx).cancelUniqueWork(SettingMO.WORK_NAME);
		WorkManager.getInstance(ctx)
				.enqueueUniquePeriodicWork(SettingMO.WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
	}

	public static void cancelWorkManager(Context ctx){

		WorkManager.getInstance(ctx).cancelAllWork();
	}
}
