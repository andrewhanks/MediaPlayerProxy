package com.bedpotato.musicplayerproxy;

import android.app.Application;
import android.content.Context;

import com.bedpotato.musicplayerproxy.utils.LogcatHelper;

/**
 * Application
 */
public class App extends Application {

	public static final String TAG = "MusicPlayerTest";

	public static Context mContext; // 应用全局context

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		LogcatHelper.getInstance(this).start();
	}
}
