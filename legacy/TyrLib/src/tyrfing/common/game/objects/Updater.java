package tyrfing.common.game.objects;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tyrfing.common.render.IFrameListener;

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
	
	@Override
	public void onUpdate(float time) {
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

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onClearRenderer() {
		// TODO Auto-generated method stub
		
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
}
