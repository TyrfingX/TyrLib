package com.TyrLib2.PC.main;

import com.tyrfing.games.tyrlib3.tinysound.Music;
import com.tyrlib2.sound.IMusic;

public class PCMusic implements IMusic {

	private Music music;
	
	public PCMusic(Music music) {
		this.music = music;
	}
	
	@Override
	public void play() {
		music.play(true);
	}
	
	@Override
	public void play(double volume) {
		music.play(true, volume);
	}


	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void stop() {
		music.stop();
	}

	@Override
	public void unload() {
		music.unload();
	}

}
