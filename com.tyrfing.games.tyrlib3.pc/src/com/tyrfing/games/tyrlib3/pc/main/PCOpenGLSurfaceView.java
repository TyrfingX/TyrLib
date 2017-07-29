package com.tyrfing.games.tyrlib3.pc.main;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.newt.event.awt.AWTWindowAdapter;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.input.IKeyboardEvent;
import com.tyrfing.games.tyrlib3.input.IMotionEvent;
import com.tyrfing.games.tyrlib3.input.InputManager;
import com.tyrfing.games.tyrlib3.main.OpenGLActivity;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.pc.config.Config;
import com.tyrfing.games.tyrlib3.pc.config.Config.ScreenState;
import com.tyrfing.games.tyrlib3.pc.graphics.renderer.PCOpenGLRenderer;
import com.tyrfing.games.tyrlib3.pc.input.PCKeyboardEvent;
import com.tyrfing.games.tyrlib3.pc.input.PCMotionEvent;
import com.tyrfing.games.tyrlib3.pc.input.PCView;


public class PCOpenGLSurfaceView implements MouseListener, KeyListener, WindowListener {

	public static FPSAnimator animator;
	
	public static PCView view;
	private PCOpenGLRenderer renderer;
	private GLWindow window;
	private JFrame frame;
	private GLCanvas canvas;
	
	private boolean useNEWT;
	private boolean useFullscreen;
	
	private boolean destroyed = false;
	
	public PCOpenGLSurfaceView(OpenGLActivity activity, JFrame old, Config config, String name, GLCapabilities caps) {
		
        InputManager.getInstance();
        InputManager.VK_ENTER = KeyEvent.VK_ENTER;
        InputManager.VK_BACK_SPACE = KeyEvent.VK_BACK_SPACE;
        InputManager.VK_V =  KeyEvent.VK_V;
        InputManager.CTRL_MASK = KeyEvent.CTRL_MASK;
        InputManager.VK_ESC = KeyEvent.VK_ESCAPE;
        InputManager.VK_SPACE = KeyEvent.VK_SPACE;
        InputManager.VK_PLUS = KeyEvent.VK_PLUS;
        InputManager.VK_MINUS = KeyEvent.VK_MINUS;
        InputManager.VK_UP = KeyEvent.VK_UP;
        InputManager.VK_DOWN = KeyEvent.VK_DOWN;
        InputManager.VK_RIGHT = KeyEvent.VK_RIGHT;
        InputManager.VK_LEFT = KeyEvent.VK_LEFT;
        InputManager.VK_ONE = KeyEvent.VK_1;
        
        useFullscreen = config == null || config.screenState == ScreenState.FULLSCREEN;
        old.setVisible(false);
        old.dispose();
        
        renderer = new PCOpenGLRenderer(activity);
        
		String OS = System.getProperty("os.name").toLowerCase();
		//useNEWT = useFullscreen && isUnix(OS);
        
        if (useNEWT) {
        	
            window = GLWindow.create(caps);
            window.addGLEventListener(renderer);
            
        	window.setFullscreen(true);
        	window.setAlwaysOnTop(false);
        	
            window.addKeyListener(this);
            window.addWindowListener(this);
            window.addMouseListener(this);
            
            window.setVisible(true);
            
            animator = new FPSAnimator(window, 60);
        } else {
        	
            canvas = new GLCanvas(caps);
            canvas.addGLEventListener(renderer);
        	
            frame = new JFrame();
			
        	if (config.screenState == ScreenState.WINDOWED) {
        		frame.setSize((int)config.screenSize.x, (int)config.screenSize.y);
        		frame.setLocationRelativeTo(null);
        	} else if (config == null || config.screenState == ScreenState.FULLSCREEN) {
        		frame.setUndecorated(true);
        		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        		GraphicsDevice vc = ge.getDefaultScreenDevice();
        		
        		vc.setFullScreenWindow(frame);		
        		
        		 //check low-level display changes are supported for this graphics device
        		DisplayMode dm = new DisplayMode(frame.getWidth(), frame.getHeight(), 16, DisplayMode.REFRESH_RATE_UNKNOWN);
        		if(dm!=null && vc.isDisplayChangeSupported()) {
    				try{
    					vc.setDisplayMode(dm);
    				}
    				catch(Exception ex){
    				}
        		}
        		
        	}
        	
            frame.setVisible(true);
    		frame.setTitle(name);
        	
			if (!isUnix(OS) && !useFullscreen) {
				frame.setResizable(false);
			}
        	
        	frame.add(canvas);
        	
        	animator = new FPSAnimator(canvas, 60);
        	
            new AWTMouseAdapter(this).addTo(canvas);
            new AWTKeyAdapter(this).addTo(canvas);
            new AWTWindowAdapter(this).addTo(canvas);
            
            frame.addWindowListener(new WindowAdapter() {
            	@Override
            	public void windowClosed(java.awt.event.WindowEvent e) {
                	if (!destroyed) {
                		destroyed = true;
                		frame.dispose();
                    	System.exit( 0 );
                	}
            	}
            });

        }

        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        view = new PCView(this);
	}
	
	public Vector2F getSize() {
		if (useNEWT) {
			return new Vector2F(window.getScreen().getWidth(), window.getScreen().getHeight());
		} else {
			return new Vector2F((int)canvas.getSize().width, (int)canvas.getSize().getHeight());
		}
	}
	
	public GLWindow getWindow() {
		return window;
	}
	
	public void setSkipRendering(boolean skipRendering) {
		renderer.setSkipRendering(skipRendering);
	}
	
	public void startRendering() {
		animator.start();
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
		final PCMotionEvent e = new PCMotionEvent(arg0);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onEnterMouse(view, e);
			}
	    });
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		final PCMotionEvent e = new PCMotionEvent(arg0);
		
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onExitMouse(view, e);
			}
	    });
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

	@Override
	public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {
        new Thread(new Runnable() {

            public void run() {
            	if (!destroyed) {
            		destroyed = true;
                	System.exit( 0 );
            	}
            }
        }).start();
	}

	@Override
	public void windowDestroyed(com.jogamp.newt.event.WindowEvent arg0) {

	}

	@Override
	public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onGainFocus(view);
			}
	    });
	}

	@Override
	public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {
    	renderer.queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onLoseFocus(view);
			}
	    });
	}

	@Override
	public void windowMoved(com.jogamp.newt.event.WindowEvent arg0) {
	}

	@Override
	public void windowRepaint(WindowUpdateEvent arg0) {
	}

	@Override
	public void windowResized(com.jogamp.newt.event.WindowEvent arg0) {
	}
	
	public static boolean isUnix(String os) {
		 
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 );
 
	}
}
