package com.tyrfing.games.tyrlib3.sound;

import java.util.ArrayList;
import java.util.List;

public class SoundSequence implements ISound, ISoundListener {

	private List<ISound> sounds = new ArrayList<ISound>();
	private int current = -1;
	private ISoundListener listener;
	
	public void addSound(ISound sound) {
		sounds.add(sound);
		sound.setOnPlaybackFinishedListener(this);
	}
	
	@Override
	public boolean isPlaybackFinished() {
		return current == -1;
	}

	@Override
	public void play() {
		current = 0;
		sounds.get(current).play();
	}

	@Override
	public void pause() {
		sounds.get(current).pause();
	}

	@Override
	public void stop() {
		sounds.get(current).stop();
		current = -1;
	}

	@Override
	public void unload() {
		for (ISound sound : sounds) {
			sound.unload();
		}
		
		sounds.clear();
	}

	@Override
	public void setOnPlaybackFinishedListener(ISoundListener listener) {
		this.listener = listener;
	}

	@Override
	public void onPlaybackFinished() {
		current++;
		if (current != sounds.size()) {
			sounds.get(current).play();
		} else {
			current = -1;
			if (listener != null) {
				listener.onPlaybackFinished();
			}
		}
	}

}
