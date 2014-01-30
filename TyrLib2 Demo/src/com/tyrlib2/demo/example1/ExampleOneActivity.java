package com.tyrlib2.demo.example1;

import android.content.Context;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.AndroidOpenGLActivity;

/**
 * This activity demonstrates the use of a simple scene setup:
 * - How to setup ambient lighting
 * - How to create simple objects and add them to the scene tree
 * - How to make objects move/use time input
 * @author Sascha
 *
 */

public class ExampleOneActivity extends AndroidOpenGLActivity {

	/**
	 * This method will be called as soon as the project has finished setting up 
	 * the most important stuff.
	 * The OpenGL context, however, has not been created a this point.
	 * Code requiring open gl (loading textures, shaders, etc) need to be done as soon
	 * as the OpenGL Surface has been created.
	 * In order to react to this type of event FrameListeners have to be used.
	 */
	
	public static Context CONTEXT;
	
	@Override
	public void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
