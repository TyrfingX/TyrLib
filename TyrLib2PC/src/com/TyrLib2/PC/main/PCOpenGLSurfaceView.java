package com.TyrLib2.PC.main;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

import com.TyrLib2.PC.input.PCKeyboardEvent;
import com.TyrLib2.PC.input.PCMotionEvent;
import com.TyrLib2.PC.input.PCView;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.opengl.util.FPSAnimator;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.InputManager;


public class PCOpenGLSurfaceView extends Frame implements MouseListener, KeyListener {
	
	public static FPSAnimator animator;
	
	public static PCView view;
	private PCOpenGLRenderer renderer;
	
	public PCOpenGLSurfaceView(String name, GLCapabilities caps) {
        
		this.setTitle(name);
		
        InputManager.getInstance();
        InputManager.VK_ENTER = KeyEvent.VK_ENTER;
        InputManager.VK_BACK_SPACE = KeyEvent.VK_BACK_SPACE;
        
        GLCanvas canvas = new GLCanvas(caps);
        //GLCanvas canvas = new GLCanvas(caps);
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
        new AWTKeyAdapter(this).addTo(canvas);
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
		final PCMotionEvent e = new PCMotionEvent(arg0);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onMove(view, e);
			}
	    });
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
		final PCMotionEvent e = new PCMotionEvent(arg0);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onScroll(view, e);
			}
	    });
	}
	
	public void postMouseEvent(final MouseEvent event, int action) {
		final PCMotionEvent e = new PCMotionEvent(event, action);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onTouch(view, e);
			}
	    });
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		postKeyEvent(arg0, IKeyboardEvent.ACTION_PRESSED);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		postKeyEvent(arg0, IKeyboardEvent.ACTION_RELEASED);
	}
	
	public void postKeyEvent(final KeyEvent event, int action) {
		final PCKeyboardEvent e = new PCKeyboardEvent(event, action);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onKeyEvent(e);
			}
	    });
	}
}
