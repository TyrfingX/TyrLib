package com.tyrlib2.util;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderables.Text2;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class TextureFailCounter implements IUpdateable {
	
	private int fails;
	private int frames;
	private float t = 0;
	private Text2 text;
	
	public TextureFailCounter() {
		text = SceneManager.getInstance().createText2("0", 0, Color.WHITE);
		text.noMVP();
		SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0));
		text.attachTo(node);
	}
	
	@Override
	public void onUpdate(float time) {

		t += time;
		fails += OpenGLRenderer.textureFails;
		frames++;
		
		if (t >= 1) {
			text.setText(String.valueOf((int)(fails/frames)));
			t = 0;
			fails = 0;
			frames = 0;
		}

	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
