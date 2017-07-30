package tyrfing.common.sound;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
	private static SoundManager instance;
	
	private Map<String, Soundtrack> soundtracks;
	
	protected Soundtrack currentlyPlaying;
	
	public SoundManager( ){
		soundtracks = new HashMap<String, Soundtrack>();
	}
	
	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		
		return instance;
	}
	
	public Soundtrack createSoundtrack(int source, String name) {
		Soundtrack soundtrack = new Soundtrack(source);
		soundtracks.put(name, soundtrack);
		
		return soundtrack;
	}
	
	public Soundtrack getSoundtrack(String name) {
		return soundtracks.get(name);
	}
	
	public Soundtrack getCurrentlyPlaying() {
		return currentlyPlaying;
	}
}
