package com.bedpotato.musicplayerproxy;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import com.bedp.musicplayerproxy.R;


public class PlayerActivity extends Activity {

	private Button btnPlayUrl;
	private Button btnCache;
	private SeekBar skbProgress;
	private Player player;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		setContentView(R.layout.activity_player);

		this.setTitle("MediaPlayerTest--By BedPotato");

		btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
		btnPlayUrl.setOnClickListener(new ClickEvent());

		btnCache = (Button) this.findViewById(R.id.btnCache);
		btnCache.setOnClickListener(new ClickEvent());
		
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

		player = new Player(skbProgress);
	}

	class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			String url = "http://lkfm-audio.b0.upaiyun.com/audio/综艺娱乐/娱乐节目/娱乐猛回头/神剧<还珠格格>15年捧出3天后.mp3";
			final String urlEn = urlEncode(url);
			if (arg0 == btnPlayUrl) {
				player.playUrl(url);
			}else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PreLoad load = new PreLoad(urlEn);
						load.download(300*1000);
					}
				}).start();
			}
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}
	}

	/**
	 * URL编码
	 * 
	 * @param url
	 * @return
	 */
	public static String urlEncode(String url) {
		try {
			url = java.net.URLEncoder.encode(url, "UTF-8");
			url = url.replaceAll("%2F", "/");
			url = url.replaceAll("%3A", ":");
			url = url.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
}
