package com.TyrLib2.PC.main;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

import com.TyrLib2.PC.input.PCMotionEvent;
import com.TyrLib2.PC.input.PCView;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.opengl.util.FPSAnimator;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.InputManager;


public class PCOpenGLSurfaceView extends Frame implements MouseListener {
	
	public static FPSAnimator animator;
	
	private PCView view;
	private PCOpenGLRenderer renderer;
	
	public PCOpenGLSurfaceView(String name, GLCapabilities caps) {
        
		this.setTitle(name);
		
        InputManager.getInstance();
        
        GLCanvas canvas = new GLCanvas();
        this.add(canvas);
        renderer = new PCOpenGLRenderer();
        canvas.addGLEventListener(renderer);

        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        animator = new FPSAnimator(canvas, 60);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        this.setLocationRelativeTo(null);
        
        view = new PCView(canvas);
        
        new AWTMouseAdapter(this).addTo(canvas);
	}
	
	public void startRendering() {
		animator.start();
		this.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		postMouseEvent(arg0, IMotionEvent.ACTION_MOVE);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		postMouseEvent(arg0, IMotionEvent.ACTION_DOWN);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		postMouseEvent(arg0, IMotionEvent.ACTION_UP);
	}

	@Override
	public void mouseWheelMoved(MouseEvent arg0) {
	}
	
	public void postMouseEvent(final MouseEvent event, int action) {
		action = action | ((event.getButton()-1) << IMotionEvent.ACTION_POINTER_INDEX_SHIFT);
		final PCMotionEvent e = new PCMotionEvent(event, action);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onTouch(view, e);
			}
	    });
	}
}
