package tyrfing.common.ui;


import java.util.List;
import java.util.Vector;

import tyrfing.common.game.objects.IMovementListener;
import tyrfing.common.game.objects.IUpdateable;
import tyrfing.common.game.objects.Movement;
import tyrfing.common.input.TouchListener;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.struct.Node;

public abstract class Window implements IUpdateable, TouchListener {

	protected boolean visible;
	protected float w;
	protected float h;
	protected Node node;
	private long priority;
	
	protected List<Renderable> components;
	protected List<Window> childWindows;
	protected List<ClickListener> clickListener;
	protected List<TouchEnterListener> touchEnterListener;
	protected Movement movement;
	protected Window parent;
	protected boolean touched;
	
	private String name;
	
	protected boolean enabled;
	
	public Window(String name, float x, float y, float w, float h) {
		this.w = w;
		this.h = h;
		node = new Node(x, y);
		components = new Vector<Renderable>();
		childWindows = new Vector<Window>();
		clickListener = new Vector<ClickListener>();
		touchEnterListener = new Vector<TouchEnterListener>();
		this.name = name;
		this.enabled = true;
		this.visible = true;
		movement = new Movement(node, 0);
	}
	
	public void blendIn(float time)
	{
		for (Renderable r : components)
		{
			r.blendIn(new Vector2(0,0), time);
		}
		
		for (Window window : childWindows)
		{
			window.blendIn(time);
		}
		
		this.setVisible(true);
	}

	public void fadeOut(float time)
	{
		for (Renderable r : components)
		{
			r.fadeOut(new Vector2(0,0), time);
		}
		
		for (Window window : childWindows)
		{
			window.fadeOut(time);
		}
	}
	
	public Window getParent()
	{
		return parent;
	}
	
	public String getName()
	{
		return name;
	}
	
	public float getX()
	{
		return node.getX();
	}
	
	public float getY()
	{
		return node.getY();
	}
	
	public float getHeight()
	{
		return h;
	}
	
	public float getWidth()
	{
		return w;
	}
	
	public void setHeight(float h)
	{
		this.h = h;
	}
	
	public void setWidth(float w)
	{
		this.w = w;
	}
	
	public void setVisible(boolean visible)
	{
		if (this.visible != visible)
		{
			
			for (Renderable r : components)
			{
				r.setVisible(visible);
			}
			
			for (Window window : childWindows)
			{
				window.setVisible(visible);
			}
		}
		this.visible = visible;
	}
	
	public boolean getVisible()
	{
		return visible;
	}
	
	public void show()
	{
		this.setVisible(true);
	}
	
	public void hide()
	{
		this.setVisible(false);
	}
	
	
	public void moveTo(Vector2 dest, float time)
	{
		movement.clearPath();
		movement.addPoint(dest);
		movement.setSpeed(dest.sub(node.getAbsolutePos()).length()/time);
		if (!WindowManager.windowUpdater.hasItem(this)) WindowManager.windowUpdater.addItem(this);
	}
	
	public long setPriority(long priority)
	{
		
		this.priority = priority;
		
		for (Renderable r : components)
		{
			r.setPriority(priority++);
		}
		
		long res = priority;
		
		for (Window window : childWindows)
		{
			res = Math.max(res, window.setPriority(priority + 1));
		}
		
		return res;
	}
	
	public long getPriority()
	{
		return this.priority;
	}
	
	public void addChild(Window window)
	{
		window.parent = this;
		window.node.setParent(node);
		childWindows.add(window);
		window.setPriority(this.priority + 1 + childWindows.size());
		window.setVisible(this.getVisible());
	}
	
	public void removeChild(Window window)
	{
		window.parent = null;
		childWindows.remove(window);
	}
	
	public boolean isPointInWindow(Vector2 point)
	{
		if (point.x >= node.getX() && point.x <= node.getX() + w)
		{
			if (point.y >= node.getY() && point.y <= node.getY() + h)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(Vector2 point) {
		boolean inWindow = this.isPointInWindow(point);
		if (enabled && inWindow) {
			touched = true;
			this.onTouchEnters(new Event(this));
		}
		return inWindow;
	}

	@Override
	public boolean onTouchUp(Vector2 point) {
		
		if (touched) {
			this.onTouchLeaves(new Event(this));
		}
		
		touched = false;
		
		if (this.enabled)
		{
			if (this.isPointInWindow(point))
			{
				this.evokeClick(new Event(this));
				return true;
			} 
		
		}
		
		return false;
	
	}

	@Override
	public boolean onTouchMove(Vector2 point) {
		boolean inWindow = this.isPointInWindow(point);
		if (enabled) {
			if (inWindow && !touched) {
				touched = true;
				this.onTouchEnters(new Event(this));
			} else if (!inWindow && touched) {
				touched = false;
				this.onTouchLeaves(new Event(this));
			}
		}
		return inWindow;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void addClickListener(ClickListener listener)
	{
		this.clickListener.add(listener);
	}
	
	public void addTouchEnterListener(TouchEnterListener listener) {
		this.touchEnterListener.add(listener);
	}
	
	public void removeClickListener(ClickListener listener)
	{
		this.clickListener.remove(listener);
	}
	
	public void removeTouchEnterListener(TouchEnterListener listener) {
		this.touchEnterListener.remove(listener);
	}
	
	protected void evokeClick(Event event)
	{
		for (ClickListener listener : clickListener)
		{
			listener.onClick(event);
		}
	}
	
	protected void onTouchEnters(Event event) {
		for (TouchEnterListener listener : touchEnterListener)
		{
			listener.onEnter(event);
		}
	}

	protected void onTouchLeaves(Event event) {
		for (TouchEnterListener listener : touchEnterListener)
		{
			listener.onLeave(event);
		}
	}
	
	public void destroy()
	{
		
		this.enabled = false;
		
		for (Renderable r : components)
		{
			r.setVisible(false);
			SceneManager.RENDER_THREAD.removeRenderable(r);
		}
		
		clickListener.clear();
		
		for (Window child : childWindows)
		{
			WindowManager.destroyWindow(child);
		}

		WindowManager.removeWindow(this);
		
	}
	
	public void disable()
	{
		enabled = false;
	}
	
	public void enable()
	{
		enabled = true;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public void onUpdate(float time) {
		if (!movement.isFinished())
		{
			movement.onUpdate(time);
		}
	}

	@Override
	public boolean isFinished() {
		return movement.isFinished();
	}
	
	public void addMovementListener(IMovementListener movementListener)
	{
		movement.addMovementListener(movementListener);
	}
	
	public IMovementListener getMovementListener(int id)
	{
		return movement.getMovementListener(id);
	}

}
