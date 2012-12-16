package com.tyrlib2.demo.example1;

import android.content.Context;

import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.scene.SceneManager;

/**
 * This activity demonstrates the use of a simple scene setup:
 * - How to setup ambient lighting
 * - How to create simple objects and add them to the scene tree
 * - How to make objects move/use time input
 * @author Sascha
 *
 */

public class ExampleOneActivity extends OpenGLActivity {

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
	protected void go() {
		CONTEXT = this;
		SceneManager.getInstance().getRenderer().addFrameListener(new FrameListener());
	}

}
