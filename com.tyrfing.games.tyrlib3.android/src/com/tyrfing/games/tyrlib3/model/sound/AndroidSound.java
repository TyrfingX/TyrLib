package com.tyrfing.games.tyrlib3.model.sound;

import com.tyrfing.games.tyrlib3.model.sound.ISound;
import com.tyrfing.games.tyrlib3.model.sound.ISoundListener;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class AndroidSound implements ISound {
	private SoundPool sp;
	private int soundID;
	private int streamID;
	private ISoundListener listener;
	
	private int requests;
	private boolean loaded;
	
	public AndroidSound(final SoundPool sp, final int soundID) {
		this.sp = sp;
		this.soundID = soundID;
		
		sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				for (int i = 0; i < requests; ++i) {
					streamID = sp.play(soundID, 1, 1, 0, 0, 1);
				}
				loaded = true;
				requests = 0;
			}
		});
	}

	@Override
	public boolean isPlaybackFinished() {
		return true;
	}

	@Override
	public void play() {
		if (loaded) {
			streamID = sp.play(soundID, 1, 1, 0, 0, 1);
		} else {
			requests++;
		}
		
		if (listener != null) {
			listener.onPlaybackFinished();
		}
	}

	@Override
	public void pause() {
		sp.autoPause();
	}

	@Override
	public void stop() {
		sp.stop(streamID);
	}

	@Override
	public void unload() {
		sp.unload(soundID);
	}

	@Override
	public void setOnPlaybackFinishedListener(final ISoundListener listener) {
		this.listener = listener;
	}
}
