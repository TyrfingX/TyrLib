package com.tyrfing.games.tyrlib3.model.sound;

import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.tyrlib3.Media;
import com.tyrfing.games.tyrlib3.util.BackgroundWorker;


public class SoundManager {
	private static SoundManager instance;
	
	private Map<String, ISound> sounds = new HashMap<String, ISound>();
	private Map<String, IMusic> musics = new HashMap<String, IMusic>();
	
	private IMusic bgm;
	
	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
	
		return instance;
	}
	
	public ISound createSound(String source) {
		if (sounds.containsKey(source)) return sounds.get(source);
		ISound sound = Media.CONTEXT.createSound(source);
		sounds.put(source, sound);
		return sound;
	}
	
	public ISound createSoundsequence(String[] sources) {
		SoundSequence seq = new SoundSequence();
		for (String source : sources) {
			seq.addSound(createSound(source));
		}
		return seq;
	}
	
	public void backgroundCreateSound(final String source) {
		if (!sounds.containsKey(source)) {
			BackgroundWorker.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					 ISound sound = Media.CONTEXT.createSound(source);
					 sounds.put(source, sound);
					 System.out.println("Background loaded sound: " + source);
				}
			});
		}
	}
	
	public IMusic createMusic(String source) {
		if (musics.containsKey(source)) return musics.get(source);
		IMusic music = Media.CONTEXT.createMusic(source);
		musics.put(source, music);
		return music;
	}
	
	public void backgroundCreateMusic(final String source) {
		if (!sounds.containsKey(source)) {
			BackgroundWorker.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					IMusic music = Media.CONTEXT.createMusic(source);
					musics.put(source, music);
					System.out.println("Background loaded music: " + source);
				}
			});
		}
	}
	
	public void setBGM(String source, double volume) {
		if (source == null || source.equals("")) {
			if (bgm != null) {
				bgm.stop();
			}
			bgm = null;
			return;
		} else {
			IMusic music = createMusic(source); 
			if (music != bgm) {
				if (bgm != null) {
					bgm.stop();
				}
				bgm = music;
				bgm.play(volume);
			}
		}
	}
	
	public void stopBGM() {
		if (bgm != null) bgm.stop();
	}
	
	public void backgroundSetBGM(final String source, final double volume) {
		BackgroundWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					setBGM(source, volume);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
}
