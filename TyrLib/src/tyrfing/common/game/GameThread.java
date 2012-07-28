package tyrfing.common.game;

import tyrfing.common.game.objects.IUpdateable;

public abstract class GameThread extends Thread implements IUpdateable {
	
	protected boolean run = false;
	protected float DELAY;
	protected final static float TO_DELAY = 10000000;
	
	public GameThread()
	{
	}
	
	public void run()
	{
		run = true;
		
		
		try {
			long time = System.nanoTime();
			while(run)
			{
				
				while((DELAY = System.nanoTime() - time) < TO_DELAY) Thread.yield();
				time = System.nanoTime();
				
				onUpdate(DELAY / 1000000000.f);
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void end()
	{
		run = false;
	}
	
}
