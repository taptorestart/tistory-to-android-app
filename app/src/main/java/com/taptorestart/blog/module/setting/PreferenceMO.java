package com.taptorestart.blog.module.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PreferenceMO {
	
	public static void setPreference(Context ctx, String keyName, String keyValue ){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		SharedPreferences.Editor editor = pref.edit();
		editor.putString( keyName, keyValue );
		editor.apply();
	}
	
	public static void setPreference(Context ctx, String keyName, int keyValue ){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt( keyName, keyValue );
		editor.apply();
	}

	public static void setPreference(Context ctx, String keyName, long keyValue){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(keyName, keyValue);
		editor.apply();
	}

	public static void setPreference(Context ctx, String keyName, float keyValue){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat(keyName, keyValue);
		editor.apply();
	}
	
	public static void setPreference(Context ctx, String keyName, boolean keyValue ){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean( keyName, keyValue );
		editor.apply();
	}
	
	public static String getPreferenceString(Context ctx, String keyName, String defaultValue ){
		String keyValue = "";
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
    	keyValue = pref.getString( keyName, defaultValue );
		return keyValue;
	}

	public static long getPreferenceLong(Context ctx, String keyName, long defaultValue ){
		long keyValue = 1;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		keyValue = pref.getLong( keyName, defaultValue );
		return keyValue;
	}
	
	public static int getPreferenceInt(Context ctx, String keyName, int defaultValue ){
		int keyValue = 1;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
    	keyValue = pref.getInt( keyName, defaultValue );
		return keyValue;
	}

	public static float getPreferenceFloat(Context ctx, String keyName, float defaultValue){
		float keyValue = 1f;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		keyValue = pref.getFloat(keyName, defaultValue);
		return keyValue;
	}

	public static boolean getPreferenceBoolean(Context ctx, String keyName, boolean defaultValue ){
		boolean keyValue = false;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
    	keyValue = pref.getBoolean( keyName, defaultValue );
		return keyValue;
	}
	
}
