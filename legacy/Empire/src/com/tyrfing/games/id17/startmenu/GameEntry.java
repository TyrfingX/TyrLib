package com.tyrfing.games.id17.startmenu;

import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ItemListEntry;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class GameEntry extends ItemListEntry{
	private boolean click;
	private Vector2 clickPoint;
	protected Rectangle2 rect;
	
	public static final float MAX_DISTANCE = 0.01f;
	
	public static final Color NORMAL = new Color(209/255f, 172/255f, 112/255f, 0.8f);
	public static final Color HIGHLIGHT = new Color(239/255f, 202/255f, 162/255f, 1f);
	
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(0.05f, ScaleDirection.Y, 0);
	public static final float SIZE_X = 0.53f;
	
	public static final ScaledVector2 NAME_POS = new ScaledVector2(0.01f, 0.0f, 2);
	
	private float alpha;
	
	private boolean enabled;
	protected boolean activated;
	
	private Label nameLabel;
	private Label playersLabel;
	private final int id;
	
	private MatchMakerUI ui;
	
	private String address;
	
	public GameEntry(String name, int id, MatchMakerUI ui, String address) {
		super(name, new Vector2(SIZE_X, SIZE_Y.get()));
		
		this.ui = ui;
		this.id = id;
		this.enabled = true;
		
		this.address = address;
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		rect = new Rectangle2(new Vector2(SIZE_X * viewport.getWidth(), SIZE_Y.get() * viewport.getHeight()), NORMAL.copy());
		addComponent(rect);
		
		this.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					rect.setColor( NORMAL.copy() );
					click = false;
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (enabled) {
					click = true;
					rect.setColor( HIGHLIGHT.copy() );
				}
				clickPoint = new Vector2((Vector2) event.getParam("POINT"));
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					click = false;
					onClick();
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_MOVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					Vector2 current = new Vector2((Vector2) event.getParam("POINT"));
					if (current.vectorTo(clickPoint).length() >= MAX_DISTANCE) {
						click = false;
						rect.setColor( NORMAL.copy() );
					}
				}
			}
		});
		
		this.setPassTouchEventsThrough(true);
		this.setReceiveTouchEvents(true);
		
		nameLabel = (Label) WindowManager.getInstance().createLabel("MatchMaker/Games/Name" + name, NAME_POS, "DefaultName");
		nameLabel.setColor(Color.BLACK);
		this.addChild(nameLabel);
		
		playersLabel = (Label) WindowManager.getInstance().createLabel("MatchMaker/Games/Players" + name, new Vector2(SIZE_X - NAME_POS.get().x, NAME_POS.get().y), "Unknown");
		playersLabel.setColor(Color.BLACK);
		playersLabel.setAlignment(ALIGNMENT.RIGHT);
		this.addChild(playersLabel);
	}
	
	@Override
	public float getAlpha() {
		return alpha;
	}
	
	@Override
	public void setAlpha(float alpha) {
		
		alpha = Math.min(1, alpha);
		alpha = Math.max(0, alpha);
		
		this.alpha = alpha;
		
		if (rect != null) {
			if (activated) {
				rect.setAlpha(HIGHLIGHT.a * alpha);
			} else {
				
			}
		}
		
		nameLabel.setAlpha(alpha);
		playersLabel.setAlpha(alpha);
		
		super.setAlpha(alpha);
	}
	
	
	public void onClick() {
		if (!activated) {
			ui.deselectAll();
			ui.setSelected(this);
			highlight();
		} else {
			if (ui.isVisible()) {
				ui.join(address);
			}
		}
		
		click = false;
	}
	
	public void unhighlight() {
		if (activated) {
			activated = false;
			rect.setColor( NORMAL );
		}
	}
	
	public void highlight() {
		rect.setColor( HIGHLIGHT );
		activated = true;
	}
	
	public int getID() {
		return id;
	}

	public void setCountPlayers(int countPlayers, int countMaxPlayers) {
		playersLabel.setText(countPlayers + "/" + countMaxPlayers);
	}
	
	public String getAddress() {
		return address;
	}
	
}
