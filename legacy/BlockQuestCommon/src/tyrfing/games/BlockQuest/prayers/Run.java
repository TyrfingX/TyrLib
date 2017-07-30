package tyrfing.games.BlockQuest.prayers;

import tyrfing.games.BlockQuest.lib.Settings;
import tyrfing.games.BlockQuest.mechanics.State;
import tyrfing.games.BlockQuest.rooms.RoomFactoryConfig;

public class Run extends Prayer {

	private static String NAME = "Run";
	private static String DESC = "Damn, damn, damn...!\nJust a bit further...\nThis damn earthquake...!\nOh God, please show me the way..\nout of here... Faster..! Faster!!\nI beg of you..\n\n..SAVE ME!!";
	private static int REWARD_PER_LEVEL = 550;
	private static int BASE_REWARD = 500;
	private int level;
	
	public Run(int level, int pos, State state,PrayerPower power) {
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
		settings.fallSpeed = 100;
		settings.speedUp = 2;
		settings.fallSpeed += level/2;
		settings.roomFactoryConfig.STAIRS_DOWN_MIN_ROOMS = 30;
		settings.roomFactoryConfig.createDefaultShapes();
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
		return PrayerType.RUN;
	}
	
}
