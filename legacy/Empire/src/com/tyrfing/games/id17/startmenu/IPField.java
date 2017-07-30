package com.tyrfing.games.id17.startmenu;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IKeyboardListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.main.AndroidMedia;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class IPField implements IFrameListener, IKeyboardListener {
	
	private Label ip;
	private String text = "";
	private boolean flip;
	
	public boolean destroyed = false;
	
	public static final float FLIP_TIME = 0.5f;
	
	private float passedTime;
	private boolean active;
	
	public IPField(final Window caller) {
		build(caller);
	}
	
	public void build(Window caller) {
		ip = (Label) WindowManager.getInstance().createLabel(	"IPLabel", 
				new Vector2(	Menu.OPTION_SIZE.get().x*0.05f, 
							  	Menu.OPTION_SIZE.get().y*0.15f),
				"IP: " + text + " ");
		caller.addChild(ip);
		ip.setFont(SceneManager.getInstance().getFont("FONT_20"));
		ip.setColor(new Color(0.3f, 0.19f, 0.12f, 1));
		ip.setBgColor(new Color(0.76f, 0.635f, 0.38f, 0.7f));
		ip.setInheritsAlpha(true);
		SceneManager.getInstance().addFrameListener(this);
	}

	@Override
	public void onSurfaceCreated() {
	}

	@Override
	public void onSurfaceChanged() {
	}

	@Override
	public void onFrameRendered(float time) {
		if (active) {
			passedTime += time;
			if (passedTime >= FLIP_TIME) {
				if (flip) {
					ip.setText("IP: " + text + "|");
				} else {
					ip.setText("IP: " + text + " ");
				}
				
				flip = !flip;
				
				passedTime -= FLIP_TIME;
			}
		} else {
			ip.setText("IP: " + text + "|");
		}
	}

	@Override
	public boolean onPress(IKeyboardEvent e) {
		
		if (e.isPrintable()) {
			if (e.getKeyCode() == InputManager.VK_BACK_SPACE) {
				if (text.length() > 0) {
					text = text.substring(0, text.length() - 1);
				}
			} else {
				char c = e.getKeyChar();
				text += c;
			}

			ip.setText("IP: " + text + " ");
		} else {
			if (e.getKeyCode() == InputManager.VK_ENTER) {
				destroy();
				EmpireFrameListener.MAIN_FRAME.connectToGame(text);
			} else if (e.getKeyCode() == InputManager.VK_BACK_SPACE) {
				if (text.length() > 0) {
					text = text.substring(0, text.length() - 1);
				}
			} else if (		(e.getKeyCode() == InputManager.VK_V) 
							&& ((e.getModifiers() & InputManager.CTRL_MASK) != 0)) {
				String data = Media.CONTEXT.getClipboard();
				text += data;
				ip.setText("IP: " + text + " ");
			}
		}
		
		return true;
	}
	
	public void destroy() {
		SceneManager.getInstance().removeFrameListener(this);
		InputManager.getInstance().removeKeyboardListener(this);
		WindowManager.getInstance().destroyWindow(ip);
		if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.ANDROID_TARGET) {
			((AndroidMedia)Media.CONTEXT).hideKeyboard();
		}
		
		destroyed = true;
	}

	@Override
	public boolean onRelease(IKeyboardEvent e) {
		return true;
	}

	public String getAddress() {
		return text;
	}

	public void activate() {
		InputManager.getInstance().addKeyboardListener(this);
		active = true;
	}

	@Override
	public long getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}
}
