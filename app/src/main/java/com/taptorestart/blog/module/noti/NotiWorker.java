package com.taptorestart.blog.module.noti;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.orhanobut.logger.Logger;
import com.taptorestart.blog.MainActivity;
import com.taptorestart.blog.R;
import com.taptorestart.blog.module.SettingMO;
import com.taptorestart.blog.module.rss.RSSMO;
import com.taptorestart.blog.module.setting.PreferenceKeyMO;
import com.taptorestart.blog.module.setting.PreferenceMO;
import com.taptorestart.blog.module.xml.XMLParserRSSMO;
import com.taptorestart.blog.model.RSSItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiWorker extends Worker {

    private Context ctx;
    public NotiWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        ctx = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
        getRSS();
        return Result.success();
    }

    private void getRSS(){
        Logger.d("getRSS");
        Call<String> resWord = RSSMO.getInstance().getService().getRSS();
        resWord.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String xml = response.body();
                assert xml != null;
                xml = xml.replaceAll("[\\n\\r\\t]+", "");
                ArrayList<RSSItem> rssItemList = XMLParserRSSMO.rssToItem(xml);
                Logger.d("rssItem title:" + rssItemList.get(0).title);
                Logger.d("rssItem link:" + rssItemList.get(0).link);
                Logger.d("rssItem pubDate:" + rssItemList.get(0).pubDate);
                Logger.d("rssItem guid:" + rssItemList.get(0).guid);
                String lastArticleGUID = PreferenceMO.getPreferenceString(ctx, PreferenceKeyMO.LAST_ARTICLE_GUID, "");
                PreferenceMO.setPreference(ctx, PreferenceKeyMO.LAST_ARTICLE_GUID, rssItemList.get(0).guid);
                if(!lastArticleGUID.equals(rssItemList.get(0).guid)){
                    RSSItem lastItem = rssItemList.get(0);
                    noti(lastItem);
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Logger.d(t.getMessage());
            }
        });
    }

    private void noti(RSSItem lastItem){
        NotificationManager notiAlarm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    SettingMO.CHANNEL_ID, SettingMO.CHANNEL_NAME, importance);
            mChannel.setSound(null, null);
            assert notiAlarm != null;
            notiAlarm.createNotificationChannel(mChannel);
        }

        Intent intent = null;
        intent = new Intent(ctx, MainActivity.class);
        intent.putExtra("new_article", true);
        intent.putExtra("article_guid", lastItem.guid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent content = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notiView = new RemoteViews(ctx.getPackageName(), R.layout.noti);
        notiView.setTextViewText(R.id.txt_noti, lastItem.title);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, SettingMO.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(null)
                .setContent(notiView);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        assert notiAlarm != null;
        notiAlarm.notify(SettingMO.NOTI_ID, mBuilder.build());
    }

    @Override
    public void onStopped() {
    }
}