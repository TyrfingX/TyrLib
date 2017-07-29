package com.tyrfing.games.tyrlib3.pc.sound;

import com.tyrfing.games.tyrlib3.sound.ISound;
import com.tyrfing.games.tyrlib3.sound.ISoundListener;
import com.tyrfing.games.tyrlib3.tinysound.Sound;
import com.tyrfing.games.tyrlib3.tinysound.internal.SoundListener;
import com.tyrfing.games.tyrlib3.tinysound.internal.SoundReference;

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
