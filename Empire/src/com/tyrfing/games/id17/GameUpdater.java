package com.tyrfing.games.id17;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.scene.SceneManager;

public class GameUpdater implements IFrameListener {
	
	private Updater updater = new Updater();
	private static GameUpdater instance;
	
	public GameUpdater() {
		SceneManager.getInstance().getRenderer().addFrameListener(this);
	}
	
	public static Updater getUpdater() {
		if (instance == null) {
			instance = new GameUpdater();
		}
		
		return instance.updater;
	}
	
	@Override
	public void onSurfaceCreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFrameRendered(float time) {
		updater.onFrameRendered(time);
	}

}
