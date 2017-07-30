package tyrfing.common.game;

import java.util.ArrayList;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import tyrfing.common.render.RenderThread;
import tyrfing.common.render.SceneManager;

public abstract class Game2D extends BaseGame {
	
	private static View view;
	
	public Game2D(Context context) {
		super(context);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		init();
	}
	
	public void init()
	{
		view = this;
		SceneManager.init(new RenderThread(this.getHolder()));
	}

	public void startThreads()
	{
		if (SceneManager.RENDER_THREAD.getState() == Thread.State.TERMINATED)
		{   
			RenderThread renderThread = SceneManager.RENDER_THREAD.copy();
			SceneManager.init(renderThread);
		    SceneManager.RENDER_THREAD.start();
		}
		else
		{
			if (!SceneManager.RENDER_THREAD.isAlive()) {
				SceneManager.RENDER_THREAD.start();
			}
		}
	}

	@Override
	public void endThreads() {
		ArrayList<GameThread> threads = new ArrayList<GameThread>();
		threads.add(SceneManager.RENDER_THREAD);
		this.joinThreads(threads);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {    	
    	final MotionEvent eventCopy = MotionEvent.obtain(event);
    	
    	SceneManager.RENDER_THREAD.handler.post(new Runnable() {
			@Override
			public void run() {
				inputManager.onTouch(view, eventCopy);
			}
    	});
    	
    	
    	return true;
    }


}
