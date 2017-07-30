package tyrfing.games.BlockQuest.prayers;

import java.util.ArrayList;

import tyrfing.common.struct.Coord2;
import tyrfing.games.BlockQuest.lib.Settings;
import tyrfing.games.BlockQuest.mechanics.State;
import tyrfing.games.BlockQuest.rooms.RoomFactoryConfig;
import tyrfing.games.BlockQuest.rooms.RoomShape;

public class Lost extends Prayer {

	private static String NAME = "Lost";
	private static String DESC = "... Please ... I have been searching...\nBut no matter how hard I search...\nI... I'm lost.\nEveryone's dead...\n God, if you truly exist... please...\n\n...HELP ME!!";
	private static int REWARD_PER_LEVEL = 300;
	private static int BASE_REWARD = 600;
	private int level;
	
	public Lost(int level, int pos, State state,PrayerPower power) {
		super(pos, state, power);
		this.level = level;
		super.build();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected String getDesc() {
		return DESC;
	}

	@Override
	protected Settings setupDungeon() {
		Settings settings = new Settings();
		
		settings.roomFactoryConfig = new RoomFactoryConfig();
		settings.fallSpeed = 20;
		settings.speedUp = 14;
		settings.roomFactoryConfig.STAIRS_DOWN_MIN_ROOMS = 35;
		
		/**
		 * First setup all necessary shapes
		 */
		
		
		settings.roomFactoryConfig.shapes = new ArrayList<RoomShape>();
		
		/**
		 *   -
		 * - - -
		 *   -
		 */
		
		RoomShape plus = new RoomShape();
		plus.addElement(new Coord2(-1,1));
		plus.addElement(new Coord2(0,1));
		plus.addElement(new Coord2(1,1));
		plus.addElement(new Coord2(0,0));
		plus.addElement(new Coord2(0,2));
		settings.roomFactoryConfig.shapes.add(plus);
		
		
		/**
		 * -
		 * ---
		 * 
		 */
		
		RoomShape shape = new RoomShape();
		shape.addElement(new Coord2(1,0));
		shape.addElement(new Coord2(-1,1));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		settings.roomFactoryConfig.shapes.add(shape);

		
				
		shape = new RoomShape();
		shape.addElement(new Coord2(-1,0));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		settings.roomFactoryConfig.shapes.add(shape);
		
		/**
		 *   -
		 * - - -
		 */
		
		shape = new RoomShape();
		shape.addElement(new Coord2(-1,1));
		shape.addElement(new Coord2(1,1));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(0,0));
		settings.roomFactoryConfig.shapes.add(shape);
		
		/**
		 * - -
		 *   - -
		 *     .
		 */
		
		shape = new RoomShape();
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		shape.addElement(new Coord2(1,2));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(-1,0));
		settings.roomFactoryConfig.shapes.add(shape);
		
		
		return settings;
	}

	@Override
	public int getReward() {
		return level * REWARD_PER_LEVEL + BASE_REWARD;
	}

	@Override
	public int getLevel() {
		return level;
	}

	protected PrayerType getType()
	{
		return PrayerType.LOST;
	}
	
}
