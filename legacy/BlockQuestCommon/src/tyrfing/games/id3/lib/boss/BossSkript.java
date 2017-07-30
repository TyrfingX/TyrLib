package tyrfing.games.id3.lib.boss;

import tyrfing.games.id3.lib.MainLogic;
import tyrfing.games.id3.lib.rooms.Room;
import tyrfing.games.id3.lib.rooms.Skript;

public abstract class BossSkript implements Skript {

	
	protected int textCounter = 0;
	protected static final String BREAK = "%%%%%%%";

	protected MainLogic mainLogic;
	protected int countSpawnedRooms;
	protected boolean talking = false;
	
	private float delay = TEXT_DELAY;
	private static final float TEXT_DELAY = 0.7f;
	private BossTalk bossTalk;
	
	protected class Speech
	{
		public String[] text;
		public float[] textPos;
	
		public Speech(String[] text, float[] textPos)
		{
			this.text = text;
			this.textPos = textPos;
		}
		
		public void hold(float time)
		{
			float factor = 1;
			boolean breakString = text[textCounter].startsWith(BREAK);
			
			if (breakString)
			{
				String textFactor = text[textCounter].substring(BREAK.length());
				factor = Float.valueOf(textFactor);
			}
			
			if (delay >= TEXT_DELAY * factor)
			{
				if (breakString)
				{
					textCounter++;
				}
				
				bossTalk.addSpeech(text[textCounter], textPos[textCounter]);
				textCounter++;
				if (textCounter == text.length) 
				{
					talking = false;
					delay = TEXT_DELAY;
				}
				else
				{
					delay -= TEXT_DELAY * factor;
				}
			}
			else
			{
				delay += time;
			}
		}
		
	}
	
	public BossSkript(MainLogic mainLogic)
	{
		this.mainLogic = mainLogic;
		bossTalk = new BossTalk(mainLogic.getRoomFactory().getRootNode(), mainLogic.getBoard().getTileSize()*2);
	}
	
	@Override
	public void onClearRoom(Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClearRow() {
		// TODO Auto-generated method stub
		
	}
}
