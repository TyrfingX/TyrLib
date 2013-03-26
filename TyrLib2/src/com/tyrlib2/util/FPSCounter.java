package com.tyrlib2.util;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderables.Text2;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class FPSCounter implements IUpdateable {
	
	private int frames;
	private float t = 0;
	private Text2 text;
	
	public FPSCounter() {
		text = SceneManager.getInstance().createText2("0", 0, Color.WHITE);
		SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0));
		text.attachTo(node);
	}
	
	@Override
	public void onUpdate(float time) {

		t += time;
		frames++;
		
		while (t >= 1) {
			text.setText(String.valueOf((int)(frames/t)));
			t = 0;
			frames = 0;
		}

	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
