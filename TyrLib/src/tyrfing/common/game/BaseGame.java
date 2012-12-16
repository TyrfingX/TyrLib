package tyrfing.common.game;


import java.util.Collection;

import tyrfing.common.input.InputManager;

import android.content.Context;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class BaseGame 	extends SurfaceView
								implements SurfaceHolder.Callback {

	public static Context CONTEXT;
	protected InputManager inputManager;
	
	public BaseGame(Context context)
	{
		super(context);
		CONTEXT = context;
		this.getHolder().addCallback(this);
		inputManager = new InputManager();
		this.setOnTouchListener(inputManager);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	// TODO Auto-generated method stub
	
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		this.setWillNotDraw(false);
		startThreads();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		endThreads();
	}
	
	public abstract void startThreads();
	public abstract void endThreads();
	public abstract void go();
	
	protected void joinThreads(Collection<GameThread> threads)
	{
		if(threads != null)
		{
			for (GameThread thread : threads)
			{
				thread.end();
			}
		}
	}
	
	public static String getString(int key)
	{
		return CONTEXT.getString(key);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if (InputManager.onPressBack()) return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
