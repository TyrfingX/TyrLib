package com.tyrlib2.demo.example2;

import android.content.Context;

import com.tyrlib2.demo.example2.FrameListener;
import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.scene.SceneManager;

/**
 * This demonstrates how to load a model exported by a 3D modelling program
 * (Used here: A blender model exported to the iqe format)
 * and how to play exported animations.
 * @author Sascha
 *
 */

public class ExampleTwoActivity extends OpenGLActivity {
	public static Context CONTEXT;
	
	@Override
	protected void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
