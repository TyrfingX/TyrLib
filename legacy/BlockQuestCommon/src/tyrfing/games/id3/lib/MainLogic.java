package tyrfing.games.id3.lib;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import android.graphics.Color;
import android.graphics.Paint.Style;
import tyrfing.common.factory.FactoryManager;
import tyrfing.common.files.FileWriter;
import tyrfing.common.game.BaseGame;
import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.game.objects.GameObject;
import tyrfing.common.game.objects.Updater;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Animation;
import tyrfing.common.render.IFrameListener;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.games.id3.lib.World.Dungeon;
import tyrfing.games.id3.lib.mechanics.Floor;
import tyrfing.games.id3.lib.mechanics.Player;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.rooms.CheckBoard;
import tyrfing.games.id3.lib.rooms.Door;
import tyrfing.games.id3.lib.rooms.DoorType;
import tyrfing.games.id3.lib.rooms.Room;
import tyrfing.games.id3.lib.rooms.RoomElement;
import tyrfing.games.id3.lib.rooms.RoomFactory;
import tyrfing.games.id3.lib.rooms.RoomFactoryConfig;
import tyrfing.games.id3.lib.rooms.RoomState;
import tyrfing.games.id3.lib.rooms.Skript;
import tyrfing.games.id3.lib.rooms.content.AttackUp;
import tyrfing.games.id3.lib.rooms.content.Heal;
import tyrfing.games.id3.lib.rooms.content.Hero;
import tyrfing.games.id3.lib.rooms.content.Monster;
import tyrfing.games.id3.lib.rooms.content.MonsterFactory;
import tyrfing.games.id3.lib.rooms.content.MonsterType;
import tyrfing.games.id3.lib.rooms.content.StairsDown;
import tyrfing.games.id3.lib.rooms.content.StairsUp;

public class MainLogic extends Observable implements IFrameListener, Observer, ClickListener {

	private Updater updater;
	private Board board;
	
	public static final int SIZE_X = 11;
	public static final int SIZE_Y = 14;
	public static final int PADDING_X = 1;
	public static final int PADDING_Y = 5;
	public static float padX;
	
	public static final char RESUME_GAME = '1';
	public static final char DONT_RESUME_GAME = '0';
	public static final String RESUME_FILE = "resume.bs";
	
	private static final int BASE_MONEY_REWARD = 200;
	private static final int MONEY_PER_FLOOR_LEVEL = 100;
	
	private Rectangle border;
	
	private Node topLeft;
	private Node downRight;
	
	private float tileSize;
	
	private Room currentRoom;
	
	private Hero hero;
	
	private Preview preview;
	
	private Player character;
	
	private CheckBoard checkBoard;
	
	private Text endingMessage = null;
	
	private Button retreat;
	
	public int oldMoney;
	
	private Floor floor;
	
	public State state;
	
	private boolean startGame;
	private boolean running;
	private boolean exitingDungeon;
	
	private Stack<Animation> potions;
	
	private static MainLogic instance = null;
	
	private boolean boardChanged = false;
	
	private List<Skript> skripts;
	
	public int EXTRA = 0;
	
	private static final float SHOW_RETREAT_BUTTON_TIME = 3;
	private float showingRetreatButtonTime;
	
	private RoomFactoryConfig config;
	
	public MainLogic(State state, Player character, Floor floor, Settings settings) 
	{
		
		this.character = character;
		this.floor = floor;
		this.state = state;
		
		if (settings == null)
		{
			if (floor.getLevel() < 4)
			{
				Room.fallSpeed = 15;
				Room.SPEED_UP = 29;			
			}
			else if (floor.getLevel() < 10)
			{
				Room.fallSpeed = 20;
				Room.SPEED_UP = 22;			
			}
			else if (floor.getLevel() < 15)
			{
				Room.fallSpeed = 25;
				Room.SPEED_UP = 18;
			} 
			else if (floor.getLevel() < 20)
			{
					Room.fallSpeed = 30;
					Room.SPEED_UP = 14;
			}
			else if (floor.getLevel() < 30)
			{
					Room.fallSpeed = 35;
					Room.SPEED_UP = 12;
			}
			else 
			{
				Room.fallSpeed = 35;
				Room.SPEED_UP = 11;
				Room.fallSpeed += floor.getLevel()/5.f;
			}
		}
		else
		{
			Room.fallSpeed = settings.fallSpeed;
			Room.SPEED_UP = settings.speedUp;
		}
		
		updater = new Updater();
		SceneManager.RENDER_THREAD.addFrameListener(updater);
		
		tileSize = MainLogic.calcTileSize();
		board = new Board(SIZE_X, SIZE_Y, tileSize);
		
		padX = TargetMetrics.width - SIZE_X * tileSize;
		padX /= 2;
		
		topLeft = new Node(padX, tileSize * PADDING_Y);
		downRight = topLeft.createChild(tileSize * SIZE_X, tileSize * SIZE_Y);
		
		border = SceneManager.createRectangle(downRight.getRelativeX(), downRight.getRelativeY(), Color.GRAY, topLeft);
		border.getPaint().setStyle(Style.STROKE);
		border.getPaint().setStrokeWidth(10);
		border.setVisible(true);

		
		float powerUp = GlobalSettings.difficulty.getMonsterPowerUp();
		
		if (floor.getLevel() >= 21)
		{
			powerUp += 0.5f;
		}
		
		FactoryManager.registerFactory(MonsterType.ORC.toString(), new MonsterFactory(floor.getLevel(), MonsterType.ORC, updater, powerUp));
		if (floor.getLevel() > 4)
		{
			FactoryManager.registerFactory(MonsterType.ZOMBIE.toString(), new MonsterFactory(floor.getLevel(), MonsterType.ZOMBIE, updater, powerUp));
		}

		
		if (floor.getLevel() > 10)
		{
			FactoryManager.registerFactory(MonsterType.DEMON.toString(), new MonsterFactory(floor.getLevel(), MonsterType.DEMON, updater, powerUp));
		}
		
		/*
		if (floor.getLevel() > 17)
		{
			FactoryManager.registerFactory(MonsterType.WISP.toString(), new MonsterFactory(floor.getLevel(), MonsterType.WISP, updater, powerUp));
		}
		*/
		
		if (settings == null)
		{
			config = new RoomFactoryConfig();
			config.createDefaultShapes();
		}
		else
		{
			config = settings.roomFactoryConfig;
		}
		
		
		if (floor.getLevel() > 1)
		{
			config.allowAttackUp = true;
			if (floor.getLevel() > 2)
			{
				config.allowRedRooms = true;
			}
			else
			{
				config.STAIRS_DOWN_MIN_ROOMS = (3*config.STAIRS_DOWN_MIN_ROOMS)/4;
			}
		}
		else
		{
			config.STAIRS_DOWN_MIN_ROOMS = config.STAIRS_DOWN_MIN_ROOMS/2;
		}
		
		if (floor.getLevel() >= 25)
		{
			config.STAIRS_DOWN_MIN_ROOMS += 5;
		}
		

		
		if (floor.getLevel() > 6)
		{
			config.allowVioletRooms = true;
		}
		
		if (floor.getLevel() >= 5)
		{
			config.allowBlueRooms = true;
		}
		
		if (floor.getLevel() >= 22)
		{
			config.allowGreenDoors = true;
		}
		
		if (floor.getLevel() >= 18)
		{
			config.allowCursedRooms = true;
		}
		
		this.preview = new Preview(new Node(PADDING_X*tileSize,PADDING_Y*tileSize/3), new RoomFactory(board, topLeft, tileSize, config), 2);
		
		hero = character.createHero(topLeft.createChild(), board);
		updater.addItem(hero);
		hero.addObserver(this);
		
		checkBoard = new CheckBoard(board);
		CheckBoard.BASE_MONEY_PER_ROW = 15;
		CheckBoard.BASE_MONEY_PER_ROW += (floor.getLevel()-1)*2;
		CheckBoard.BASE_MONEY_PER_ROW += hero.getStats().getStat("ExtraMoney");
		
		retreat = WindowManager.createButton("Retreat", 0.75f*TargetMetrics.width, tileSize*2f, 90, 60, "Retreat");
		retreat.setVisible(false);
		retreat.addClickListener(this);
	
		oldMoney = character.getMoney();
		
		startGame = false;
		running = false;
		
		potions = new Stack<Animation>();
		for (int i = 0; i < hero.getStats().getStat("HpPotions"); ++i)
		{
			Node potionNode =  new Node(TargetMetrics.width /2 -  TargetMetrics.width*0.05f + TargetMetrics.width*0.05f*i,tileSize*(PADDING_Y+SIZE_Y+0.5f));
			Animation hpPotion = SceneManager.createAnimation(Ressources.getScaledBitmap("potion", new Vector2(TargetMetrics.width*0.05f*5, TargetMetrics.width*0.05f)), 5, 0, potionNode);
			potions.push(hpPotion);
		}
		
		instance = this;
		
		skripts = new ArrayList<Skript>();
	
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	public CheckBoard getCheckBoard()
	{
		return checkBoard;
	}
	
	public RoomFactory getRoomFactory()
	{
		return preview.factory;
	}
	
	public void addSkript(Skript skript)
	{
		skripts.add(skript);
		updater.addItem(skript);
	}
	
	public static float calcTileSize()
	{
		return Math.min(TargetMetrics.height / (SIZE_Y + PADDING_Y*2), TargetMetrics.width / (SIZE_X + 2 * PADDING_X));
	}
	
	public Updater getUpdater()
	{
		return updater;
	}
	
	public static void updateFallPreview()
	{
		if (instance != null) {
			instance.boardChanged = true;
		}
	}
	
	private void resetTempStats()
	{
		if (hero.isBuffed())
		{
			hero.getStats().setStat("Atk", hero.getStats().getStat("OldAtk"));
			state.atk.setCaptionColor(Color.BLACK);
		}
		
		hero.getStats().setStat("PotionFill", 0);
	}
	
	@Override
	public void onUpdate(float time) {
		
		if (!updater.isPaused())
		{
			checkBoard.checkDoors();
			int clearedRows = 0;
			do {
				clearedRows = checkBoard.clearRows(hero, state);
				if (clearedRows != 0)
				{
					character.setMoney(character.getMoney() + clearedRows * CheckBoard.BASE_MONEY_PER_ROW);
					state.playerMoney.setCaption(character.getMoney()+"");
					for (int i = 0; i < skripts.size(); ++i)
					{
						skripts.get(i).onClearRow();
					}
				}
			} while(clearedRows != 0);
			
			if (boardChanged)
			{
				if (currentRoom != null) {
					currentRoom.createFallPreview();
					boardChanged = false;
				}
			}
			
			
			if (retreat.getVisible()) {
				if (!hero.isOnStairsDown()) {
					showingRetreatButtonTime += time;
					if (showingRetreatButtonTime >= SHOW_RETREAT_BUTTON_TIME) {
						showingRetreatButtonTime = 0;
						retreat.setVisible(false);
					}
				}
			}
		}
		
		
		if (endingMessage != null)
		{
			if (!endingMessage.getVisible())
			{
				instance = null;
				checkBoard.destroyRoomsOnBoard();
				hero.remove();
				preview.destroy();
				
				if (currentRoom != null)
				{
					currentRoom.destroy();
				}
				
				SceneManager.RENDER_THREAD.removeFrameListener(this);
				SceneManager.RENDER_THREAD.removeFrameListener(updater);
				
				SceneManager.RENDER_THREAD.removeRenderable(border);
				topLeft.detachAllObjectsRecursivly();
				downRight.detachAllObjectsRecursivly();
				
				FactoryManager.unRegisterFactory(MonsterType.ORC.toString());
				FactoryManager.unRegisterFactory(MonsterType.ZOMBIE.toString());
				FactoryManager.unRegisterFactory(MonsterType.DEMON.toString());
				
				for (Animation potion : potions)
				{
					SceneManager.RENDER_THREAD.removeRenderable(potion);
				}
				
				this.setChanged();
				this.notifyObservers();
				this.deleteObservers();
			}
		}
		
		if (startGame)
		{
			running = true;
			startGame = false;
			if (currentRoom == null)
			{
				preview.generate();
				this.nextRoom();
			}
		}
		
		if (!running)
		{	
			if (!state.tutorial.isItemDone("Gameplay1"))
			{
				final String message = "Welcome to the first floor!\n\nConstruct the dungeon as good as possible\nfor your adventurer by placing\nthe falling blocks accordingly.\n\nRotate: Tap short.\nMirror: Tap long.";
				state.tutorial.createInfo(message, "Gameplay1");
				state.tutorial.doItem("Gameplay1");
			}
			else if (!state.tutorial.isItemDone("Gameplay2") && WindowManager.getWindow("Gameplay1") == null) {
				final String message = "Doors:\n\nThe red lines represent passageways\nfor your adventurer. If either block\nhas a red marking,\nthey will be connected.\n\nAlways create a path to advance!";
				state.tutorial.createInfo(message, "Gameplay2");
				state.tutorial.doItem("Gameplay2");
			} else if (!state.tutorial.isItemDone("Gameplay3") && WindowManager.getWindow("Gameplay1") == null && WindowManager.getWindow("Gameplay2") == null) {
				final String message = "Rooms:\n\nRooms will be cleared if the\nadventurer has interacted will all present\nobjects. If a row contains only tiles from\ncleared rooms, the row will be removed."; 
				state.tutorial.createInfo(message, "Gameplay3");
				state.tutorial.doItem("Gameplay3");
			}
			else if (WindowManager.getWindow("Gameplay1") == null && WindowManager.getWindow("Gameplay2") == null && WindowManager.getWindow("Gameplay3") == null)
			{
				if (state.deepestLevel == 1)
				{
					startGame = true;
				}
				else if (WindowManager.getWindow("Level2") == null)
				{
					
					if (state.deepestLevel >= 3 && !state.tutorial.isItemDone("Level2"))
					{
						final String message = "Red rooms:\n\nBeware the red rooms!\nAll enemies within and adjacent to them\nwill receive a significant power-up!"; 
						state.tutorial.createInfo(message, "Level2");
						state.tutorial.doItem("Level2");		

					} else if (state.deepestLevel >= 5 && !state.tutorial.isItemDone("BlueRooms")) {
							final String message = "Blue rooms:\n\nBlue rooms increase the \nreceived loot from a row.\n\nBeware: Blue rooms will be colored red\nshould they neighbour a\nred room."; 
							state.tutorial.createInfo(message, "BlueRoom");
							state.tutorial.doItem("BlueRooms");								
					} else if (WindowManager.getWindow("BlueRoom") == null) {
						if (state.deepestLevel >= 7 && !state.tutorial.isItemDone("VioletRoom"))
						{
							final String message = "Violet rooms:\n\nBeware the violet rooms!\nAll enemies within will receive\na significant power-up!\nAlso, neighbouring rooms will be\ncolored red!"; 
							state.tutorial.createInfo(message, "VioletRoom");
							state.tutorial.doItem("VioletRoom");								
						}
						else if (WindowManager.getWindow("VioletRoom") == null) {
							if (state.deepestLevel >= 18 && !state.tutorial.isItemDone("CursedRoom"))
							{
								final String message = "Cursed rooms:\n\nBeware the cursed rooms!\nThey cannot rooms be cleared, unless\ntheir color is changed to break\nthe curse.\n"; 
								state.tutorial.createInfo(message, "CursedRoom");
								state.tutorial.doItem("CursedRoom");								
							}
							else if (WindowManager.getWindow("CursedRoom") == null) {
								if (config.allowGreenDoors && !state.tutorial.isItemDone("GreenDoors"))
								{
									final String message = "Green Doors:\n\nBeware the green doors!\nThey only open up if matched\nwith another red or green door!"; 
									state.tutorial.createInfo(message, "GreenDoor");
									state.tutorial.doItem("GreenDoors");								
								}
								else if (WindowManager.getWindow("GreenDoor") == null)
								{
									startGame = true;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onClearRenderer() {
	}


	@Override
	public void update(Observable observable, Object data) {
		if (observable == currentRoom && !currentRoom.isFalling())
		{
			InputManager.removeTouchListener(currentRoom);
			if (!this.nextRoom())
			{
				this.gameOver();
			}
		}
		else if (observable == hero)
		{
			state.hp.setCaption(hero.getStats().getStat("Hp") + "/" + hero.getStats().getStat("MaxHp"));
			state.lvl.setCaption("Lvl. " + + hero.getStats().getStat("Level"));
			state.exp.setCaption("Exp: " + hero.getStats().getStat("Exp") + "/" + hero.getStats().getStat("NextExp"));
			
			state.atk.setCaption(hero.getStats().getStat("Atk")+"");
			if (hero.isBuffed())
			{
				state.atk.setCaptionColor(Color.RED);
			}
			else
			{
				state.atk.setCaptionColor(Color.BLACK);
			}
			
			int currentPotions = hero.getStats().getStat("HpPotions");
			
			if (potions.size() != 0)
			{
			
				for (int i = currentPotions; i < potions.size(); ++i)
				{
					Animation potion = potions.get(i);
					potion.setFrame(potion.getCountFrames() - 1);
				}
				
				if (potions.size() > currentPotions)
				{
					Animation potion = potions.get(hero.getStats().getStat("HpPotions"));
					int fill = hero.getStats().getStat("PotionFill");
					potion.setFrame(5 - fill - 1);
					if (fill >= 5)
					{
						hero.getStats().setStat("PotionFill", 0);
						hero.getStats().setStat("HpPotions", currentPotions + 1);
						potion.setFrame(0);
					}
				}
				
			}
			
			if (hero.getStats().getStat("Hp") <= 0)
			{
				character.setMoney(oldMoney);
				this.exitDungeon("Your follower died...");
			}
			else
			{	
				Monster monster = hero.killedAMob();
				if (monster != null)
				{
					for (Skript skript : skripts)
					{
						skript.onMobDies(monster);
					}
				}
				
				if (hero.clearedARoom())
				{
					for (Skript skript : skripts)
					{
						RoomElement element = hero.getCurrentElement();
						if (element != null) {
							skript.onClearRoom(element.getRoom());
						}
					}
				}
				
				if (hero.isOnStairsUp())
				{
					showingRetreatButtonTime = 0;
					retreat.setVisible(true);
				}
				else
				{
					if (hero.isOnStairsDown())
					{
						floor.setCleared(true);
						this.resetTempStats();
						character.synchWithHero(hero);
						this.reward(MainLogic.BASE_MONEY_REWARD + MainLogic.MONEY_PER_FLOOR_LEVEL * (floor.getLevel()-1) + character.getStats().getStat("ExtraMoneyFloor") + EXTRA);
						for (Skript skript : skripts)
						{
							skript.onFinishFloor();
						}
						this.exitDungeon("Gratulations! Floor cleared!");
					}
				}
			}
		}
		
	}
	
	private void gameOver()
	{
		character.setMoney(oldMoney);
		this.resetTempStats();
		character.synchWithHero(hero);
		this.exitDungeon("Your follower is lost within the dungeon...");
	}
	
	public boolean nextRoom()
	{
		currentRoom = preview.step();
		if (currentRoom.overlaps()) return false;
		currentRoom.addObserver(this);
		InputManager.addTouchListener(currentRoom);
		updater.addItem(currentRoom);
		currentRoom.createFallPreview();
		
		for (Skript skript : skripts)
		{
			skript.onSpawnRoom(currentRoom);
		}
		
		return true;
	}

	@Override
	public void onClick(Event event) {
		if (event.getEvoker() == retreat)
		{
			if (hero.getStats().getStat("Hp") > 0)
			{
				retreat.disable();
				this.resetTempStats();
				character.synchWithHero(hero);
				this.exitDungeon("You have decided to retreat.");
			}
		}
	}
	
	public void exitDungeon(String message)
	{
		if (!exitingDungeon)
		{
			this.resetTempStats();
			if (currentRoom != null)
			{
				InputManager.removeTouchListener(currentRoom);
			}
			character.getStats().setStat("HpPotions", hero.getStats().getStat("HpPotions"));
			exitingDungeon = true;
			updater.clear();
			this.endingMessage = SceneManager.createText(message, Color.BLACK, new Node(TargetMetrics.width * 0.1f,TargetMetrics.height*0.4f));
			endingMessage.setSize(35);
			endingMessage.fadeOut(new Vector2(-50, 0), 10);
			endingMessage.setPriority(10000);		
		}
	}
	
	public void reward(int money)
	{
		Text rewardMessage = SceneManager.createText("+" + money, Color.YELLOW, new Node(TargetMetrics.width * 0.25f,TargetMetrics.height*0.3f));
		rewardMessage.setSize(35);
		rewardMessage.fadeOut(new Vector2(0, -20), 10);
		rewardMessage.setPriority(10000);	
		
		Image moneyImg = SceneManager.createImage(Ressources.getScaledBitmap("money", new Vector2(50,50)), rewardMessage.getParent());
		moneyImg.fadeOut(new Vector2(0,-20), 10);
		moneyImg.setPriority(10000);
		
		state.character.setMoney(state.character.getMoney() + money);
	}
	
	public static void save()
	{
		
		String res = "";
	
		/***************************************************************************************
		 * 	Save data using the following format
		 * Flag for game resumption
		 * floorNumber
		 * money
		 * countSpawnedRooms
		 * countRooms
		 * room1
		 * room2
		 * room3
		 */
		
		/****************************************************************************************
		 * Save format for a room:
		 * x
		 * y
		 * red
		 * originalRed
		 * cleared
		 * countRoomElements
		 * roomElement1
		 * roomElement2
		 * roomElement3
		 * roomElement1DoorTop
		 * roomElement1DoorRight
		 * countRoomContent
		 * content1
		 * content2
		 * content3
		 */
		
		
		/***************************************************************************************
		 * Save format for a room-Element:
		 * x
		 * y
		 */

		
		/****************************************************************************************
		 * Save format for a content element:
		 * type
		 * roomElementNumber
		 * additional data
		 */
		
		if (instance != null)
		{
			instance.updater.pause();
			res += RESUME_GAME + "\n";
			res += instance.floor.getLevel() + "\n";
			res += instance.character.getMoney() + "\n";
			res += instance.preview.factory.getCountSpawnedRooms() + "\n";
		
			List<Room> rooms = instance.checkBoard.getRoomsOnBoard();
			rooms.add(instance.currentRoom);
			rooms.add(instance.preview.step());
			rooms.add(instance.preview.step());
			res += rooms.size() + "\n";
			for (Room room : rooms)
			{
				res += room.getNode().getRelativeX() + "\n";
				res += room.getNode().getRelativeY() + "\n";
				res += room.getState().ordinal() + "\n";
				res += room.isCleared() + "\n";
				
				res += room.countRoomElements() + "\n";
				for (int i = 0; i < room.countRoomElements(); ++i)
				{
					RoomElement element = room.getRoomElement(i);
					res += element.getNode().getRelativeX() + "\n";
					res += element.getNode().getRelativeY() + "\n";
				}
				
				for (int i = 0; i < room.countRoomElements(); ++i)
				{
					RoomElement element = room.getRoomElement(i);
					for (Direction direction : Direction.values())
					{
						res += element.hasDoor(direction) + "\n";
					}
				}
				
				int roomContent = room.countTotalContent();
				res += roomContent + "\n";
				for (int j = 0; j < roomContent; ++j)
				{
					GameObject content = room.getContent(j);
					res += content.toString();
				}
			}
		
		}
		else
		{
			res += DONT_RESUME_GAME;
		}
		
		FileWriter.writeFile(BaseGame.CONTEXT, RESUME_FILE, res);
		MainLogic.instance = null;
	}
	
	public static void resume(String data, State state)
	{
		String[] tokens = data.split("\n");
		Floor floor = new Floor(Integer.valueOf(tokens[1]));
		state.currentFloor = floor;
		
		MainLogic mainLogic = new MainLogic(state, state.character, floor, null);
		mainLogic.updater.pause();
		
		int money = Integer.valueOf(tokens[2]);
		mainLogic.character.setMoney(money);
		state.playerMoney.setCaption(state.character.getMoney()+"");
		
		int spawnedRooms = Integer.valueOf(tokens[3]);
		mainLogic.preview.factory.setCountSpawnedRooms(spawnedRooms);
		
		int countRooms = Integer.valueOf(tokens[4]);
		int row = 5;
		for (int i = 0; i < countRooms; ++i)
		{
			float x = Float.valueOf(tokens[row++]);
			float y = Float.valueOf(tokens[row++]);
			Room room = new Room(mainLogic.topLeft.createChild(x,y), mainLogic.board);
			int roomState = Integer.valueOf(tokens[row++]);
			boolean cleared = Boolean.valueOf(tokens[row++]);
			
			int countElements = Integer.valueOf(tokens[row++]);
			for (int j = 0; j < countElements; ++j)
			{
				float elementX = Float.valueOf(tokens[row++]);
				float elementY = Float.valueOf(tokens[row++]);
				RoomElement element = new RoomElement(	room.getNode().createChild(elementX, elementY), 
														mainLogic.board.getTileSize());
				room.addElement(element);
			}
			
			room.finishConstruction();
			
			for (int j = 0; j < countElements; ++j)
			{
				RoomElement element = room.getRoomElement(j);
				for (Direction direction : Direction.values())
				{
					if (Boolean.valueOf(tokens[row++])){
						element.addDoor(new Door(direction, DoorType.RED));
					}					
				}
			}
			
			
			int countContent = Integer.valueOf(tokens[row++]);
			for (int j = 0; j < countContent; ++j)
			{
				String type = tokens[row++];
				int roomElementNr = Integer.valueOf(tokens[row++]);
				RoomElement roomElement = room.getRoomElement(roomElementNr);
				if (type.equals("heal"))
				{
					Heal heal = new Heal();
					heal.assignRoom(roomElement);
				} else if (type.equals("monster")) {
					String monsterType = MonsterType.values()[Integer.valueOf(tokens[row++])].toString();
					Monster monster = (Monster) FactoryManager.getFactory(monsterType).create();
					monster.assignRoom(roomElement);
					monster.createEntity(mainLogic.board.getTileSize());
				} else if (type.equals("hero")) {
					mainLogic.hero.spawn(roomElement);
					int hp = Integer.valueOf(tokens[row++]);
					int maxHp = Integer.valueOf(tokens[row++]);
					int exp = Integer.valueOf(tokens[row++]);
					int nextExp = Integer.valueOf(tokens[row++]);
					int lvl = Integer.valueOf(tokens[row++]);
					int potions = Integer.valueOf(tokens[row++]);
					float buffed = Float.valueOf(tokens[row++]);
					mainLogic.hero.getStats().setStat("Hp", hp);
					mainLogic.hero.getStats().setStat("MaxHp", maxHp);
					mainLogic.hero.getStats().setStat("Exp", exp);
					mainLogic.hero.getStats().setStat("NextExp", nextExp);
					mainLogic.hero.getStats().setStat("Level", lvl);
					mainLogic.hero.getStats().setStat("HpPotions", potions);
					if (buffed != 0) {
						mainLogic.hero.buff(buffed);
						state.atk.setCaption(mainLogic.hero.getStats().getStat("Atk")+"");
						state.atk.setCaptionColor(Color.RED);
					}
					
					state.hp.setCaption(mainLogic.hero.getStats().getStat("Hp") + "/" + mainLogic.hero.getStats().getStat("MaxHp"));
					state.lvl.setCaption("Lvl. " + + mainLogic.hero.getStats().getStat("Level"));
					state.exp.setCaption("Exp: " + mainLogic.hero.getStats().getStat("Exp") + "/" + mainLogic.hero.getStats().getStat("NextExp"));
					
					while (mainLogic.potions.size() > mainLogic.hero.getStats().getStat("HpPotions"))
					{
						Renderable potion = mainLogic.potions.pop();
						SceneManager.RENDER_THREAD.removeRenderable(potion);
					}
				} else if (type.equals("stairsDown")) {
					StairsDown stairs = new StairsDown();
					stairs.assignRoom(roomElement);
				}else if (type.equals("stairsUp")) {
					StairsUp stairs = new StairsUp();
					stairs.assignRoom(roomElement);
				} else if (type.equals("attackUp")) {
					AttackUp attackUp = new AttackUp();
					attackUp.assignRoom(roomElement);
				}
			}
			
			room.setState(RoomState.values()[roomState]);
			
			
			if (i < countRooms - 3)
			{
				mainLogic.updater.addItem(room);
				room.makePartOfDungeon();
				if (cleared) room.cleared();
			}
			else
			{
				if (i == countRooms - 3)
				{
					//Currently falling room
					mainLogic.currentRoom = room;
					room.addObserver(mainLogic);
					mainLogic.updater.addItem(room);
					InputManager.addTouchListener(room);
					room.createFallPreview();
				}
				else
				{
					//preview rooms
					mainLogic.preview.addRoom(room);
				}
			}
		}
		
		
		
		
		Dungeon dungeon = (Dungeon) state.worldMap.getLocation("Dungeon");
		mainLogic.addObserver(dungeon);
		SceneManager.RENDER_THREAD.addFrameListener(mainLogic);
		mainLogic.updater.unPause();
	}

}
