package com.bedpotato.musicplayerproxy;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import com.bedpotato.musicplayerproxy.utils.MediaPlayerProxy;

public class Player implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnPreparedListener {
	public MediaPlayer mediaPlayer;
	private SeekBar skbProgress;

	private Timer mTimer = new Timer();

	MediaPlayerProxy proxy;

	private boolean USE_PROXY = true;

	public Player(SeekBar skbProgress) {
		this.skbProgress = skbProgress;

		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}

		mTimer.schedule(mTimerTask, 0, 1000);

		proxy = new MediaPlayerProxy();
		proxy.init();
		proxy.start();
	}

	/*******************************************************
	 * 通过定时器和Handler来更新进度条
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	@SuppressLint("HandlerLeak")
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
			}
		};
	};

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String url) {

		if (USE_PROXY) {
			startProxy();
			url = proxy.getProxyURL(url);
		}

		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			long p = System.currentTimeMillis();
			Log.e("P", String.valueOf(p));
			mediaPlayer.prepare();
			long s = System.currentTimeMillis();
			Log.e("S", String.valueOf(s) + " " + (p - s));
			mediaPlayer.start();
			long x = System.currentTimeMillis();
			Log.e("X", String.valueOf(x) + " " + (x - s));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	/**
	 * 通过onPrepared播放 
	 */
	public void onPrepared(MediaPlayer arg0) {
		Log.e("mediaPlayer", "onPrepared");
		arg0.start();

	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.e("mediaPlayer", "onCompletion");
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		// Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
	}

	private void startProxy() {
		if (proxy == null) {
			proxy = new MediaPlayerProxy();
			proxy.init();
			proxy.start();
		}
	}
}
