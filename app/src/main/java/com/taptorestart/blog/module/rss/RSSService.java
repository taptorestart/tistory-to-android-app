package com.taptorestart.blog.module.rss;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RSSService {

	@GET("rss")
	Call<String> getRSS();

}
