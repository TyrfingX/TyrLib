package tyrfing.common.sound;

import java.io.IOException;

import tyrfing.common.game.BaseGame;
import android.media.MediaPlayer;

public class Soundtrack {
	
	private MediaPlayer mediaPlayer;
	
	public Soundtrack(int source) {
		mediaPlayer = MediaPlayer.create(BaseGame.CONTEXT, source);
		mediaPlayer.setLooping(true);
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play() {
		
		if (SoundManager.getInstance().currentlyPlaying != null) { 
			SoundManager.getInstance().currentlyPlaying.stop();
		}
		SoundManager.getInstance().currentlyPlaying = this;
		
		mediaPlayer.start();
	}
	
	public void stop()  {
		mediaPlayer.pause();
	}
	
	public void fadeOut(float time) {
		
	}
	
	public void fadeIn(float time) {
		
	}
}
