package tyrfing.games.id3.lib.prayers;

import tyrfing.games.id3.lib.Settings;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.rooms.RoomFactoryConfig;

public class Quake extends Prayer {

	private static String NAME = "Quake";
	private static String DESC = "A dead end.\nOnce more our path upwards\nhas been blocked.\n\nI pray to you god,\nif you have yet not\nabandoned us.\n\n...Help us.";
	private static int REWARD_PER_LEVEL = 500;
	private static int BASE_REWARD = 800;
	private int level;
	
	public Quake(int level, int pos, State state,PrayerPower power) {
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
		settings.speedUp = 20;
		settings.roomFactoryConfig.STAIRS_DOWN_MIN_ROOMS = 32;
		settings.roomFactoryConfig.createDefaultShapes();
		settings.roomFactoryConfig.PROB_GREEN_DOOR = 1;
		settings.roomFactoryConfig.MIN_DOORS = 3;
		settings.roomFactoryConfig.MAX_DOORS = 6;
		settings.roomFactoryConfig.allowGreenDoors = true;
		
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
		return PrayerType.QUAKE;
	}
	
}
