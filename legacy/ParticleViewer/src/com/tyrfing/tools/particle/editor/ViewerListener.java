package com.tyrfing.tools.particle.editor;

import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.gui.ParticleWindow;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IKeyboardListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

public class ViewerListener implements IFrameListener, IKeyboardListener {

	private String currentFile;
	private JFileChooser fc;
	
	private SceneNode particleNode;
	private ParticleSystem system;
	private ParticleWindow window;
	
	@Override
	public void onSurfaceCreated() {
		particleNode = SceneManager.getInstance().getRootSceneNode().createChild();
		SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild(0,5,0);
		Camera camera = SceneManager.getInstance().createCamera(new Vector3(0,-1,0), new Vector3(0,0,1), node);
		camera.use();
		
		URL url = getClass().getResource("/res/assets/particle");
		String defaultPath = url.getPath();
		
		fc = new JFileChooser(defaultPath);
		
		InputManager.getInstance().addKeyboardListener(this);
		
		TextureManager.getInstance().createTexture("SMOKE", Media.CONTEXT.getResourceID("smoke", "drawable"));
		TextureManager.getInstance().createTexture("THUNDER", Media.CONTEXT.getResourceID("thunder", "drawable"));
		//TextureManager.getInstance().createTexture("CROSS", Media.CONTEXT.getResourceID("cross", "drawable"));
		//TextureManager.getInstance().createTexture("SLASH", Media.CONTEXT.getResourceID("slash", "drawable"));
		//TextureManager.getInstance().createTexture("SLEEP", Media.CONTEXT.getResourceID("sleep", "drawable"));
		
		WindowManager.getInstance().setScales(new Vector2[] {
				new Vector2(1, 1),
			});
	}

	@Override
	public void onSurfaceChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFrameRendered(float time) {
		if (system != null) {
			system.onUpdate(time);
			if (system instanceof ComplexParticleSystem && ((ComplexParticleSystem) system).isSaturated()) {
				reload();		
			}
		} else if (window != null && window.isSaturated()) {
			SceneManager.getInstance().removeParticleSystemFactory("particle/" + currentFile);
			reload();
		}
	}

	public void reload() {
		SceneManager.getInstance().removeParticleSystemFactory("particle/" + currentFile);
		if (system != null) {
			SceneManager.getInstance().destroyRenderable(system);
            system = SceneManager.getInstance().createParticleSystem("particle/" + currentFile);
            particleNode.attachSceneObject(system);		
		} else if (window != null) {
			WindowManager.getInstance().destroyWindow(window);
			window = (ParticleWindow) WindowManager.getInstance().createParticleWindow(window.getName(), new Vector2(0.5f, -0.5f), "particle/" + currentFile);
		}
	}
	
	@Override
	public boolean onPress(IKeyboardEvent e) {
		
		if (e.getKeyCode() == InputManager.VK_ESC) {
			Media.CONTEXT.quit();
		} else if (e.getKeyCode() == InputManager.VK_SPACE) {
			reload();
		} else if (e.getKeyChar() == '3'){
			int returnVal = fc.showOpenDialog(null);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            currentFile = file.getName();
	            System.out.println("Opening: " + currentFile);
	            
	            system = SceneManager.getInstance().createParticleSystem("particle/" + currentFile);
	            particleNode.attachSceneObject(system);
	        } 
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final int returnVal = fc.showOpenDialog(null);
					SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
						@Override
						public void run() {
							if (returnVal == JFileChooser.APPROVE_OPTION) {
					            File file = fc.getSelectedFile();
					            currentFile = file.getName();
					            System.out.println("Opening: " + currentFile);
					            window = (ParticleWindow) WindowManager.getInstance().createParticleWindow("Particle", new Vector2(0.5f, -0.5f), "particle/" + currentFile);
					        }
						}
			        });
				}
			});
		}
		
		return false;
	}

	@Override
	public boolean onRelease(IKeyboardEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
