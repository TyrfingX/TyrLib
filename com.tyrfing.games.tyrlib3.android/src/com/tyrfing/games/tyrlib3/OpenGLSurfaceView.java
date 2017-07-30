package com.tyrfing.games.tyrlib3;

import com.tyrfing.games.tyrlib3.edit.input.AndroidKeyboardEvent;
import com.tyrfing.games.tyrlib3.edit.input.AndroidMotionEvent;
import com.tyrfing.games.tyrlib3.edit.input.AndroidView;
import com.tyrfing.games.tyrlib3.edit.input.IKeyboardEvent;
import com.tyrfing.games.tyrlib3.edit.input.InputManager;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.AndroidOpenGLRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * View for the game. Properly sets the renderer up and sets the rendering options.
 * 
 * @author Sascha
 *
 */

public class OpenGLSurfaceView extends GLSurfaceView {
	
	public static OpenGLSurfaceView instance;
	private AndroidView view = new AndroidView(this);
	
	public final InputMethodManager imm;
	
	public OpenGLSurfaceView(AndroidOpenGLActivity context){
        super(context);

        instance = this;
        
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        
        imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //setSoftKeyboardHandler();
        
        InputManager.VK_BACK_SPACE = KeyEvent.KEYCODE_DEL;
        InputManager.VK_ENTER = KeyEvent.KEYCODE_ENTER;
        InputManager.VK_SPACE = KeyEvent.KEYCODE_SPACE;
        InputManager.VK_PLUS = KeyEvent.KEYCODE_PLUS;
        InputManager.VK_MINUS = KeyEvent.KEYCODE_MINUS;
        InputManager.VK_ONE = KeyEvent.KEYCODE_1;
        
        InputManager.getInstance();
        
        AndroidOpenGLRenderer renderer = new AndroidOpenGLRenderer(context);
        
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        
        setKeepScreenOn(true);
        
        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
    }
	
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {    	
    	final MotionEvent eventCopy = MotionEvent.obtain(event);
    	queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onTouch(view, new AndroidMotionEvent(eventCopy));
			}
	    });
    	return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	final AndroidKeyboardEvent eventCopy = new AndroidKeyboardEvent(IKeyboardEvent.ACTION_PRESSED, (short) keyCode, event);
    	queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onKeyEvent(eventCopy);
			}
	    });
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	final AndroidKeyboardEvent eventCopy = new AndroidKeyboardEvent(IKeyboardEvent.ACTION_RELEASED, (short) keyCode, event);
    	queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onKeyEvent(eventCopy);
			}
	    });
    	return super.onKeyUp(keyCode, event);
    }
    
}
