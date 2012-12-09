package com.tyrli2.input;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.renderer.IFrameListener;

public class Updater implements IFrameListener {
	
	private boolean pause;
	private BlockingQueue<IUpdateable> queue;
	
	public Updater(BlockingQueue<IUpdateable> queue) {
		this.queue = queue;
	}
	
	public Updater() {
		this.queue = new LinkedBlockingQueue<IUpdateable>();
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
		Iterator<IUpdateable> itr = queue.iterator();
		while (itr.hasNext() && !pause)
		{
			IUpdateable item = itr.next();
			if (item != null) {
				item.onUpdate(time);
				if (item.isFinished())
					itr.remove();
			} else {
				itr.remove();
			}
		}
	}
}
