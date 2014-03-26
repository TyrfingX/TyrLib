package com.tyrlib2.game;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.IFrameListener;

public class Updater implements IFrameListener {
	
	private boolean pause;
	private List<IUpdateable> queue;
	
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
		}
	}

	public void removeItem(IUpdateable item)
	{
		queue.remove(item);
	}
	
	public void clear()
	{
		queue.clear();
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
		for (int i = 0; i < queue.size() && !pause; ++i) {
			IUpdateable item = queue.get(i);
			if (item.isFinished()) {
				removeItem(i);
				--i;
			} else {
				item.onUpdate(time);
			}
		}
	}
	
	private void removeItem(int i) {
		if (i < queue.size()) {
			queue.set(i, queue.get(queue.size() - 1));
			queue.remove(queue.size() - 1);
		}
	}
}
