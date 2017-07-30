package com.TyrLib2.PC.main;

import com.tyrfing.games.tyrlib3.tinysound.Sound;
import com.tyrfing.games.tyrlib3.tinysound.internal.SoundListener;
import com.tyrfing.games.tyrlib3.tinysound.internal.SoundReference;
import com.tyrlib2.sound.ISound;
import com.tyrlib2.sound.ISoundListener;

public class PCSound implements ISound {

	private ISoundListener listener;
	private SoundReference soundRef;
	private Sound sound;
	
	public PCSound(Sound sound) {
		this.sound = sound;
	}
	
	@Override
	public boolean isPlaybackFinished() {
		return soundRef == null || soundRef.bytesAvailable() <= 0;
	}

	@Override
	public void play() {
		soundRef = sound.play();
		if (listener != null) {
			soundRef.setSoundListener(new SoundListener() {
				@Override
				public void onPlaybackFinished() {
					listener.onPlaybackFinished();
				}
			});
		}
	}

	@Override
	public void pause() {
		sound.stop();
	}

	@Override
	public void stop() {
		sound.stop();
	}

	@Override
	public void unload() {
		sound.unload();
	}

	@Override
	public void setOnPlaybackFinishedListener(ISoundListener soundListener) {
		this.listener = soundListener;
	}

}
