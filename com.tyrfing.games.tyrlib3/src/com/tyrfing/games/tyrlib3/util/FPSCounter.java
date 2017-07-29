package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Text2;

public class FPSCounter implements IUpdateable {
	
	private int frames;
	private float t = 0;
	private Text2 text;
	
	public FPSCounter() {
		text = SceneManager.getInstance().createText2("0", 0, Color.WHITE);
		text.noMVP();
		SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3F(0,0,0));
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
