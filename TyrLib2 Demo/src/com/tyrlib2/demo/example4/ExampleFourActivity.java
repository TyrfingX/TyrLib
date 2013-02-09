package com.tyrlib2.demo.example4;

import android.content.Context;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.OpenGLActivity;

/**
 * This demonstrates how to create a particle system
 * from an XML file.
 * @author Sascha
 *
 */

public class ExampleFourActivity extends OpenGLActivity {
	public static Context CONTEXT;
	
	@Override
	protected void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
