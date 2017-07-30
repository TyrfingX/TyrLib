package tyrfing.games.BlockQuest.rooms;

import java.util.List;

import tyrfing.common.factory.FactoryManager;
import tyrfing.common.factory.IFactory;
import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Node;
import tyrfing.games.BlockQuest.lib.MainLogic;
import tyrfing.games.BlockQuest.rooms.content.AttackUp;
import tyrfing.games.BlockQuest.rooms.content.Heal;
import tyrfing.games.BlockQuest.rooms.content.Monster;
import tyrfing.games.BlockQuest.rooms.content.MonsterType;
import tyrfing.games.BlockQuest.rooms.content.StairsDown;
import tyrfing.games.BlockQuest.rooms.content.StairsUp;

public class RoomFactory {
	
	private Board board;
	private float tileSize;
	private Node node;
	private List<RoomShape> shapes;
	private int countSpawnedRooms = 0;
	
	private static final int RETRIES = 10;
	
	private static final int MAX_MONSTER = 3;
	private static final float PROB_HEAL = 0.15f;
	private static final float PROB_ATTACK_UP = 0.06f;

	
	private static final float PROB_RED_ROOM = 0.05f;
	
	private static final float PROB_ZOMBIE = 0.075f;
	private static final float PROB_DEMON = 0.04f;
	
	private static final float PROB_VIOLET_ROOM = 0.05f;
	
	private static final float PROB_CURSED_ROOM = 0.05f;
	
	private RoomFactoryConfig config;
	
	public RoomFactory(Board board, Node node, float tileSize, RoomFactoryConfig config)
	{
		this.board = board;
		this.node = node;
		this.tileSize = tileSize;
		this.config = config;
		this.shapes = config.shapes;
	}
	
	public RoomFactoryConfig getConfig()
	{
		return config;
	}
	
	public int getCountSpawnedRooms()
	{
		return countSpawnedRooms;
	}
	
	public void setCountSpawnedRooms(int spawnedRooms)
	{
		countSpawnedRooms = spawnedRooms;
	}
	
	public Node getRootNode()
	{
		return node;
	}
	
	public Room createRandomRoom()
	{
		countSpawnedRooms++;
		Room room = new Room(new Node(0,0), board);
		RoomShape shape = shapes.get((int)(Math.random() * shapes.size()));
		
		this.addElements(room, shape);
		
		if (config.allowCursedRooms && Math.random() <= PROB_CURSED_ROOM)
		{
			room.setState(RoomState.CURSED);
		}
		
		this.addContent(room);
		
		if (room.getState() == RoomState.NORMAL)
		{
			if (config.allowRedRooms && Math.random() <= PROB_RED_ROOM)
			{
				room.setState(RoomState.RED);
			}
			else if (config.allowVioletRooms && Math.random() <= PROB_VIOLET_ROOM)
			{
				room.setState(RoomState.VIOLET);
			} 
			else if (config.allowBlueRooms && Math.random() <= config.PROB_BLUE_ROOM)
			{
				room.setState(RoomState.BLUE);
			}
		}
		
		return room;
	}
	
	public Room createBasicRoom(RoomShape shape)
	{
		countSpawnedRooms++;
		Room room = new Room(new Node(0,0), board);
		this.addElements(room,  shape);
		return room;
	}
	
	public void positionRoom(Room room)
	{
		room.setPos(new Vector2(tileSize * (MainLogic.SIZE_X-1) / 2, 0));
	}
		
	private void addElements(Room room, RoomShape shape)
	{
		
		for (int i = 0; i < shape.countElements(); i++)
		{
			RoomElement element = new RoomElement(room.getNode().createChild(shape.getElement(i)), tileSize);
			room.addElement(element);
		}
		
		room.finishConstruction();
		
	}
	
	private void addContent(Room room)
	{
		int countDoors = (int)(Math.random() * (config.MAX_DOORS-config.MIN_DOORS)) + config.MIN_DOORS;
		
		int monsters = 0;
		float extraMonsterFactor = 1;
		if (room.getState() == RoomState.CURSED) extraMonsterFactor = 2;
		while (Math.random() <= config.PROB_PER_TILE * room.getCountElements() * extraMonsterFactor && monsters < MAX_MONSTER)
		{
			
			IFactory factory;
			
			if (room.getState() != RoomState.CURSED)
			{
				if (FactoryManager.getFactory(MonsterType.ZOMBIE.toString()) != null && Math.random() <= PROB_ZOMBIE)
				{
					factory = FactoryManager.getFactory(MonsterType.ZOMBIE.toString());
				}
				else if (FactoryManager.getFactory(MonsterType.DEMON.toString()) != null && Math.random() <= PROB_DEMON)
				{
					countDoors = 0;
					factory = FactoryManager.getFactory(MonsterType.DEMON.toString());
					Monster monster = (Monster) factory.create();
					RoomElement element = room.getRandomFreeRoomElement();
					monster.createEntity(tileSize);
					monster.assignRoom(element);
					monsters++;
					break;
				}
				else if (FactoryManager.getFactory(MonsterType.ORCBOSS.toString()) != null && Math.random() <= config.PROB_ORC_BOSS)
				{
					factory = FactoryManager.getFactory(MonsterType.ORCBOSS.toString());
					FactoryManager.unRegisterFactory(MonsterType.ORCBOSS.toString());
					config.PROB_ORC_BOSS = 0;
				}
				else if (FactoryManager.getFactory(MonsterType.UNDEADBOSS.toString()) != null && Math.random() <= config.PROB_UNDEAD_BOSS)
				{
					factory = FactoryManager.getFactory(MonsterType.UNDEADBOSS.toString());
					FactoryManager.unRegisterFactory(MonsterType.UNDEADBOSS.toString());
					config.PROB_UNDEAD_BOSS = 0;
				}
				else
				{
					factory = FactoryManager.getFactory(MonsterType.ORC.toString());
				}
			}
			else
			{
				factory = FactoryManager.getFactory(MonsterType.ZOMBIE.toString());
			}
			
			Monster monster = (Monster) factory.create();
			RoomElement element = room.getRandomFreeRoomElement();
			monster.createEntity(tileSize);
			monster.assignRoom(element);
			monsters++;
		}
		
		if (room.getState() != RoomState.CURSED && Math.random() <= PROB_HEAL)
		{
			RoomElement element = room.getRandomFreeRoomElement();
			if (element != null)
			{
				Heal heal = new Heal();
				heal.assignRoom(element);
			}
		}
		
		if (countSpawnedRooms >= config.STAIRS_UP_MIN_ROOMS && Math.random() <= config.PROB_STAIRS_UP)
		{
			RoomElement element = room.getRandomFreeRoomElement();
			if (element != null)
			{
				StairsUp stairs = new StairsUp();
				stairs.assignRoom(element);
			}
		}
		else if (countSpawnedRooms >= config.STAIRS_DOWN_MIN_ROOMS && Math.random() <= config.PROB_STAIRS_DOWN)
		{
			RoomElement element = room.getRandomFreeRoomElement();
			if (element != null)
			{
				StairsDown stairs = new StairsDown();
				stairs.assignRoom(element);
			}
		}
		

		if (room.getState() != RoomState.CURSED && Math.random() <= PROB_ATTACK_UP && config.allowAttackUp)
		{
			RoomElement element = room.getRandomFreeRoomElement();
			if (element != null)
			{
				AttackUp attackUp = new AttackUp();
				attackUp.assignRoom(element);
			}
		}
		
		for (int i = 0; i < countDoors; i++)
		{
			int tries = 0;
			while(tries <= RETRIES)
			{
				RoomElement element = room.getRandomRoomElement();
				Direction dir = Direction.getRandomDirection();
				if (!element.hasDoor(dir))
				{
					Door door = null;
					if (config.allowGreenDoors && Math.random() <= config.PROB_GREEN_DOOR)
					{
						door = new Door(dir, DoorType.GREEN);
					}
					else
					{
						door = new Door(dir, DoorType.RED);
					}
					
					if (element.addDoor(door))
					{
						break;
					}
				}
				tries++;
			}
			

		}
	
	}

}
