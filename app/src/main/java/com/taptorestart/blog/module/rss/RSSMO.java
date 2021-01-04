package com.taptorestart.blog.module.rss;

import com.taptorestart.blog.module.SettingMO;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RSSMO {

	private static RSSMO instance = new RSSMO();

	public static RSSMO getInstance() {
		return instance;
	}

	Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(SettingMO.URL_HOME)
			.addConverterFactory(ScalarsConverterFactory.create())
			.build();

	RSSService service = retrofit.create(RSSService.class);

	public RSSService getService() {
		return service;
	}
}
