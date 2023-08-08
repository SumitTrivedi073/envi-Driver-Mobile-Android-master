package sgs.env.ecabsdriver.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import sgs.env.ecabsdriver.R;


public class MediaPlayerSingleton {
	
	private static MediaPlayerSingleton instance;
	
	public static MediaPlayer mediaPlayer;
	
	private Context appContext;
	
	private MediaPlayerSingleton() { }
	
	public static MediaPlayer getSingletonMedia() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mediaPlayer = MediaPlayer.create(get(), R.raw.ride_notification);
		return mediaPlayer;
	}
	
	public static Context get() {
		return getInstance().getContext();
	}
	
	private Context getContext() {
		return appContext;
	}
	
	public static synchronized MediaPlayerSingleton getInstance() {
		if (instance == null) {
			instance = new MediaPlayerSingleton();
		}
		return instance;
	}
	
	public void init(Context context) {
		if (appContext == null) {
			this.appContext = context;
		}
	}
	
}
