package com.taptorestart.blog.module;

import com.taptorestart.blog.BuildConfig;

public class SettingMO {
	public static String URL_HOME = "https://taptorestart.tistory.com";
	public static String URL_SEARCH = URL_HOME + "/search/";
	public static String URL_PLAYSTORE = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;;
	public static String URL_MARKET = "market://details?id=" + BuildConfig.APPLICATION_ID;

	public static final String WORK_NAME = BuildConfig.APPLICATION_ID;
	public static final String CHANNEL_ID = BuildConfig.APPLICATION_ID;
	public static final String CHANNEL_NAME = BuildConfig.APPLICATION_ID;
	public static final int NOTI_ID = 1;
}
