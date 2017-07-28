package com.tyrlib2.sound;


public interface ISound {
	public boolean isPlaybackFinished();
	
	public void play();
	public void pause();
	public void stop();
	public void unload();
	
	public void setOnPlaybackFinishedListener(ISoundListener listener);
}
