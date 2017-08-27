package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class RectItemListEntry extends ItemListEntry {

	private final IEventListener onEnter = new IEventListener() {
		@Override
		public void onEvent(WindowEvent event) {
			onEnter();
		}
	};
	
	private final IEventListener onLeave = new IEventListener() {
		@Override
		public void onEvent(WindowEvent event) {
			onLeave();
		}
	};
	
	private final IEventListener onClick = new IEventListener() {
		@Override
		public void onEvent(WindowEvent event) {
			onClick();
		}
	};
	
	protected Window main;
	protected boolean selected;
	
	private Paint paint;
	private Color highlighted;
	
	public RectItemListEntry(String name, Vector2 size, Paint paint, Color highlighted) {
		super(name, size);
		
		this.paint = paint;
		this.highlighted = highlighted;
		
		main = WindowManager.getInstance().createRectWindow(name + "/BG", new Vector2(), size, paint);
		main.setInheritsAlpha(true);
		this.addChild(main);
		this.setReceiveTouchEvents(true);
		this.setPassTouchEventsThrough(true);

		this.addEventListener(WindowEventType.TOUCH_ENTERS, onEnter);
		this.addEventListener(WindowEventType.MOUSE_ENTERS, onEnter);
		
		this.addEventListener(WindowEventType.TOUCH_LEAVES, onLeave);
		this.addEventListener(WindowEventType.MOUSE_LEAVES, onLeave);
		
		this.addEventListener(WindowEventType.TOUCH, onClick);
		
		//onEnterSound = SoundManager.getInstance().createSound("menuselect");
	}
	
	private void onEnter() {
		setHighlighted(true);
	}

	private void onLeave() {
		if (!selected) {
			setHighlighted(false);		
		}
	}
	
	protected void onClick() {
		setSelect(true);
		this.fireEvent(new WindowEvent(this, WindowEventType.CONFIRMED));
	}
	
	public void setHighlighted(boolean state) {
		Rectangle2 rect = (Rectangle2) main.getComponent(0);
		if (state) {
			//onEnterSound.play();
			rect.setColor(highlighted.copy());
		} else {
			rect.setColor(paint.color.copy());
		}
		
		if (selected) {
			rect.setAlpha(1);
		}
	}
	
	public void setSelect(boolean state) {
		this.selected = state;
		
		if (selected) {
			setHighlighted(true);
		} else {
			setHighlighted(false);
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public float getAlpha() {
		return main.getAlpha();
	}
	
}
