package tyrfing.games.id3.lib.rooms;

import tyrfing.common.game.objects.IUpdateable;

public class VisitCounter implements IUpdateable {
	
	private float passedTime;
	private int visits;
	private final float COOLDOWN_VISIT = 3;
	
	public VisitCounter()
	{
		passedTime = 0;
		visits = 0;
	}

	@Override
	public void onUpdate(float time) {
		if (visits > 1)
		{
			passedTime += time;
			if (passedTime >= COOLDOWN_VISIT)
			{
				visits--;
				passedTime -= COOLDOWN_VISIT;
			}
		}
	}
	
	public int getVisits()
	{
		return visits;
	}
	
	public void visit()
	{
		visits++;
		visits *= 2;
		if (visits > 20) visits = 20;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
