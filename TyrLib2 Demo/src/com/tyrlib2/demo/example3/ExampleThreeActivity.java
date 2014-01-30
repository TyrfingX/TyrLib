package com.tyrlib2.demo.example3;

import android.content.Context;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.AndroidOpenGLActivity;

/**
 * This demonstrates how to create terrain using a heightmap and how
 * to apply textures depending on height & slope.
 * @author Sascha
 *
 */

public class ExampleThreeActivity extends AndroidOpenGLActivity {
	public static Context CONTEXT;
	
	@Override
	public void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
