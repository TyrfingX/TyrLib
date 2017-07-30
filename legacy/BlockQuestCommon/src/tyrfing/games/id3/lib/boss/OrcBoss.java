package tyrfing.games.id3.lib.boss;

import tyrfing.common.factory.FactoryManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.sound.SoundManager;
import tyrfing.games.id3.lib.MainLogic;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.rooms.Room;
import tyrfing.games.id3.lib.rooms.content.Monster;
import tyrfing.games.id3.lib.rooms.content.MonsterFactory;
import tyrfing.games.id3.lib.rooms.content.MonsterType;

public class OrcBoss extends BossSkript{
	
	
	private enum PHASE {
		IDLE,
		SPAMM,
		SPEEDUP,
		SPAWN,
		DEAD
	}
	
	public static final int LEVEL = 10;
	private static final int EXTRA_MONEY = 4000;
	private static final int PHASE_SHIFT_IDLE_SPAMM = 10;
	private static final int PHASE_SHIFT_SPAMM_SPEEDUP = 25;
	private static final int PHASE_SHIFT_SPEEDUP_SPAWN = 15;
	
	
	private PHASE phase;
	
	private Speech SPAMM = new Speech(	new String[] { BREAK+2,"oHH!", "dELICioUS", "hUMaN", BREAK+3, "COME", "mY", "mINiONs!!", "tImE", BREAK+2, "tO", "hUNT!!!"},
			 							new float[] { 0, 100, 200, 300, 0, 75, 150, 200, 100, 0, 150, 300});
	private Speech SPEEDUP = new Speech( new String[] { BREAK+2, "aHHAHAAH", "DIE!!", "DiIiIIeEE!!!!"},
										 new float[] { 0, 100, 200, 300 } );
	private Speech SPAWN = new Speech( new String[] {  "wHY", "yOu", "NOT", "dIE??", BREAK+3, "pUnYY", "hUUMAAN", BREAK+3, "kILL", "yOu", "mYsELF"},
									   new float[] { 75, 150, 200, 0, 100, 200, 300, 0, 150, 175, 250} );
	private Speech DEAD = new Speech( new String[] {  "aRGGHGRH", BREAK+3, "iMPossIBLEE"},
									  new float[] { 150, 0, 300} );
	
	public OrcBoss(MainLogic mainLogic)
	{
		super(mainLogic);
		this.phase = PHASE.IDLE;
		
		mainLogic.getRoomFactory().getConfig().PROB_STAIRS_DOWN = 0;
		mainLogic.EXTRA = EXTRA_MONEY;
		
		Ressources.loadRes("OrcBoss", R.drawable.orcboss, new Vector2(mainLogic.getBoard().getTileSize(), mainLogic.getBoard().getTileSize()));
	
		SoundManager.getInstance().createSoundtrack(R.raw.orcboss, "ORCBOSS").play();
	}
			

	@Override
	public void onUpdate(float time) {
		if (talking)
		{
			if (phase == PHASE.SPAMM)
			{
				SPAMM.hold(time);
			} else if (phase == PHASE.SPEEDUP)
			{
				SPEEDUP.hold(time);
			} else if (phase == PHASE.SPAWN)
			{
				SPAWN.hold(time);
			} else if (phase == PHASE.DEAD)
			{
				DEAD.hold(time);
			}
		}
	}
	
	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSpawnRoom(Room room) {
		countSpawnedRooms++;
		if (phase == PHASE.IDLE)
		{
			if (countSpawnedRooms == PHASE_SHIFT_IDLE_SPAMM)
			{
				countSpawnedRooms = 0;
				phase = PHASE.SPAMM;
				
				mainLogic.getRoomFactory().getConfig().PROB_PER_TILE = 0.18f;

				textCounter = 0;
				talking = true;
			}

		} else if (phase == PHASE.SPAMM)
		{
			if (countSpawnedRooms == PHASE_SHIFT_SPAMM_SPEEDUP)
			{
				countSpawnedRooms = 0;
				phase = PHASE.SPEEDUP;
				
				textCounter = 0;
				talking = true;
			
				Room.fallSpeed *= 1.75f;
				Room.SPEED_UP /= 1.75f;
				
			}
			
			
		} else if (phase == PHASE.SPEEDUP)
		{
			if (countSpawnedRooms == PHASE_SHIFT_SPEEDUP_SPAWN)
			{
				countSpawnedRooms = 0;
				phase = PHASE.SPAWN;
				
				textCounter = 0;
				talking = true;
				
				mainLogic.getRoomFactory().getConfig().PROB_ORC_BOSS = 1;
				FactoryManager.registerFactory(MonsterType.ORCBOSS.toString(), new MonsterFactory(LEVEL, MonsterType.ORCBOSS, mainLogic.getUpdater(), 1));
			}
		}
	}

	@Override
	public void onMobDies(Monster monster) {
		if (monster.getStats().getStat("Type") == MonsterType.ORCBOSS.ordinal())
		{
			mainLogic.getRoomFactory().getConfig().PROB_STAIRS_DOWN = 0.5f;
			mainLogic.getRoomFactory().getConfig().STAIRS_DOWN_MIN_ROOMS = 0;
			phase = PHASE.DEAD;
			
			talking = true;
			textCounter = 0;
		}
	}


	@Override
	public void onFinishFloor() {
		mainLogic.state.tutorial.doItem("OrcBoss");
	}

}
