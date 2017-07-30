package com.tyrlib2.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.IFrameListener;

public class Updater implements IFrameListener, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6660445587292999876L;
	private boolean pause;
	private List<IUpdateable> queue;
	private int countItems;
	private float skipTime = 999;
	
	public Updater(List<IUpdateable> queue) {
		this.queue = queue;
	}
	
	public Updater() {
		this.queue = new ArrayList<IUpdateable>();
	}

	public void addItem(IUpdateable item)
	{
		if (item != null) {
			queue.add(item);
			countItems++;
		}
	}

	public void removeItem(IUpdateable item)
	{
		if (queue.remove(item)) {
			countItems--;
		}
	}
	
	public void clear()
	{
		queue.clear();
		countItems = 0;
	}
	
	public boolean hasItem(IUpdateable item)
	{
		for (IUpdateable i : queue)
		{
			if (i == item)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasItems() {
		return !queue.isEmpty();
	}
	
	public void pause()
	{
		pause = true;
	}
	
	public void unPause()
	{
		pause = false;
	}
	
	public boolean isPaused()
	{
		return pause;
	}

	@Override
	public void onSurfaceCreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFrameRendered(float time) {
		for (int i = 0; i < countItems && !pause; ++i) {
			IUpdateable item = queue.get(i);
			if (item.isFinished()) {
				removeItem(i);
				--i;
			} else {
				if (time < skipTime) item.onUpdate(time);
			}
		}
	}
	
	public void setSkipTime(float skipTime) {
		this.skipTime = skipTime;
	}
	
	private void removeItem(int i) {
		if (i < countItems) {
			queue.set(i, queue.get(countItems - 1));
			queue.remove(countItems - 1);
			countItems--;
		}
	}
}
