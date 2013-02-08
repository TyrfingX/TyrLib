package com.tyrlib2.demo.example3;

import android.content.Context;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.OpenGLActivity;

/**
 * This demonstrates how to create terrain using a heightmap and how
 * to apply textures depending on height & slope.
 * @author Sascha
 *
 */

public class ExampleThreeActivity extends OpenGLActivity {
	public static Context CONTEXT;
	
	@Override
	protected void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
