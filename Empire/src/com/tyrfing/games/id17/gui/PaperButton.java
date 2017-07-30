package com.tyrfing.games.id17.gui;

import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.Frame;
import com.tyrlib2.gui.Frame.FrameImagePosition;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class PaperButton extends Window {

	private Frame frame;
	private Label label;
	
	private boolean click;
	private boolean enabled = true;
	
	public PaperButton(String name, Vector2 pos, Vector2 size, float borderSize, String text) {
		super(name, size);
		
		setRelativePos(pos);
		
		
		Skin skin = WindowManager.getInstance().getSkin();
		float tmp = skin.FRAME_BORDER_SIZE;
		skin.FRAME_BORDER_SIZE = borderSize;
		
		frame = WindowManager.getInstance().createFrame(name + "/FRAME", new Vector2(), size);
		frame.setInheritsAlpha(true);
		
		frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2");
		
		skin.FRAME_BORDER_SIZE = tmp;
		
		label = (Label) WindowManager.getInstance().createLabel(name + "/LABEL", new Vector2(size.x * 0.5f, 0), text);
		label.setColor(Color.BLACK);
		label.setInheritsAlpha(true);
		label.setAlignment(ALIGNMENT.CENTER);
		
		WindowManager.getInstance().addWindow(this);
		
		this.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (enabled) {
					frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2_ACTIVE");
					click = true;
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click && enabled) {
					click = false;
					frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2");
					WindowEvent windowEvent = new WindowEvent(frame, WindowEventType.CONFIRMED);
					fireEvent(windowEvent);
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click && enabled) {
					click = false;
					frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2");
				}
			}
		});
		
		frame.addChild(label);
		addChild(frame);
	}
	
	public void unhighlight() {
		frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2");
	}
	
	public void highlight() {
		frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2_ACTIVE");
	}
	
	@Override
	public float getAlpha() {
		return label.getAlpha();
	}
	
	public Label getLabel() {
		return label;
	}
	
	public void enable() {
		enabled = true;
		frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2");
		this.setReceiveTouchEvents(true);
	}
	
	public void disable() {
		enabled = false;
		frame.setBgRegion(FrameImagePosition.MIDDLE, "PAPER2_DISABLED");
		this.setReceiveTouchEvents(false);
	}
	
}
