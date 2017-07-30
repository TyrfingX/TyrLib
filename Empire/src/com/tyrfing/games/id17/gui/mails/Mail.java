package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Frame;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;

public abstract class Mail extends Window {

	public static final ScaledVector2 SIZE = new ScaledVector2(0.45f, 0.5f, 1);
	public static final Vector2 SHOW_POS = new Vector2(0.3f+0.45f/2, -0.87f+0.25f);
	public static final ScaledVector2 SHOW_POS_OFFSET = new ScaledVector2(-SIZE.x/2, -SIZE.y/2, 1);
	public static final Vector2 HIDE_POS = new Vector2(1, 0);
	public static final ScaledVector2 BUTTON_SIZE = TabGUI.SIGIL_HOLDER_SIZE.multiply(0.8f);
	public static final ScaledVector2 CANCEL_HOLDER_POS = new ScaledVector2(SIZE.x, 0, 1);
	public static final ScaledVector2 CANCEL_HOLDER_POS_OFFSET = new ScaledVector2(-BUTTON_SIZE.x/2, -BUTTON_SIZE.y/2, 0);
	public static final ScaledVector2 CANCEL_POS = new ScaledVector2(CANCEL_HOLDER_POS.x, 0, 1);
	public static final ScaledVector2 CANCEL_POS_OFFSET = new ScaledVector2(-BUTTON_SIZE.x/2.3f, -BUTTON_SIZE.y/2.3f, 0);
	
	public static final ScaledVector2 MINIMIZE_HOLDER_POS = new ScaledVector2(SIZE.x, SIZE.y, 1);
	public static final ScaledVector2 MINIMIZE_POS_OFFSET = new ScaledVector2(-BUTTON_SIZE.x/2.4f, -BUTTON_SIZE.y/2.4f, 0);
	public static final ScaledVector2 MINIMIZE_POS = new ScaledVector2(MINIMIZE_HOLDER_POS.x, MINIMIZE_HOLDER_POS.y, 1);
	
	public static final ScaledVector2 BACK_HOLDER_POS = new ScaledVector2(0, 0, 1);
	public static final ScaledVector2 BACK_POS_OFFSET = new ScaledVector2(-BUTTON_SIZE.x/2.7f, -BUTTON_SIZE.y/2.7f, 0);
	public static final ScaledVector2 BACK_POS = new ScaledVector2(BACK_HOLDER_POS.x, BACK_HOLDER_POS.y, 1);
	
	public static final float MAX_TIME = World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	private float passedTime;
	
	protected Frame frame;
	private ImageBox cancel;
	private ImageBox minimize;
	
	private MailboxGUI gui;
	private String identity;
	
	private Mail backMail;
	private ImageBox back;
	
	protected Window mailHolder;
	protected String iconName = "Default";
	
	
	public Mail(String name) {
		super(name, SIZE.get());
		createFrameContent();
	}
	
	public void setIconName(String name) {
		this.iconName = name;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void remove() {
		
		frame.addEventListener(WindowEvent.WindowEventType.FADE_OUT_FINISHED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				destroy();
				WindowManager.getInstance().destroyWindow(frame);
			}
		});
		
		this.setRecuresiveReceiveTouchEvents(false);
		hide();
		gui.removeMail(this);
	}
	
	public void setMailbox(MailboxGUI gui) {
		this.gui = gui;
	}
	
	public void show() {
		frame.setRelativePos(SHOW_POS.add(SHOW_POS_OFFSET.get()));
		frame.fadeIn(1, 0.35f);
	}
	
	public void hide() {
		frame.fadeOut(0, 0.35f);
		
		gui.hideMail(this);
	}
	
	private void createFrameContent() {
		
		WindowManager.getInstance().addWindow(this);
		
		frame = WindowManager.getInstance().createFrame(this.getName() + "/FRAME", HIDE_POS, SIZE);
		frame.setReceiveTouchEvents(true);
		
	}
	
	protected void createButtons() {
		String name = this.getName();
		WindowManager.getInstance().createImageBox(name + "MAIL/CANCEL", CANCEL_POS.get().add(CANCEL_POS_OFFSET.get()), "MAIN_GUI", "CANCEL", BUTTON_SIZE.multiply(0.85f));
		WindowManager.getInstance().getWindow(name + "MAIL/CANCEL").setReceiveTouchEvents(false);
		frame.addChild(WindowManager.getInstance().getWindow(name + "MAIL/CANCEL"));
		
		cancel = (ImageBox) WindowManager.getInstance().createImageBox(name + "MAIL/CANCEL_HOLDER", CANCEL_HOLDER_POS.get().add(CANCEL_HOLDER_POS_OFFSET.get()), "MAIN_GUI", "SMALL_CIRCLE_BORDER", BUTTON_SIZE);
		frame.addChild(WindowManager.getInstance().getWindow(name + "MAIL/CANCEL_HOLDER"));
		cancel.setReceiveTouchEvents(true);
		cancel.setInheritsAlpha(true);
		
		cancel.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				remove();
			}
		});
		
		InputManager.getInstance().sort();
		frame.setAlpha(0);
	}
	
	public void setBackMail(Mail mail) {
		backMail = mail;
		
		Window holder = WindowManager.getInstance().createImageBox(this.getName() + "MAIL/BACK", BACK_POS.get().add(BACK_POS_OFFSET.get()), "MAIN_GUI", "BACK", BUTTON_SIZE.multiply(0.85f));
		holder.setReceiveTouchEvents(false);
		holder.setInheritsAlpha(true);
		frame.addChild(holder);
		
		back = (ImageBox) WindowManager.getInstance().createImageBox(this.getName() + "MAIL/BACK_HOLDER", BACK_HOLDER_POS.get().add(CANCEL_POS_OFFSET.get()), "MAIN_GUI", "SMALL_CIRCLE_BORDER", BUTTON_SIZE);
		frame.addChild(WindowManager.getInstance().getWindow(this.getName() + "MAIL/BACK_HOLDER"));
		back.setReceiveTouchEvents(true);
		back.setInheritsAlpha(true);
		
		back.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				remove();
				gui.removeMail(backMail);
				gui.addMail(backMail, true);
			}
		});
		
		InputManager.getInstance().sort();
		frame.setAlpha(0);
	}

	public String getTooltipText() {
		return "NO TOOLTIP DEFINED";
	}
	
	@Override
	public void onUpdate(float time) {
		super.onUpdate(time);
		passedTime += time * World.getInstance().getPlaySpeed();
		if (passedTime >= MAX_TIME) {
			hide();
			gui.removeMail(this);
		}
	}

}
