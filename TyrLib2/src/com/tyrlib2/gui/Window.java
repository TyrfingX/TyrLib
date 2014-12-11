package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.IMoveListener;
import com.tyrlib2.input.ITouchListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Rectangle;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.DirectMovement;
import com.tyrlib2.movement.Speed;
import com.tyrlib2.movement.TargetPoint;
import com.tyrlib2.util.IPrioritizable;

/** Basic window class
 * 
 * @author Sascha
 *
 */

public class Window implements IUpdateable, ITouchListener, IRenderable, IPrioritizable, IMoveListener {
	
	/** The name of this window **/
	private String name;
	
	/** The child windows of this window **/
	private List<Window> children;
	
	/** The size of this window **/
	private Vector2 size;
	
	/** Factors to relax size for UI interaction **/
	private Vector2 sizeRelaxation = new Vector2(1,1);
	
	/** Size relaxed by sizeRelaxation **/
	protected Vector2 relaxedSize = new Vector2(0,0);
	
	/** Takes care of moving this window **/
	private DirectMovement movement;
	
	/** Movement speed of this window **/
	private Speed speed;
	
	/** Integration into the scene graph **/
	protected SceneNode node;
	
	/** Has this window been destroyed? **/
	protected boolean destroyed = false;
	
	/** Is this window receiving touch events **/
	protected boolean receiveTouchEvents = false;
	
	/** Is this window allowing windows behind it to receive touch events? **/
	protected boolean passTouchEventsThrough = false;
	
	/** Is this window visible **/
	protected boolean visible = true;
	
	/** Is the user currently inside of the window? **/
	protected boolean touchInWindow = false;
	
	/** Renderable components of this window **/
	protected List<IRenderable> components;
	
	/** How "High" is this window in the display hirachy? **/
	protected long priority;
	
	protected long tmpPrio;
	
	private Map<WindowEventType, List<IEventListener>> eventListeners;
	
	private int countComponents;
	
	protected boolean inheritsAlpha;
	
	private Viewport viewport;
	private Vector3 absolutePosVector;
	
	public enum BLEND_STATE {
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
	
	/** Data attached to this window **/
	private Map<String, Object> data;
	
	/** The parent window **/
	private Window parent;
	
	/** Maximum alpha this window will receive when blending **/
	private float maxAlpha = 1;
	
	/** if this window is moving **/
	private boolean moving;
	
	/** if this window is being dragged by the user **/
	private boolean drag;
	
	/** does this window inherit fade out/ins from parent? **/
	private boolean inheritsFade = true;
	
	/** is he window being hovered by the mouse **/
	protected boolean hovered;

	private int insertionID;
	
	private Window() {
		node = new SceneNode();
		children = new ArrayList<Window>();
		components = new ArrayList<IRenderable>();
		blendState = BLEND_STATE.IDLE;
		speed = new Speed(0);
		movement = new DirectMovement(node, speed);
		priority = WindowManager.GUI_BASE_PRIORITY;
		eventListeners = new HashMap<WindowEventType, List<IEventListener>>();
		data = new HashMap<String, Object>();
		
		viewport = SceneManager.getInstance().getViewport();
		absolutePosVector = node.getCachedAbsolutePosVector();
		
		this.setRelativePos(new Vector2(-1,-1));
	}
	
	protected Window(String name) {
		this();
		this.name = name;
	}
	
	public Window(String name, Vector2 size) {
		this(name);
		this.setSize(size);
	}
	
	/**
	 * Fade the window out until minAlpha has been reached
	 * @param minAlpha	The target alpha value
	 * @param time		Time until minAlpha will be reached
	 */
	
	public void fadeOut(float minAlpha, float time) {
		for (int i = 0; i < children.size(); ++i) {
			if (children.get(i).inheritsFade) {
				children.get(i).fadeOut(minAlpha, time);
			}
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
			if (children.get(i).inheritsFade) {
				children.get(i).fadeIn(maxAlpha, time);
			}
		}
		
		maxAlpha = Math.min(this.maxAlpha, maxAlpha);
		
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
		
		if (blendState == BLEND_STATE.FADE_IN) {
			fireEvent(new WindowEvent(this, WindowEventType.FADE_IN_FINISHED));
		} else if (blendState == BLEND_STATE.FADE_OUT) {
			fireEvent(new WindowEvent(this, WindowEventType.FADE_OUT_FINISHED));
		}
		
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
		window.parent = this;
		
		window.setVisible(this.isVisible());
		window.setAlpha(this.getAlpha());
		
		window.setPriority(priority+children.size());
	}
	
	/**
	 * Remove a child window from this window
	 * @param window
	 */
	
	public void removeChild(Window window) {
		children.remove(window);
		node.detachChild(window.node);
		
		WindowManager.getInstance().getRootNode().attachChild(window.node);
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
		
		movement.onUpdate(time);
		
		if (movement.isFinished()) {
			if (moving) {
				moving = false;
				fireEvent(new WindowEvent(this, WindowEventType.MOVEMENT_FINISHED));
			}
		} else {
			moving = true;
		}
		
		if (drag) {
			setRelativePos(InputManager.getInstance().getLastTouch());
		}
		
	}
	
	public void disableTimeUpdates() {
		WindowManager.getInstance().updater.removeItem(this);
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
		if (!destroyed) {
			
			destroyed = true;
			
			for (int i = 0; i < children.size(); ++i) {
				children.get(i).destroy();
			}
			
			this.setReceiveTouchEvents(false);
			WindowManager.getInstance().removeWindow(this);
			
			if (parent != null && !parent.destroyed) {
				parent.removeChild(this);
			}
			
			fireEvent(new WindowEvent(this, WindowEventType.DESTROYED));
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
	
	public void setInheritsAlpha(boolean inheritsAlpha) {
		this.inheritsAlpha = inheritsAlpha;
	}
	
	/**
	 * Set an alpha value
	 * @param alpha
	 */
	
	public void setAlpha(float alpha) {
		for (int i = 0; i < children.size(); ++i) {
			if (children.get(i).inheritsAlpha) {
				float childAlpha = Math.min(children.get(i).maxAlpha, alpha);
				children.get(i).setAlpha(childAlpha);
			}
		}
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
		Viewport viewport = SceneManager.getInstance().getViewport();
		float x = pos.x*viewport.getWidth();
		float y = -pos.y*viewport.getHeight();
		node.setRelativePos(new Vector3(x, y, 0));
	}
	
	/**
	 * Get the position of this window relative to the parent
	 * @return
	 */
	
	public Vector2 getRelativePos() {
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector3 pos = node.getRelativePos();
		return new Vector2(pos.x/viewport.getWidth(), -pos.y/viewport.getHeight());
	}
	
	/**
	 * Get the absolute position of this window relative to its parent from the last frame
	 */
	
	public Vector2 getAbsolutePos() {
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector3 pos = node.getCachedAbsolutePosVector();
		return new Vector2(pos.x/viewport.getWidth(), pos.y/viewport.getHeight());
	}
	
	public float getAbsolutePosX() {
		return absolutePosVector.x/viewport.getWidth();
	}
	
	public float getAbsolutePosY() {
		return absolutePosVector.y/viewport.getHeight();
	}
	
	/**
	 * Resize this window
	 * @param size
	 */
	
	public void setSize(Vector2 size) {
		this.size = size;
		relaxedSize.x = size.x * sizeRelaxation.x;
		relaxedSize.y = size.y * sizeRelaxation.y;
	}
	
	public void setSizeRelaxation(Vector2 relaxation) {
		sizeRelaxation = relaxation;
		relaxedSize.x = size.x * sizeRelaxation.x;
		relaxedSize.y = size.y * sizeRelaxation.y;
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
		return priority;
	}
	
	public void setPriority(long priority) {
		this.priority = priority;
		
		for (int i = 0; i < children.size(); ++i) {
			children.get(i).setPriority(priority+i+1);
		}
		
		WindowManager.getInstance().notifyResort();
	}
	
	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		point = new Vector2(point.x, 1-point.y);
		Vector2 pos = getAbsolutePos();
		pos.x -= size.x * (sizeRelaxation.x-1) /2;
		pos.y += size.y * (sizeRelaxation.y-1) /2;
		if (Rectangle.pointInRectangle(pos, relaxedSize, point)) {
			if (!touchInWindow) {
				onTouchEntersWindow(point);
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
	
	protected void onTouchDownWindow(Vector2 point, IMotionEvent event) {
		WindowEvent windowEvent = new WindowEvent(this, WindowEventType.TOUCH_DOWN);
		windowEvent.setParam("POINT", point);
		windowEvent.setParam("MOTIONEVENT", event);
		fireEvent(windowEvent);
	}
	
	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		point = new Vector2(point.x, 1-point.y);
		Vector2 pos = getAbsolutePos();
		pos.x -= size.x * (sizeRelaxation.x-1) /2;
		pos.y += size.y * (sizeRelaxation.y-1) /2;
		if (drag || Rectangle.pointInRectangle(pos, relaxedSize, point)) {
			onTouchUpWindow(point, event);
			if (touchInWindow) {
				onTouchLeavesWindow();
			}
			return !passTouchEventsThrough;
		}
		
		return false;
	}
	
	/**
	 * The user has started a touch up interaction with this window
	 * @param point
	 * @param event
	 */
	
	protected void onTouchUpWindow(Vector2 point, IMotionEvent event) {
		WindowEvent windowEvent = new WindowEvent(this, WindowEventType.TOUCH_UP);
		windowEvent.setParam("POINT", point);
		windowEvent.setParam("MOTIONEVENT", event);
		fireEvent(windowEvent);
		
		if (touchInWindow) {
			windowEvent = new WindowEvent(this, WindowEventType.TOUCH);
			windowEvent.setParam("POINT", point);
			windowEvent.setParam("MOTIONEVENT", event);
			fireEvent(windowEvent);
		}
	}

	
	
	/**
	 * React to a touch move event
	 */
	
	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		point = new Vector2(point.x, 1-point.y);
		Vector2 pos = getAbsolutePos();
		if (Rectangle.pointInRectangle(pos, relaxedSize, point)) {
			if (!touchInWindow) {
				onTouchEntersWindow(point);
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
	
	protected void onTouchMoveWindow(Vector2 point, IMotionEvent event) {
		WindowEvent windowEvent = new WindowEvent(this, WindowEventType.TOUCH_MOVES);
		windowEvent.setParam("POINT", point);
		windowEvent.setParam("MOTIONEVENT", event);
		fireEvent(windowEvent);
	}
	
	/**
	 * The user has started touching this window
	 * @param point 
	 */
	
	protected void onTouchEntersWindow(Vector2 point) {
		touchInWindow = true;
		WindowEvent e = new WindowEvent(this, WindowEventType.TOUCH_ENTERS);
		e.setParam("POINT", point);
		fireEvent(e);
	}
	
	/**
	 * The user has stopped touching this window
	 */

	protected void onTouchLeavesWindow() {
		touchInWindow = false;
		fireEvent(new WindowEvent(this, WindowEventType.TOUCH_LEAVES));
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
		return receiveTouchEvents && visible && getAlpha() > 0;
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
		
		if (receiveTouchEvents) {
			if (!InputManager.getInstance().isAdded(this)) {
				InputManager.getInstance().addTouchListener(this);
				InputManager.getInstance().addMoveListener(this);
			}
		} else {
			InputManager.getInstance().removeTouchListener(this);
			InputManager.getInstance().removeMoveListener(this);
		}
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
	
	public boolean isVisible() {
		return visible;
	}
	
	/** Move to a specified point within the alloted time
	 * 	Previous movement operations will be aborted;
	 * @param point
	 * @param time
	 */
	
	public void moveTo(Vector2 point, float time) {
		movement.clear();
		Vector3 pos = node.getRelativePos();
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector3 newPos = new Vector3(point.x*viewport.getWidth(), -point.y*viewport.getHeight(), pos.z);
		TargetPoint target = new TargetPoint(newPos);
		movement.addTarget(target);
		
		float distance = pos.vectorTo(newPos).length();
		speed.speed = distance / time;
		
		moving = true;
	}

	/**
	 * Translates the window by the given point
	 * @param point
	 * @param time
	 */
	
	public void moveBy(Vector2 point, float time) {
		Vector3 pos = node.getRelativePos();
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 target2 = new Vector2(pos.x/viewport.getWidth()+point.x, -pos.y/viewport.getHeight()+point.y);
		moveTo(target2, time);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		for (int i = 0; i < countComponents; ++i) {
			components.get(i).render(vpMatrix);
		}
	}
	
	public void addComponent(IRenderable renderable) {
		components.add(renderable);
		if (renderable instanceof SceneObject) {
			node.attachSceneObject((SceneObject) renderable);
		}
		countComponents++;
	}
	
	public void addComponent(IRenderable renderable, int position) {
		components.add(position, renderable);
		if (renderable instanceof SceneObject) {
			node.attachSceneObject((SceneObject) renderable);
		}
		countComponents++;
	}
	
	public void addEventListener(WindowEventType event, IEventListener listener) {
		List<IEventListener> listeners = eventListeners.get(event);
		if (listeners == null) {
			listeners = new ArrayList<IEventListener>();
			eventListeners.put(event, listeners);
		}
		
		listeners.add(listener);
	}
	
	public void removeEventListener(WindowEventType event, IEventListener listener) {
		List<IEventListener> listeners = eventListeners.get(event);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
	public void removeEventListeners(WindowEventType event) {
		List<IEventListener> listeners = eventListeners.get(event);
		if (listeners != null) {
			listeners.clear();
		}
	}
	
	public void fireEvent(WindowEvent event) {
		List<IEventListener> listeners = eventListeners.get(event.getType());
		if (listeners != null) {
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onEvent(event);
			}
		}
	}
	
	public void setData(String name, Object value) {
		data.put(name, value);
	}
	
	public Object getData(String name) {
		return data.get(name);
	}
	
	public Window getParent() {
		return parent;
	}
	
	/** An alpha treshhold which will never be overstepped during blending processes event if
	 * the target alpha is higher
	 * @param maxAlpha
	 */
	
	public void setMaxAlpha(float maxAlpha) {
		this.maxAlpha = maxAlpha;
	}
	
	public float getMaxAlpha() {
		return maxAlpha;
	}
	
	public void setDrag(boolean drag) {
		if (drag) {
			if (parent != null) {
				parent.removeChild(this);
			}
		} 

		setPriority(WindowManager.GUI_OVERLAY_PRIORITY/2);
		
		InputManager.getInstance().sort();
		
		this.drag = drag;
	}
	
	public boolean getDrag() {
		return drag;
	}
	
	public boolean overlaps(Window window) {
		Vector2 pos1 = getAbsolutePos();
		pos1.y = 1 - pos1.y;
		Vector2 pos2 = window.getAbsolutePos();
		pos2.y = 1 - pos2.y;
		return Rectangle.overlap(pos1, getSize().add(pos1), pos2, window.getSize().add(pos2));
	}
	
	public void setRecuresiveReceiveTouchEvents(boolean receiveTouchEvents) {
		InputManager.getInstance().removeTouchListener(this);
		this.receiveTouchEvents = receiveTouchEvents;
		
		for (int i = 0; i < children.size(); ++i) {
			children.get(i).setRecuresiveReceiveTouchEvents(receiveTouchEvents);
		}
	}
	
	public void setInheritsFade(boolean state) {
		this.inheritsFade = state;
	}
	
	public boolean isMoving() {
		return !movement.isFinished();
	}
	
	@Override
	public boolean onMove(Vector2 point) {
		point = new Vector2(point.x, 1-point.y);
		Vector2 pos = getAbsolutePos();
		if (Rectangle.pointInRectangle(pos, relaxedSize, point)) {
			if (!hovered) {
				WindowEvent e = new WindowEvent(this, WindowEventType.MOUSE_ENTERS);
				e.setParam("POINT", point);
				fireEvent(e);
			}
			hovered = true;
		} else {
			if (hovered) {
				WindowEvent e = new WindowEvent(this, WindowEventType.MOUSE_LEAVES);
				e.setParam("POINT", point);
				fireEvent(e);
			}
			hovered = false;
		}
		
		return false;
	}

	@Override
	public boolean onEnterRenderWindow() {
		return false;
	}

	@Override
	public boolean onLeaveRenderWindow() {
		return false;
	}

	@Override
	public boolean onRenderWindowLoseFocus() {
		return false;
	}

	@Override
	public boolean onRenderWindowGainFocus() {
		return false;
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
