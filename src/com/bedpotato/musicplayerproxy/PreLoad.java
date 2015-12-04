package com.bedpotato.musicplayerproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.bedpotato.musicplayerproxy.db.CacheFileInfoDao;
import com.bedpotato.musicplayerproxy.utils.HttpUtils;
import com.bedpotato.musicplayerproxy.utils.ProxyFileUtils;
import com.bedpotato.musicplayerproxy.utils.RequestDealThread;

public class PreLoad extends Thread{

	private static final String LOG_TAG = RequestDealThread.class.getSimpleName();

	CacheFileInfoDao cacheDao = CacheFileInfoDao.getInstance();
	ProxyFileUtils fileUtils;
	URI uri;

	public PreLoad(String url) {
		uri = URI.create(url);
		fileUtils = ProxyFileUtils.getInstance(uri, false);
	}

	public boolean download(int size) {
		try {
			Log.i(LOG_TAG, "缓存开始");
			//
			if (!fileUtils.isEnable()) {
				return false;
			}
			// 得到文件长度，如果超过缓冲给定长度，则返回
			int fileLength = fileUtils.getLength();
			if (fileLength >= size) {
				return true;
			}
			// 如果已经下载完成，返回
			System.out.println(fileUtils.getLength() + " " + cacheDao.getFileSize(fileUtils.getFileName()));
			if (fileUtils.getLength() == cacheDao.getFileSize(fileUtils.getFileName())) {
				return true;
			}
			// 从之前的位置缓存
			HttpResponse response = HttpUtils.send(new HttpGet(uri));
			if (response == null) {
				return false;
			}
			int contentLength = Integer.valueOf(response.getHeaders(Constants.CONTENT_LENGTH)[0].getValue());
			cacheDao.insertOrUpdate(fileUtils.getFileName(), contentLength);
			//
			InputStream data = response.getEntity().getContent();
			byte[] buff = new byte[1024 * 40];
			int readBytes = 0;
			int fileSize = 0;
			while (fileUtils.isEnable() && (readBytes = data.read(buff, 0, buff.length)) != -1) {
				fileUtils.write(buff, readBytes);
				fileSize += readBytes;
				if (fileSize >= size) {
					break;
				}
			}
			if (fileUtils.isEnable()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "缓存异常", e);
			return false;
		} finally {
			Log.i(LOG_TAG, "缓存结束");
			ProxyFileUtils.close(fileUtils, false);
		}
	}
}
