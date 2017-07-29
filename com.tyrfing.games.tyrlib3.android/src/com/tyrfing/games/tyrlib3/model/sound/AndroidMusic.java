package com.tyrfing.games.tyrlib3.model.sound;

import java.io.IOException;

import com.tyrfing.games.tyrlib3.model.sound.IMusic;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AndroidMusic implements IMusic {

	private MediaPlayer mp;
	private AssetFileDescriptor afd;
	
	public AndroidMusic(MediaPlayer mp, AssetFileDescriptor afd) {
		this.mp = mp;
		this.afd = afd;
	}
	
	@Override
	public void play(double volume) {
        try {
        	if (mp.isPlaying()) {
        		mp.release();
        	}
        	mp.reset();
        	mp.setLooping(true);
        	mp.setVolume((float)volume, (float)volume);
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
	        mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void play() {
        try {
        	if (mp.isPlaying()) {
        		mp.release();
        	}
        	mp.reset();
        	mp.setLooping(true);
        	mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
	        mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		mp.pause();
	}

	@Override
	public void stop() {
		mp.stop();
	}

	@Override
	public void unload() {
		mp.stop();
		mp.release();
	}

}
