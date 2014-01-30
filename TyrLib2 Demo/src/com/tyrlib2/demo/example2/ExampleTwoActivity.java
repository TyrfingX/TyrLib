package com.tyrlib2.demo.example2;

import android.content.Context;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.AndroidOpenGLActivity;

/**
 * This demonstrates how to load a model exported by a 3D modelling program
 * (Used here: A blender model exported to the iqe format)
 * and how to play exported animations.
 * @author Sascha
 *
 */

public class ExampleTwoActivity extends AndroidOpenGLActivity {
	public static Context CONTEXT;
	
	@Override
	public void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
