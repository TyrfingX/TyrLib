package com.tyrlib2.sound;

public interface IMusic {
	public void play();
	public void play(double volume);
	public void pause();
	public void stop();
	public void unload();
}
