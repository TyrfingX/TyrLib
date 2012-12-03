package tyrfing.common.game;

import tyrfing.common.game.objects.IUpdateable;

public abstract class GameThread extends Thread implements IUpdateable {
	
	protected boolean run = false;
	protected float DELAY = 10;
	protected final static float TO_DELAY = 10000000;
	
	public GameThread()
	{
	}
	
	public void run()
	{
		run = true;
		
		while (run) {
			try { 
				this.loop();
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	public void loop() throws InterruptedException  {
		long time = System.nanoTime();
		while(run)
		{
			
			while((DELAY = System.nanoTime() - time) < TO_DELAY) {
				Thread.yield();
			}
			time = System.nanoTime();
			
			onUpdate(DELAY / 1000000000.f);
			
			Thread.sleep((long)(TO_DELAY/10000000));
			
		}
	}
	
	public void end()
	{
		run = false;
	}
	
}
