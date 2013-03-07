package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;

import com.tyrlib2.game.DirectMovement;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.input.ITouchListener;
import com.tyrlib2.math.Rectangle;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

/** Basic window class
 * 
 * @author Sascha
 *
 */

public class Window implements IUpdateable, ITouchListener {
	
	/** The name of this window **/
	private String name;
	
	/** The child windows of this window **/
	private List<Window> children;
	
	/** The scene node of this window for integration into the scene graph **/
	private SceneNode node;
	
	/** The size of this window **/
	private Vector2 size;
	
	/** Takes care of moving this window **/
	private DirectMovement movement;
	
	/** Has this window been destroyed? **/
	protected boolean destroyed;
	
	/** Is this window receiving touch events **/
	protected boolean receiveTouchEvents;
	
	/** Is this window allowing windows behind it to receive touch events? **/
	protected boolean passTouchEventsThrough;
	
	/** Is this window visible **/
	protected boolean visible;
	
	/** Is the user currently inside of the window? **/
	protected boolean touchInWindow;
	
	private enum BLEND_STATE {
		IDLE,
		FADE_IN,
		FADE_OUT
	}
	
	/** The state of blending **/
	private BLEND_STATE blendState;

	/** The speed at which this window is blending towards the target alpha value **/
	private float blendSpeed;
	
	/** The target alpha value for a blending operation **/
	private float targetAlpha;
	
	public Window() {
		children = new ArrayList<Window>();
		blendState = BLEND_STATE.IDLE;
	}
	
	/**
	 * Fade the window out until minAlpha has been reached
	 * @param minAlpha	The target alpha value
	 * @param time		Time until minAlpha will be reached
	 */
	
	public void fadeOut(float minAlpha, float time) {
		for (int i = 0; i < children.size(); ++i) {
			children.get(i).fadeOut(minAlpha, time);
		}
		blendState = BLEND_STATE.FADE_OUT;
		blendSpeed = (minAlpha - getAlpha()) / time;
		targetAlpha = minAlpha;
	}
	
	/**
	 * Fade the window in until maxAlpha has been reached.
	 * If the window was not visible, it will be made visible.
	 * @param maxAlpha	The target alpha value
	 * @param time		The time until maxAlpha will be reached
	 */
	
	public void fadeIn(float maxAlpha, float time) {
		for (int i = 0; i < children.size(); ++i) {
			children.get(i).fadeIn(maxAlpha, time);
		}
		blendState = BLEND_STATE.FADE_IN;
		blendSpeed = (maxAlpha - getAlpha()) / time;
		targetAlpha = maxAlpha;
		
		if (getAlpha() == 0 && targetAlpha != 0) {
			setVisible(true);
		}
	}
	
	/**
	 * Get the current blending state
	 * @return
	 */
	
	public BLEND_STATE getBlendState() {
		return blendState;
	}
	
	/**
	 * Jump to the end of current blending operations.
	 * If the end alpha was 0, then the visibility of the window will be set to false.
	 */
	
	public void endBlend() {
		setAlpha(targetAlpha);
		blendState = BLEND_STATE.IDLE;
		
		if (targetAlpha == 0) {
			setVisible(false);
		}
	}

	
	/**
	 * Add a new child window to this window
	 * @param window
	 */
	
	public void addChild(Window window) {
		children.add(window);
		window.node.detach();
		node.attachChild(window.node);
	}
	
	/**
	 * Remove a child window from this window
	 * @param window
	 */
	
	public void removeChild(Window window) {
		children.remove(window);
		node.detachChild(window.node);
	}
	
	/**
	 * Get the number of child windows
	 * @return
	 */
	
	public int getCountChildren() {
		return children.size();
	}
	
	/**
	 * Get an indexed child window
	 * @param index
	 * @return
	 */
	
	public Window getChild(int index) {
		return children.get(index);
	}
	
	/**
	 * Update all timed actions of this window
	 * @param time
	 */
	
	@Override
	public void onUpdate(float time) {
		if (blendState != BLEND_STATE.IDLE) {
			updateBlending(time);
		}
	}
	
	private void updateBlending(float time) {
		float newAlpha = getAlpha() + time * blendSpeed;
		if (		(newAlpha < targetAlpha && blendState == BLEND_STATE.FADE_IN) 
				||	(newAlpha > targetAlpha && blendState == BLEND_STATE.FADE_OUT)) {
			setAlpha(newAlpha);
		} else {
			endBlend();
		}
		
	}
	
	/**
	 * Destroy this window.
	 * All children will be destroyed recursively.
	 */
	
	protected void destroy() {
		for (int i = 0; i < children.size(); ++i) {
			WindowManager.getInstance().removeWindow(this);
			destroyed = true;
			children.get(i).destroy();
		}
	}

	/**
	 * Is this window alive?
	 * @return
	 */
	
	@Override
	public boolean isFinished() {
		return destroyed;
	}
	
	/**
	 * Get the name of the window
	 * @return
	 */
	
	public String getName() {
		return name;
	}
	
	/**
	 * Set an alpha value
	 * @param alpha
	 */
	
	public void setAlpha(float alpha) {
	}
	
	/**
	 * Get the current alpha value
	 * @return
	 */
	
	public float getAlpha() {
		return 1;
	}
	
	/**
	 * Set the position of this window relative to its parent
	 * @param pos
	 */
	
	public void setRelativePos(Vector2 pos) {
		node.setRelativePos(new Vector3(pos.x, pos.y, 0));
	}
	
	/**
	 * Get the position of this window relative to the parent
	 * @return
	 */
	
	public Vector2 getRelativePos() {
		Vector3 pos = node.getRelativePos();
		return new Vector2(pos.x, pos.y);
	}
	
	/**
	 * Resize this window
	 * @param size
	 */
	
	public void setSize(Vector2 size) {
		this.size = size;
	}
	
	/**
	 * Get the size values of this window
	 * @return
	 */
	
	public Vector2 getSize() {
		return size;
	}

	/**
	 * Get the priority of this window
	 */
	
	@Override
	public long getPriority() {
		return 0;
	}
	
	@Override
	public boolean onTouchDown(Vector2 point, MotionEvent event) {
		Vector2 pos = getRelativePos();
		if (Rectangle.pointInRectangle(pos, size, point)) {
			if (!touchInWindow) {
				onTouchEntersWindow();
			}
			onTouchDownWindow(point, event);
			return !passTouchEventsThrough;
		}
		
		return false;
	}
	
	/**
	 * The user has started a touch down interaction with this window
	 * @param point
	 * @param event
	 */
	
	protected void onTouchDownWindow(Vector2 point, MotionEvent event) {
	}
	
	@Override
	public boolean onTouchUp(Vector2 point, MotionEvent event) {
		Vector2 pos = getRelativePos();
		if (Rectangle.pointInRectangle(pos, size, point)) {
			if (touchInWindow) {
				onTouchLeavesWindow();
			}
			onTouchUpWindow(point, event);
			return !passTouchEventsThrough;
		}
		
		return false;
	}
	
	/**
	 * The user has started a touch up interaction with this window
	 * @param point
	 * @param event
	 */
	
	protected void onTouchUpWindow(Vector2 point, MotionEvent event) {
	}

	
	
	/**
	 * React to a touch move event
	 */
	
	@Override
	public boolean onTouchMove(Vector2 point, MotionEvent event) {
		Vector2 pos = getRelativePos();
		if (Rectangle.pointInRectangle(pos, size, point)) {
			if (!touchInWindow) {
				onTouchEntersWindow();
			}
			onTouchMoveWindow(point, event);
			return !passTouchEventsThrough;
		} else {
			if (touchInWindow) {
				onTouchLeavesWindow();
			}
		}
		
		return false;
	}
	
	/**
	 * The user has started a touch move interaction with this window
	 * @param point
	 * @param event
	 */
	
	protected void onTouchMoveWindow(Vector2 point, MotionEvent event) {
	}
	
	/**
	 * The user has started touching this window
	 */
	
	protected void onTouchEntersWindow() {
		touchInWindow = true;
	}
	
	/**
	 * The user has stopped touching this window
	 */

	protected void onTouchLeavesWindow() {
		touchInWindow = false;
	}

	/**
	 * Is this window currently touched by the user?
	 * @return
	 */
	
	public boolean isBeingTouched() {
		return touchInWindow;
	}
	
	/**
	 * Is this window enabled?
	 */
	
	@Override
	public boolean isEnabled() {
		return receiveTouchEvents && visible;
	}
	
	/**
	 * Is this window receiving touch events?
	 * @return
	 */
	
	public boolean isReceivingTouchEvents() {
		return receiveTouchEvents;
	}
	
	/**
	 * Set if this window is supposed to receive touch events
	 * @param receiveTouchEvents
	 */
	
	public void setReceiveTouchEvents(boolean receiveTouchEvents) {
		this.receiveTouchEvents = receiveTouchEvents;
	}
	
	/**
	 * Does this window let touch events through?
	 * Windows behind this one will receive the touch events.
	 * @return
	 */
	
	public boolean isPassingTouchEventsThrough() {
		return passTouchEventsThrough;
	}
	
	/**
	 * Set whether windows behind this window will receive touch events
	 * @param passTouchEventsThrough
	 */
	
	public void setPassTouchEventsThrough(boolean passTouchEventsThrough) {
		this.passTouchEventsThrough = passTouchEventsThrough;
	}
	
	/**
	 * Set the visibility of this window
	 * @param visible
	 */
	
	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			this.visible = visible;
			for (int i = 0; i < children.size(); ++i) {
				children.get(i).setVisible(visible);
			}
		}
	}
	
	/**
	 * Is this window visible?
	 * @return
	 */
	
	public boolean getVisible() {
		return visible;
	}
	
}
