package com.taptorestart.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.taptorestart.blog.module.SettingMO;
import com.taptorestart.blog.module.noti.NotiMO;
import com.taptorestart.blog.module.rss.RSSMO;
import com.taptorestart.blog.module.setting.PreferenceKeyMO;
import com.taptorestart.blog.module.setting.PreferenceMO;
import com.taptorestart.blog.module.xml.XMLParserRSSMO;
import com.taptorestart.blog.model.RSSItem;
import com.taptorestart.blog.ui.ObservableWebView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ObservableWebView web;
    private Context ctx;
    private boolean isNewArticle = false;
    private String articleGUID = "";
    private String currentUrl = SettingMO.URL_HOME;

    private final String INTENT_PROTOCOL_START = "intent:";
    private final String INTENT_PROTOCOL_INTENT = "#Intent;";
    private final String INTENT_PROTOCOL_END = ";end;";
    private final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
    private final String INTENT_COUPANG = "coupang:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

        ctx = this;
        Intent intent = getIntent();
        isNewArticle = intent.getBooleanExtra("new_article", false);
        articleGUID = intent.getStringExtra("article_guid");

        initView();

        boolean isNotiOn = PreferenceMO.getPreferenceBoolean(ctx, PreferenceKeyMO.NOTI_ON, true);
        if(isNotiOn){
            NotiMO.setAlarmForWorkManager(ctx);
            NotiMO.cancelWorkManager(ctx);
            NotiMO.setWorkManager(ctx);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(){
        ImageButton btnMenu = (ImageButton)findViewById(R.id.btn_menu);
        ImageButton btnHome = (ImageButton)findViewById(R.id.btn_home);
        ImageButton btnShare = (ImageButton)findViewById(R.id.btn_share);
        ImageButton btnSearch = (ImageButton)findViewById(R.id.btn_search);
        btnMenu.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        web = (ObservableWebView)findViewById(R.id.web);
        WebSettings webSet = web.getSettings();
        webSet.setAllowFileAccess(true);
        webSet.setSaveFormData(true);
        webSet.setJavaScriptCanOpenWindowsAutomatically(true);
        webSet.setSupportZoom(true);
        webSet.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSet.setUseWideViewPort(true);
        webSet.setBuiltInZoomControls(true);
        webSet.setDomStorageEnabled(true);
        webSet.setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        web.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.d("url:" + url);
                if(url.startsWith("http")){
                    if(url.contains(SettingMO.URL_PLAYSTORE)){
                        try {
                            url = SettingMO.URL_MARKET;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
                            return false;
                        }
                    }else{
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                }else{
                    if(url.startsWith(INTENT_PROTOCOL_START)){
                        try {
                            final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                            final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                            if (customUrlEndIndex < 0) {
                                return false;
                            } else {
                                final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                                try{
                                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                                }catch(ActivityNotFoundException e) {
                                    final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                                    final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                                    final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                                }
                                return true;
                            }
                        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
                            return false;
                        }
                    }else if(url.startsWith(INTENT_COUPANG)){
                        try{
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }catch(ActivityNotFoundException e) {
                            url = url.replace("coupang://product?pId=", "https://www.coupang.com/vp/products/");
                            url = url.replace("&", "?");
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        }
                        return true;
                    }else{
                        try{
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }catch(ActivityNotFoundException e) {
                            return false;
                        }
                        return true;
                    }
                }
                return false;
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                currentUrl = url;
                super.onPageStarted(view, url, favicon);
            }
            public void onPageFinished(WebView view, String url) {
            }
        });
        if(isNewArticle){
            web.clearCache(true);
            if(!"".equals(articleGUID)){
                PreferenceMO.setPreference(ctx, PreferenceKeyMO.LAST_ARTICLE_GUID, articleGUID);
                web.loadUrl(articleGUID);
            }
        }else{
            web.loadUrl(SettingMO.URL_HOME);
            getRSS();
        }
    }

    private void getRSS(){
        Call<String> resWord = RSSMO.getInstance().getService().getRSS();
        resWord.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String xml = response.body();
                assert xml != null;
                xml = xml.replaceAll("[\\n\\r\\t]+", "");
                ArrayList<RSSItem> rssItemList = XMLParserRSSMO.rssToItem(xml);
                String lastArticleGUID = PreferenceMO.getPreferenceString(ctx, PreferenceKeyMO.LAST_ARTICLE_GUID, "");
                PreferenceMO.setPreference(ctx, PreferenceKeyMO.LAST_ARTICLE_GUID, rssItemList.get(0).guid);
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Logger.d(t.getMessage());
            }
        });
    }

    public static void popupSearch(final WebView webview, final Activity act){
        final PopupWindow popup = new PopupWindow(act);
        LinearLayout viewGroup = (LinearLayout)act.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_search, viewGroup);

        final EditText edt = (EditText)layout.findViewById(R.id.edt);
        TextView btnOK = (TextView)layout.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlSearch = SettingMO.URL_SEARCH + edt.getText().toString().trim();
                webview.loadUrl(urlSearch);
                popup.dismiss();
            }
        });
        TextView btnCancel = (TextView)layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup.setContentView(layout);
        popup.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    //뒤로 가기 버튼 처리.
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (web != null && (keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()){
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_menu:
                web.loadUrl("javascript:document.getElementById('mobile-menu').click();");
                break;

            case R.id.btn_home:
                web.clearCache(true);
                getRSS();
                web.loadUrl(SettingMO.URL_HOME);
                break;

            case R.id.btn_search:
                popupSearch(web, this);
                break;

            case R.id.btn_share:
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                String shareBody = currentUrl;
                intentShare.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intentShare, getString(R.string.app_name)));
                break;
        }
    }

}