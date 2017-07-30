package tyrfing.games.BlockQuest.rooms.content;

import java.util.ArrayList;
import java.util.Observable;

import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.game.objects.GameObject;
import tyrfing.common.game.objects.Level;
import tyrfing.common.game.objects.Movement;
import tyrfing.common.game.objects.Stats;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.struct.Coord2;
import tyrfing.common.struct.Node;
import tyrfing.games.BlockQuest.mechanics.Fight;
import tyrfing.games.BlockQuest.rooms.Room;
import tyrfing.games.BlockQuest.rooms.RoomElement;

public class Hero extends GameObject {

	private static final int HP_PER_LEVELUP = 5;
	
	private Movement movement;
	private Image entity;
	
	private float SPEED = 150;
	
	private Board board;
	
	private boolean spawned = false;
	
	private Room currentRoom;
	private Room lastRoom;
	private Room targetRoom;
	private RoomElement lastElement;
	
	private Stats stats;
	private Level level;

	private boolean onStairsUp = false;
	private boolean onStairsDown = false;
	
	private static final int RETRIES = 5;
	
	private float buffDuration = 0;
	
	private boolean clearedARoom = false;
	private Monster killedAMob = null;
	private boolean usedHealCrytsal = false;
	
	private Fight fight;
	private Monster currentEnemy;
	
	public static final int TURNS_PER_TICK = 3;
	private float tickTime;
	
	
	private float hpRegenTime;
	
	public Hero(Node node, Board board, Stats stats) {
		super(node);
		entity = SceneManager.createImage(	Ressources.getScaledBitmap("hero", new Vector2(board.getTileSize(), board.getTileSize())),
												node.createChild());
		entity.setPriority(10);
		entity.setVisible(false);
		SPEED = stats.getStat("Speed");
		movement = new Movement(node, SPEED);
		this.board = board;
		
		this.stats = stats;
		stats.setStat("Bottles", stats.getStat("HpPotions"));
		level = new Level(stats);
		
	}

	public Stats getStats()
	{
		return stats;
	}
	
	@Override
	public void onUpdate(float time) {
		if (!spawned)
		{
			trySpawning();
		}
		else if (fight == null)
		{

			if (buffDuration > 0)
			{
				buffDuration -= time;
				if (buffDuration <= 0)
				{
					buffDuration = 0;
					stats.setStat("Atk", stats.getStat("OldAtk"));
					this.setChanged();
				}
			}
			
			if (stats.getStat("Hp") > 0) {
				hpRegenTime += time;
				if (hpRegenTime >= 1) {
					hpRegenTime -= 1;
					stats.setStat("Hp", stats.getStat("Hp") + stats.getStat("HP_REGEN"));
					if (stats.getStat("Hp") > stats.getStat("MaxHp")) {
						stats.setStat("Hp", stats.getStat("MaxHp"));
					}
					this.setChanged();
				}
			}
			
			while (time > 0)
			{
				if(movement.isFinished())
				{
					act();
					time = 0;
				}
				else
				{
					movement.onUpdate(time);
					time = movement.getRemainingTime();
				}
			}
			
			this.notifyObservers();
		}
		else
		{
			tickTime += time;
			if (tickTime >= 1.f/stats.getStat("TICKS_PER_SECOND")) {
				this.proceedFight();
				tickTime -= 1.f/stats.getStat("TICKS_PER_SECOND");
			}
		}
		

		
	}
	
	public boolean isBuffed()
	{
		return (buffDuration != 0);
	}
	
	private void trySpawning()
	{
		for (int i = 0; i < board.getWidth(); i++)
		{
			//There is a room on the lowest level => spawn!
			RoomElement element = (RoomElement) board.getItem(i, board.getHeight() - 1);
			if (element != null)
			{
				if (!element.getRoom().isFalling())
				{
					spawned = true;
					Vector2 target = new Vector2(i * board.getTileSize(), (board.getHeight() - 1) * board.getTileSize());
					node.setPos(target.add(new Vector2(0, board.getTileSize())));
					movement.addPoint(target.add(node.getParent().getAbsolutePos()));
					entity.setVisible(true);
					this.targetRoom = element.getRoom();
					break;
				}
			}
		}
	}
	
	public void spawn(RoomElement element)
	{
		spawned = true;
		entity.setVisible(true);
		node.setPos(element.getCenter().add(new Vector2(-entity.getWidth()/2, -entity.getHeight()/2)));
	}
	
	private void act()
	{

		RoomElement element = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
		if (element != null)
		{
			Room room = element.getRoom();
			
			if (room != currentRoom)
			{
				if (onStairsUp || onStairsDown)
				{
					onStairsUp = false;
					onStairsDown = false;
					this.setChanged();
					this.notifyObservers();
				}
				onStairsUp = false;
				onStairsDown = false;
				lastRoom = currentRoom;
				room.enter(this);
				if (currentRoom != null)
				{
					currentRoom.leave(this);
					if (currentRoom.countContent() == 0 && !currentRoom.isCleared())
					{
						clearedARoom = true;
						currentRoom.cleared();
					}
				}
			}
			
			currentRoom = room;
			
			determineNextAction();
			
		}
		
	}
	
	private void determineNextAction()
	{
		
		for (int i = 0; i < currentRoom.countTotalContent(); i++)
		{
			GameObject content = currentRoom.getContent(i);
			
			if (content instanceof StairsUp)
			{
				onStairsUp = true;
				this.setChanged();
				this.notifyObservers();
			}
		}
		
		if (currentRoom.countContent() == 0)
		{
			this.changeRoom();

		}
		else
		{
			this.clearRoom();
		}
		
	}
	
	public void changeRoom()
	{
		ArrayList<Room> adjacentRooms = currentRoom.getAdjacentRooms(true);
		if (adjacentRooms.size() != 0)
		{
			//Only consider moving into the room from which the hero came, if its the only choice
			if (adjacentRooms.size() != 1)
			{
				if (lastRoom != null)
				{
					adjacentRooms.remove(lastRoom);
				}
			}
			this.moveToAdjacentRoom(adjacentRooms);
		}
		else
		{
			this.randomMovement();
		}
	}
	
	public RoomElement getCurrentElement()
	{
		return (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
	}
	
	public void clearRoom()
	{
		for (int i = 0; i < currentRoom.countTotalContent(); i++)
		{
			GameObject content = currentRoom.getContent(i);	
			if (content instanceof Monster)
			{
				Monster monster = (Monster) content;
				
				if (monster.getStats().getStat("Hp") > 0)
				{
					
					RoomElement currentElement = this.getCurrentElement();
					
					RoomElement monsterElement = monster.getRoomElement();
					if (currentElement != monsterElement)
					{
						currentRoom.createPathToElement(movement, currentElement, monsterElement);
						if (movement.isFinished())
						{
							this.changeRoom();
						}
						else
						{
							break;
						}
					}
					else
					{
						currentEnemy = monster;
						tickTime = 0;
						fight = new Fight(this.stats, monster.getStats());
						this.proceedFight();
						break;
					}
					
				}
				
			}
			else if (content instanceof Heal)
			{
				if (stats.getStat("Hp") != stats.getStat("MaxHp"))
				{
					stats.setStat("Hp", stats.getStat("MaxHp"));
				}
				else
				{
					if (stats.getStat("Bottles") > stats.getStat("HpPotions"))
					{
						stats.setStat("PotionFill", stats.getStat("PotionFill") + stats.getStat("Refill"));
					}	
				}
				
				((StaticContent)content).remove();
				this.setChanged();
				this.notifyObservers();
			}
			else if (content instanceof AttackUp)
			{
				this.buff(AttackUp.DURATION);
				((StaticContent)content).remove();
				this.setChanged();
				this.notifyObservers();
			}
			else if (content instanceof StairsDown)
			{
				RoomElement currentElement = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
				StairsDown stairs = (StairsDown) content;
				RoomElement stairsElement = stairs.getRoomElement();
				if (currentElement != stairsElement)
				{
					currentRoom.createPathToElement(movement, currentElement, stairsElement);
				}
				else
				{
					onStairsDown = true;
					this.setChanged();
					this.notifyObservers();
				}
			}
		}	
	}
	
	private void proceedFight() {
		fight.simulate(TURNS_PER_TICK);
		
		if (currentEnemy.getStats().getStat("Hp") <= 0)
		{
			currentEnemy.die();
			this.doLevelUps();
			killedAMob = currentEnemy;
			fight = null;
		}
		else if (this.stats.getStat("Hp") <= 0) {
			fight = null;
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public boolean clearedARoom()
	{
		boolean clearedARoomTmp = clearedARoom;
		clearedARoom = false;
		return clearedARoomTmp;
	}
	
	public Monster killedAMob()
	{
		Monster killedAMobTmp = killedAMob;
		killedAMob = null;
		return killedAMobTmp;
	}
	
	public boolean isOnStairsUp()
	{
		return onStairsUp;
	}
	
	public boolean isOnStairsDown()
	{
		return onStairsDown;
	}
	
	public void doLevelUps()
	{
		while (level.checkLevelUp())
		{
			this.stats.setStat("MaxHp", this.stats.getStat("MaxHp") + HP_PER_LEVELUP);
			this.stats.setStat("Hp", this.stats.getStat("Hp") + HP_PER_LEVELUP);
		}
	}
	
	public void randomMovement()
	{
		int tries = 0;
		while (tries <= RETRIES)
		{
			
			RoomElement currentElement = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
			if (currentElement.getRoom().countRoomElements() <= 1) break; 
			
			//Just wander around randomly within the current room if no neighboring exists
			Direction dir = Direction.getRandomDirection();
			Vector2 target = Direction.translatePoint(dir, this.getPos(), board.getTileSize());
			Vector2 boardTarget = target.add(new Vector2(entity.getWidth()/2, entity.getHeight()/2));
			if (board.pointInBoard(boardTarget))
			{
				RoomElement element = (RoomElement)board.getItem(boardTarget);
				if (element != null && !element.getRoom().isMoving())
				{
					currentElement = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
					currentRoom.createPathToElement(movement, currentElement, element);
					break;
				}
			}
			tries++;
		}
	}
	
	public void moveToAdjacentRoom(ArrayList<Room> adjacentRooms)
	{
		//Get the non-cleared rooms
		ArrayList<Room> notClearedRooms = new ArrayList<Room>();
		Room nextRoom = null;
		
		for (Room room : adjacentRooms)
		{
			if (!room.isCleared()) notClearedRooms.add(room);
		}
		
		//Only take a cleared room if no cleared one is adjacent
		if (notClearedRooms.size() != 0)
		{
			nextRoom = notClearedRooms.get((int)(Math.random()*notClearedRooms.size()));
		}
		else
		{
			
			//Next check if there is a room which is adjacent to a room adjacent to a cleared room
			nextRoom = null;
			searchRoom: for (Room room : adjacentRooms)
			{
				ArrayList<Room> indirectAdjacentRooms = room.getAdjacentRooms(false);
			
				for (Room room2 : indirectAdjacentRooms)
				{
					if (room2 != lastRoom && room2 != currentRoom && !room2.isCleared())
					{
						nextRoom = room;
						break searchRoom;
					}
				}	
			}
			
			if (nextRoom == null) {
				// Still not found a not cleared room! Try one last time by increasing the distance
				searchRoom: for (Room room : adjacentRooms)
				{
					ArrayList<Room> indirectAdjacentRooms = room.getAdjacentRooms(false);
				
					for (Room room2 : indirectAdjacentRooms)
					{
						if (room2 != lastRoom && room2 != currentRoom && !room2.isCleared())
						{
							nextRoom = room;
							break searchRoom;
						}
					}	
				}
				
			}
			
			if (nextRoom == null)
			{
				// Nothings found, just run around randomly trying to explore the dungeon
				float max = 0;
				for (Room room : adjacentRooms)
				{
					max += 1 / (float)room.getVisits();
				}
				
				float random = (float)Math.random();
				
				int res = 0;
				for (Room room : adjacentRooms)
				{
					res += (1.f / (float)room.getVisits()) / max;
					if (res >= random)
					{
						nextRoom = room;
						break;
					}
				}
				
				if (nextRoom == null)
				{
					nextRoom = adjacentRooms.get((int)(Math.random()*adjacentRooms.size()));
				}
			
			}
			
			
		}
		

		RoomElement target = nextRoom.getRandomRoomElement();
		RoomElement currentElement = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
		currentRoom.createPathToElement(movement, currentElement, target);
	
		targetRoom = nextRoom;
	}

	public Room getTargetRoom()
	{
		return targetRoom;
	}
	
	@Override
	public float getWidth() {
		return entity.getWidth();
	}

	@Override
	public float getHeight() {
		return entity.getHeight();
	}

	@Override
	public void update(Observable observable, Object data) {
		
		Coord2 boardCoord;
		Vector2 tmpPos = this.getPos();
		
		if (observable == currentRoom)
		{
			
			boardCoord = board.getBoardCoord(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
			if (boardCoord.y < board.getHeight() - 1)
			{
				node.translate(currentRoom.getLastTranslation());
			}
			movement.clearPath();
		}
		
		if (observable == targetRoom)
		{
			movement.clearPath();
		}
		
		boardCoord = board.getBoardCoord(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
	
		if (boardCoord.y >= board.getHeight())
		{
			this.setPos(tmpPos);
		}
	
	}
	
	public void remove()
	{
		SceneManager.RENDER_THREAD.removeRenderable(entity);
		movement.clearPath();
		
	}
	
	public void buff(float duration)
	{
		if (buffDuration == 0)
		{
			stats.setStat("OldAtk", stats.getStat("Atk"));
			float newAttack = stats.getStat("Atk") * (100 + AttackUp.POWER_UP)/100.f;
			stats.setStat("Atk", (int)newAttack);
		}
		
		buffDuration = duration;
		
	}
	
	public String toString()
	{
		String res 	 = "hero\n";
		RoomElement currentElement = (RoomElement) board.getItem(this.getPos().add(new Vector2(entity.getWidth()/2, entity.getHeight()/2)));
		res			+= currentElement.getRoom().getIndexOfRoomElement(currentElement) + "\n";
		res			+= stats.getStat("Hp") + "\n";
		res			+= stats.getStat("MaxHp") + "\n";
		res			+= stats.getStat("Exp") + "\n";
		res			+= stats.getStat("NextExp") + "\n";
		res			+= stats.getStat("Level") + "\n";
		res			+= stats.getStat("HpPotions") + "\n";
		res 		+= buffDuration + "\n";
		return res;
	}
	
}
