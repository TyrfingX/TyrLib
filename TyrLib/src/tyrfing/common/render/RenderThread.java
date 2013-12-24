package tyrfing.common.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tyrfing.common.game.GameThread;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.struct.PriorityComparator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.SurfaceHolder;

public class RenderThread extends GameThread {
	private SurfaceHolder surface;
	private List<Renderable> toRender;
	private Bitmap bg;
	private BlockingQueue<IFrameListener> listeners;
	private boolean pause;
	private static final PriorityComparator comparator = new PriorityComparator();
	
	public Handler handler = new Handler();
	
	public RenderThread(SurfaceHolder surface)
	{
		super();
		this.surface = surface;
		surface.setKeepScreenOn(true);
		
		toRender = new ArrayList<Renderable>();
		listeners = new LinkedBlockingQueue<IFrameListener>();
	}
	
	public RenderThread copy()
	{
		RenderThread thread = new RenderThread(surface);
		thread.bg = bg;
		thread.toRender = toRender;
		thread.listeners = listeners;
		return thread;
	}
	
	public void addRenderable(Renderable renderable)
	{
		
		if (renderable != null) {
			synchronized(toRender)
			{
				toRender.add(renderable);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public void removeRenderable(Renderable renderable)
	{
		synchronized(toRender)
		{
			toRender.remove(renderable);
		}
	}
	
	public void dropAllFrameListeners()
	{
		listeners.clear();
	}

	public void setBg(Bitmap bg)
	{
		this.bg = bg;
	}
	
	public void clear()
	{
		toRender.clear();
		for (IFrameListener listener : listeners)
		{
			listener.onClearRenderer();
		}
		bg = null;
	}
	
	@Override
	public void onUpdate(float time) {
		if (!pause)
		{
			this.render(time);
			this.updateListeners(time);
		}
	}
	
	public void render(float time)
	{
	
		
		Canvas target = surface.lockCanvas(null);
		
		if (target != null) {
			
			if (bg == null) {
				target.drawColor(Color.BLACK);
			} else {
				target.drawBitmap(bg, 0, 0, null);
			}
			
			synchronized(toRender)
			{
				Collections.sort(toRender, comparator);
				for (Renderable renderable : toRender)
				{
					if (renderable.getVisible())
						renderable.onRender(target, time);
				}
			}
			surface.unlockCanvasAndPost(target);		
		}

	}
	
	public void updateListeners(float time)
	{
		Iterator<IFrameListener> listenerItr = listeners.iterator();
		while(listenerItr.hasNext())
		{
			IFrameListener frameListener = listenerItr.next();
			if (frameListener != null) {
				if (!frameListener.isFinished())
					frameListener.onUpdate(time);
				else
					listenerItr.remove();
			}
		}		
	}

	@Override
	public boolean isFinished() {
		return this.run;
	}
	
	public void addFrameListener(IFrameListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeFrameListener(IFrameListener listener)
	{
		listeners.remove(listener);
	}
	
	
	public void pause()
	{
		pause = true;
	}
	
	public void unPause()
	{
		pause = false;
	}
}
