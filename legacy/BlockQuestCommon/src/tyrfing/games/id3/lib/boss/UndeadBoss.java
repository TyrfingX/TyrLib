package tyrfing.games.id3.lib.boss;

import java.util.ArrayList;
import java.util.List;

import tyrfing.common.factory.FactoryManager;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Animation;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.sound.SoundManager;
import tyrfing.common.struct.Coord2;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.MainLogic;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.rooms.Door;
import tyrfing.games.id3.lib.rooms.DoorType;
import tyrfing.games.id3.lib.rooms.Room;
import tyrfing.games.id3.lib.rooms.RoomFactoryConfig;
import tyrfing.games.id3.lib.rooms.RoomShape;
import tyrfing.games.id3.lib.rooms.RoomState;
import tyrfing.games.id3.lib.rooms.content.Monster;
import tyrfing.games.id3.lib.rooms.content.MonsterFactory;
import tyrfing.games.id3.lib.rooms.content.MonsterType;

public class UndeadBoss extends BossSkript{

	private enum PHASE
	{
		IDLE,
		TRANSIST_IDLE_DROP,
		DROP_BLOCKS,
		CURSE_ROOMS,
		SPAWNED,
		TRANSIST_SPAWNED_DEAD,
		DEAD
	}
	
	public static final int LEVEL = 20;
	
	private static final int DROP_AMOUNT = 6;
	private static final int PHASE_SHIFT_IDLE_DROP = 6;
	private static final int PHASE_SHIFT_DROP_CURSE = 40;
	private static final int PHASE_SHIFT_CURSE_SPAWN = 17;
	private static final int DROP_INTERVAL = 11;
	private static final int CURSE_INTERVAL = 6;
	
	
	private Speech DROP = new Speech(	new String[] { "Who", "dost", "dare", BREAK+4, "to", "disturb", "my", "slumber?", BREAK+4, "A", "mortal?", BREAK+3, "BE", "GONE!!" },
										new float[] { 100, 200, 300, 0, 75, 150, 200, 100, 0, 150, 300, 0, 100, 200});

	private Speech CURSE = new Speech(	new String[] { "Mortal", "one", BREAK+3, "Learn", "thy", "bounds!" },
										new float[] { 100, 200, 0, 150, 250, 300});
	
	private Speech SPAWN = new Speech(	new String[] { "Thou", "who", "art", "but", "nothingness", BREAK+3, "Death", "awaits", "you!!" },
										new float[] { 75, 150, 200, 300, 100, 0, 100, 200, 300});
	
	private Speech DEAD = new Speech(	new String[] { "OHHH", "I", "see!", BREAK+3, "You", "were", "guided", "here!!", BREAK+3, "AMUSING!", BREAK+3, "How", "truly", "amusing!"},
										new float[] { 75, 150, 200, 0, 30, 100, 300, 180, 0, 250, 0, 100, 200, 300 });

	
	private int countSpawnedRooms = 0;
	private PHASE phase;
	private List<Animation> animations;
	
	private RoomShape dropRoomShape;
	
	public UndeadBoss(MainLogic mainLogic)
	{
		super(mainLogic);
		RoomFactoryConfig config = mainLogic.getRoomFactory().getConfig();
		config.PROB_BLUE_ROOM += 0.15f;
		config.PROB_STAIRS_DOWN = 0;
		phase = PHASE.IDLE;
		animations = new ArrayList<Animation>();
		
		mainLogic.EXTRA = 10000;
		
		dropRoomShape = new RoomShape();
		dropRoomShape.addElement(new Coord2(0,0));
		
		Ressources.loadRes("UndeadBoss", R.drawable.undeadboss, new Vector2(mainLogic.getBoard().getTileSize(), mainLogic.getBoard().getTileSize()));
	
		SoundManager.getInstance().createSoundtrack(R.raw.undeadboss, "UNDEADBOSS").play();
	}
	
	@Override
	public void onUpdate(float time) {
		for (int i = 0; i < animations.size(); ++i)
		{
			Animation animation = animations.get(i);
			if (animation.getCurrentFrame() == animation.getCountFrames() - 1)
			{
				SceneManager.RENDER_THREAD.removeRenderable(animation);
			}
		}
		
		if (talking)
		{
			if (phase == PHASE.TRANSIST_IDLE_DROP)
			{
				DROP.hold(time);
				if (!talking)
				{
					phase = PHASE.DROP_BLOCKS;
					countSpawnedRooms = DROP_INTERVAL - 1;
				}
			} else if (phase == PHASE.CURSE_ROOMS)
			{
				CURSE.hold(time);
			} else if (phase == PHASE.SPAWNED)
			{
				SPAWN.hold(time);
			} else if (phase == PHASE.TRANSIST_SPAWNED_DEAD)
			{
				DEAD.hold(time);
				if (!talking)
				{
					phase = PHASE.DEAD;
					RoomFactoryConfig config = mainLogic.getRoomFactory().getConfig();
					config.PROB_STAIRS_DOWN = 0.5f;
					config.STAIRS_DOWN_MIN_ROOMS = 0;
				}
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onSpawnRoom(Room room) {
		countSpawnedRooms++;
		if (phase == PHASE.IDLE)
		{
			if (countSpawnedRooms == PHASE_SHIFT_IDLE_DROP) {
				phase = PHASE.TRANSIST_IDLE_DROP;
				
				textCounter = 0;
				talking = true;
			}
		} 
		else if (phase == PHASE.CURSE_ROOMS)
		{
			if (countSpawnedRooms % CURSE_INTERVAL == 0)
			{
				this.curseRandomRoom();
			}
			
			if (countSpawnedRooms == PHASE_SHIFT_CURSE_SPAWN)
			{
				this.phase = PHASE.SPAWNED;
				
				countSpawnedRooms = 0;
				textCounter = 0;
				talking = true;
				
				mainLogic.getRoomFactory().getConfig().PROB_UNDEAD_BOSS = 1;
				FactoryManager.registerFactory(MonsterType.UNDEADBOSS.toString(), new MonsterFactory(LEVEL, MonsterType.UNDEADBOSS, mainLogic.getUpdater(), 1));
			}
		}
		else if (phase == PHASE.DROP_BLOCKS)
		{
			if (countSpawnedRooms % DROP_INTERVAL == 0)
			{
				int gap = 0;
				for (int i = 0; i < DROP_AMOUNT; ++i) {
					
					if (i == 3)
					{
						gap = 5;
					}
					
					Room dropRoom = mainLogic.getRoomFactory().createBasicRoom(dropRoomShape);
					for (Direction direction : Direction.values())
					{
						Door door = new Door(direction, DoorType.RED);
						dropRoom.getRoomElement(0).addDoor(door);
					}
					dropRoom.setState(RoomState.CURSED);
					mainLogic.getUpdater().addItem(dropRoom);
					mainLogic.getRoomFactory().getRootNode().addChild(dropRoom.getNode());
					mainLogic.getRoomFactory().positionRoom(dropRoom);
					dropRoom.setFirstFall(false);
					dropRoom.setX(mainLogic.getBoard().getTileSize()*(i+gap));
					dropRoom.setY(mainLogic.getBoard().getTileSize());
					if (dropRoom.overlaps())
					{
						dropRoom.destroy();
					}
				}
			}
			
			if (countSpawnedRooms == PHASE_SHIFT_DROP_CURSE)
			{
				phase = PHASE.CURSE_ROOMS;
				countSpawnedRooms = 0;

				textCounter = 0;
				talking = true;
			}
		}
	}
	
	@Override
	public void onMobDies(Monster monster) {
		if (monster.getStats().getStat("Type") == MonsterType.UNDEADBOSS.ordinal())
		{
			phase = PHASE.TRANSIST_SPAWNED_DEAD;
			
			talking = true;
			textCounter = 0;
		}
	}
	
	private void curseRandomRoom()
	{

		List<Room> rooms = mainLogic.getCheckBoard().getRoomsOnBoard();
		
		for (int retries = 0; retries < 3; ++retries)
		{
		
			int random = (int)(Math.random()*rooms.size());
			Room room = rooms.get(random);
			
			if (room.getState() == RoomState.NORMAL)
			{
			
				room.setState(RoomState.CURSED);
				
				Node node = room.getNode();
				Animation curse = SceneManager.createAnimation(Ressources.getBitmap("curseEffect"), 7, 0.1f, node);
				animations.add(curse);
				curse.setRepeat(false);
			
				break;
			
			}
		}
	}

	@Override
	public void onFinishFloor() {
		mainLogic.state.tutorial.doItem("UndeadBoss");
	}

}
