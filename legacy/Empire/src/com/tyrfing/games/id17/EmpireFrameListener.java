package com.tyrfing.games.id17;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.gui.CameraController;
import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.gui.Scoreboard;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.war.FormationWindow;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrfing.games.id17.networking.JoinGame;
import com.tyrfing.games.id17.networking.MapInfo;
import com.tyrfing.games.id17.networking.RequestWorld;
import com.tyrfing.games.id17.save.WorldFactoryMapFile;
import com.tyrfing.games.id17.save.WorldFactorySaveFile;
import com.tyrfing.games.id17.startmenu.HostedGameUpdate;
import com.tyrfing.games.id17.startmenu.Menu;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.compositors.BloomComposit;
import com.tyrlib2.graphics.compositors.DoFComposit;
import com.tyrlib2.graphics.compositors.Precision;
import com.tyrlib2.graphics.compositors.SSAOComposit;
import com.tyrlib2.graphics.lighting.DirectionalLight;
import com.tyrlib2.graphics.lighting.Light.Type;
import com.tyrlib2.graphics.renderables.Skybox;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IErrorHandler;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IKeyboardListener;
import com.tyrlib2.input.IScrollListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.networking.Network;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.FPSCounter;
import com.tyrlib2.util.Options;

public class EmpireFrameListener implements IFrameListener, INetworkListener {

	public static final int PC_TARGET = 1;
	public static final int ANDROID_TARGET = 0;
	
	public static final int CAN_HOST = 1;
	public static final int CANNOT_HOST = 2;
	
	public static int BUILD_TARGET = ANDROID_TARGET;
	
	private World world;
	private SceneNode camNode;
	
	private boolean firstUpdate = false;
	
	public static final Vector2 NATIVE_SIZE = new Vector2(800, 480);
	
	private float[] atlasScales;
	
	public static EmpireFrameListener MAIN_FRAME;
	public CameraController camController;
	
	private MainGUI mainGUI;
	
	private Menu startMenu;
	
	private Network network;
	
	private ChatListener chat;
	
	public static final int DEFAULT_PORT = 52869;
	public static int SERVER_PORT = DEFAULT_PORT;
	
	private IScrollListener zoomer;
	private ScrollListener sl;
	
	private Network matchmakerNetwork;
	
	public static final float MATCHMAKER_UPDATE_TIME = 1;
	
	private float passedTime;
	
	public enum GameState {
		START,
		SELECT,
		MAIN,
	}
	
	public static GameState state;
	
	private float bandWidthCheckTime;
	private Scoreboard board;
	private int port;
	
	public static final float KEY_ZOOM = 0.2f;
	
	public static final String SERVER_ADDRESS = "www.swordscroll.com";
	private static boolean LOG_BANDWIDTH = false;
	public boolean randomJoin;
	
	public EmpireFrameListener(float[] atlasScales) {
		this.atlasScales = atlasScales;
		MAIN_FRAME = this;
		state = GameState.START;
		
		if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.ANDROID_TARGET) {
			CameraController.STRATEGIC_VIEW_HEIGHT = 150;
			CameraController.ROTATE_SPEED = 12;
		}
	}
	
	@Override
	public void onSurfaceCreated() {
		
		SceneManager.getInstance().setAmbientLight(new Color(0.22f,0.22f,0.17f,0));
		
		/* Next we setup some directional lighting to make the scene a bit better looking*/
		DirectionalLight light = (DirectionalLight) SceneManager.getInstance().createLight(Type.DIRECTIONAL_LIGHT);
		light.setLightDirection(new Vector3(1,-1,-1));
		light.setIntensity(1.0f);
		final SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0));
		node.attachSceneObject(light);
		TextureManager.getInstance().createTexture("CASTLE", Media.CONTEXT.getResourceID("castle", "drawable"));
		TextureManager.getInstance().createTexture("knight", Media.CONTEXT.getResourceID("knight", "drawable"));
		TextureManager.getInstance().createTexture("HORSE", Media.CONTEXT.getResourceID("horse", "drawable"));
		TextureManager.getInstance().createTexture("house1", Media.CONTEXT.getResourceID("house1", "drawable"));
		TextureManager.getInstance().createTexture("house2", Media.CONTEXT.getResourceID("house2", "drawable"));
		TextureManager.getInstance().createTexture("SHROOM1", Media.CONTEXT.getResourceID("shroom1", "drawable"));
		TextureManager.getInstance().createTexture("MINE", Media.CONTEXT.getResourceID("mine", "drawable"));
		TextureManager.getInstance().createTexture("TREE1", Media.CONTEXT.getResourceID("tree1", "drawable"));
		TextureManager.getInstance().createTexture("TREE1_WIN", Media.CONTEXT.getResourceID("tree1win", "drawable"));
		TextureManager.getInstance().createTexture("WINDMILL", Media.CONTEXT.getResourceID("windmill", "drawable"));
		TextureManager.getInstance().createTexture("ARROW_HEAD", Media.CONTEXT.getResourceID("arrowhead", "drawable"));
		TextureManager.getInstance().createTexture("ARROW_BLOCK", Media.CONTEXT.getResourceID("arrowblock", "drawable"));
		TextureManager.getInstance().createTexture("GRASS_BLADES", Media.CONTEXT.getResourceID("grass_blades", "drawable"));
		TextureManager.getInstance().createTexture("GRAIN", Media.CONTEXT.getResourceID("grain", "drawable"));
		TextureManager.getInstance().createTexture("SMOKE", Media.CONTEXT.getResourceID("smoke", "drawable"));
		TextureManager.getInstance().createTexture("BRIGHT_SMOKE", Media.CONTEXT.getResourceID("brightsmoke", "drawable"));
		TextureManager.getInstance().createTexture("CATTLE", Media.CONTEXT.getResourceID("cattle", "drawable"));
		TextureManager.getInstance().createTexture("WATER", Media.CONTEXT.getResourceID("water", "drawable"));
		TextureManager.getInstance().createTexture("ICE", Media.CONTEXT.getResourceID("ice", "drawable"));
		TextureManager.getInstance().createTexture("FOG", Media.CONTEXT.getResourceID("fog", "drawable"));
		TextureManager.getInstance().createTexture("WHITE", Media.CONTEXT.getResourceID("white", "drawable"));
		TextureManager.getInstance().createTexture("VILLAGER", Media.CONTEXT.getResourceID("aldeano", "drawable"));
		
		TextureManager.getInstance().createTexture("MAIN_GUI", Media.CONTEXT.getResourceID("scheme", "drawable"));
		TextureAtlas.fromXMLFile("atlas/maingui.xml", atlasScales[0]);
		
		TextureManager.getInstance().createTexture("MAIL_ICONS", Media.CONTEXT.getResourceID("messageicons", "drawable"));
		TextureAtlas.fromXMLFile("atlas/messageicons.xml");
		
		TextureManager.getInstance().createTexture("MORAL", Media.CONTEXT.getResourceID("moral", "drawable"));
		TextureManager.getInstance().createTexture("MORALALLY", Media.CONTEXT.getResourceID("moralally", "drawable"));
		TextureManager.getInstance().createTexture("MORALENEMY", Media.CONTEXT.getResourceID("moralenemy", "drawable"));
		TextureAtlas.fromXMLFile("atlas/moral.xml");
		
		TextureManager.getInstance().createTexture("PROGRESS", Media.CONTEXT.getResourceID("progress", "drawable"));
		TextureAtlas.fromXMLFile("atlas/progress.xml");
		
		TextureManager.getInstance().createTexture("TECHARROW", Media.CONTEXT.getResourceID("techarrow", "drawable"));
		TextureAtlas.fromXMLFile("atlas/techarrow.xml");
		
		TextureManager.getInstance().createTexture("UNIT_ICONS", Media.CONTEXT.getResourceID("unit_icons", "drawable"));
		TextureAtlas.fromXMLFile("atlas/unit_icons.xml");

		TextureManager.getInstance().createTexture("GOODS", Media.CONTEXT.getResourceID("goods", "drawable"));
		TextureAtlas.fromXMLFile("atlas/goods.xml", atlasScales[0]);
		
		TextureManager.getInstance().createTexture("BUILDINGS", Media.CONTEXT.getResourceID("buildings", "drawable"));
		TextureAtlas.fromXMLFile("atlas/buildings.xml", atlasScales[0]);
		
		TextureManager.getInstance().createTexture("TECH", Media.CONTEXT.getResourceID("techs", "drawable"));
		TextureAtlas.fromXMLFile("atlas/tech.xml", atlasScales[0]);
		
		TextureManager.getInstance().createTexture("TRAITS", Media.CONTEXT.getResourceID("traits", "drawable"));
		TextureAtlas.fromXMLFile("atlas/traits.xml");

		
		float scaleWeight = 1.2f;
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		float xScale = scaleWeight * NATIVE_SIZE.x / screenSize.x + 1-scaleWeight;
		float yScale = scaleWeight * NATIVE_SIZE.y / screenSize.y + 1-scaleWeight;
		
		float scaleWeight2 = 0.5f;
		float xScale2 = scaleWeight2 * NATIVE_SIZE.x / screenSize.x + 1-scaleWeight2;
		float yScale2 = scaleWeight2 * NATIVE_SIZE.y / screenSize.y + 1-scaleWeight2;	
		
		float scaleWeight3 = 0.4f;
		float xScale3 = scaleWeight3 * NATIVE_SIZE.x / screenSize.x + 1-scaleWeight3;
		
		float scaleWeight4 = 1.2f;
		float xScale4 = scaleWeight4 * NATIVE_SIZE.x / screenSize.x + 1-scaleWeight4;
		float yScale4 = scaleWeight4 * NATIVE_SIZE.y / screenSize.y + 1-scaleWeight4;	
		

		float scaleWeight5 = 0.7f;
		float xScale5 = scaleWeight5 * screenSize.x / NATIVE_SIZE.x + 1-scaleWeight5;
		float yScale5 = scaleWeight5 * screenSize.y / NATIVE_SIZE.y + 1-scaleWeight5;	
		
		float scaleWeight6 = 0.8f;
		float xScale6 = scaleWeight6 * 1600 / screenSize.x  + 1-scaleWeight5;
		float yScale6 = scaleWeight6 * 900 / screenSize.y+ 1-scaleWeight5;	
		
		WindowManager.getInstance().setScales(new Vector2[] {
			new Vector2(xScale, yScale),
			new Vector2(xScale2, yScale2),
			new Vector2(xScale3, 1),
			new Vector2(xScale4, yScale4),
			new Vector2(xScale5, yScale5),
			new Vector2(xScale6 * 1/1600f, yScale6 * 1/900f)
		});
		
		int fontOffset = 0;
		if (screenSize.x < 1600) {
			fontOffset = -1;
		} else if (screenSize.x < 1024) {
			fontOffset = -2;
		} else if (screenSize.x <= 800) {
			fontOffset = -3;
		}
		
		SceneManager.getInstance().loadFont("Roboto-Regular.ttf", "FONT_14", 14 + fontOffset);
		SceneManager.getInstance().loadFont("Roboto-Regular.ttf", "FONT_16", 18 + fontOffset);
		SceneManager.getInstance().loadFont("Roboto-Regular.ttf", "FONT_20", 22 + fontOffset);
		
		WindowManager.getInstance().loadSkin(new Skin());	
		
		/* Finally we need to create a camera capturing the scene */
		if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.PC_TARGET) {
			camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(-60,116,76));
		} else {
			camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(-18,32,24).multiply(1.5f));
		}
		Camera camera = SceneManager.getInstance().createCamera(CameraController.INIT_LOOK_VECTOR, new Vector3(0,0,1), camNode);
		camera.use();
		
		Skin skin = WindowManager.getInstance().getSkin();
		skin.FRAME_BOTTOM = "SMALL_RECT_BOTTOM";
		skin.FRAME_TOP = "SMALL_RECT_TOP";
		skin.FRAME_LEFT = "SMALL_RECT_LEFT";
		skin.FRAME_RIGHT = "SMALL_RECT_RIGHT";
		skin.FRAME_BOTTOMLEFT = "SMALL_RECT_BOTTOM_LEFT";
		skin.FRAME_TOPRIGHT = "SMALL_RECT_TOP_RIGHT";
		skin.FRAME_BOTTOMRIGHT = "SMALL_RECT_BOTTOM_RIGHT";
		skin.FRAME_TOPLEFT = "SMALL_RECT_TOP_LEFT";
		skin.FRAME_MIDDLE = "WOOD_BRUSH";
		skin.FRAME_MIDDLE_REPEAT = new Vector2(28, 10);
		skin.TEXTURE_ATLAS = "MAIN_GUI";
		skin.FRAME_BORDER_SIZE = 0.0075f * (0.5f * xScale + 0.5f * yScale);
		skin.LABEL_FONT = "FONT_16";
		skin.PROGRESS_BAR_BG_PAINT = TabGUI.GOLDEN_WOOD_PAINT;
		skin.PROGRESS_BAR_COLOR = new Color(FormationWindow.GREEN.r*0.7f, FormationWindow.GREEN.g*0.7f, FormationWindow.GREEN.b*0.7f, FormationWindow.GREEN.a*0.7f);

		
		SceneManager.getInstance().getRenderer().errorHandler = new IErrorHandler() {
			@Override
			public void onError() {
				if (network != null) {
					network.close();
				}
				throw new RuntimeException("Fatal error!");
			}
		};
		
		Skybox sky = SceneManager.getInstance().createSkybox("FOG", new Vector3(2000, 2000, 2000));
		camNode.attachSceneObject(sky);
		
		if (BUILD_TARGET == PC_TARGET) {
			int[] textureSizes = { 2*2048, 2048, 1024 };
			float[] distances = { 300, 1000, 2000 };
			SceneManager.getInstance().getRenderer().setShadowsEnabled(true, SceneManager.getInstance().getLight(0), textureSizes, distances);
		} else {
			//int[] textureSizes = { 512, 256, 128 };
			//float[] distances = { 300, 1000, 2000 };
			//SceneManager.getInstance().getRenderer().setShadowsEnabled(true, SceneManager.getInstance().getLight(0), textureSizes, distances);
		}
		
		Updater fpsUpdater = new Updater();
		fpsUpdater.addItem(new FPSCounter());
		SceneManager.getInstance().addFrameListener(fpsUpdater);
		
	}

	@Override
	public void onSurfaceChanged() {
		
		Options opt = Media.CONTEXT.getOptions();
		
		if ((Boolean)opt.getSetting(Media.DEPTH_TEXTURES_ENABLED)) {
			Precision precision = (Precision) opt.getSetting(Media.PRECISION);
			SceneManager.getInstance().getRenderer().enableOffscreenRendering(precision);

			//SceneManager.getInstance().getRenderer().addComposit(new DeferredLighting());
			SceneManager.getInstance().getRenderer().addComposit(new BloomComposit((Integer)opt.getSetting(EmpireActivity.BLOOM_LEVELS), (Integer)opt.getSetting(EmpireActivity.BLOOM_DOWNSCALE)));
			SceneManager.getInstance().getRenderer().addComposit(new SSAOComposit(new Color(0.1f,0.1f,0.1f,1)));
			SceneManager.getInstance().getRenderer().addComposit(new DoFComposit());
		}
	}

	@Override
	public void onFrameRendered(float time) {
		if (!firstUpdate) {
			firstUpdate = true;
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				startMenu = new Menu();
			}
			
			LawSet.init();
			
			if (SceneManager.getInstance().getRenderer().isInServerMode()) {
				hostGame("small", true, true);
			} 
			
			//WindowManager.getInstance().createImageBox("Test", new Vector2(0, -1f), SceneManager.getInstance().getRenderer().getShadowMapHandle(), new Vector2(1, 1));
			
			//world.saveAs("test");
			//world = new WorldDeserializeFactory("test").create();
		}
		
		if (network != null && LOG_BANDWIDTH) {
			bandWidthCheckTime += time;
			if (bandWidthCheckTime >= 5) {
				
				System.out.println("Upstream: " + network.pollSentBytes() / bandWidthCheckTime + " byte/s");
				System.out.println("Downstream: " + network.pollReceivedBytes() / bandWidthCheckTime + " byte/s");
				bandWidthCheckTime = 0;
			}
		}
		
		if (camController != null) {
			camController.onUpdate(time);
		}
		
		if (world != null && matchmakerNetwork != null && matchmakerNetwork.isClient()) {
			passedTime += time;
			if (passedTime >= MATCHMAKER_UPDATE_TIME) {
				HostedGameUpdate u = new HostedGameUpdate();
				if (getPlayers() != null) {
					u.players = getPlayers().size();
				}
				u.maxPlayers = World.getInstance().getRankedHouses().size();
				u.port = port;
				matchmakerNetwork.send(u, 0);
				passedTime = 0;
			}
		}
	}
	
	public void hostGameFromSave(String save) {

		if (world != null) {
			world.destroy();
		}
		
		//network.host(SERVER_PORT);
		
		world = new WorldFactorySaveFile(save).create();
		
		startGame();
		joinGame();
	}
	
	public void createWorld(String mapName) {
		System.out.println("Creating a world...");
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			TextureManager.getInstance().createTexture("SIGILS1", Media.CONTEXT.getResourceID("sigils" + mapName, "drawable"));
			TextureAtlas.fromXMLFile("maps/" + mapName + "/sigils1.xml", atlasScales[0]);
		}
		
		world = new WorldFactoryMapFile(mapName).create();
		chat = new ChatListener();
		board = new Scoreboard();
		
		System.out.println(" ...successful");
	}
	
	public void loadWorld(String mapName) {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			TextureManager.getInstance().createTexture("SIGILS1", Media.CONTEXT.getResourceID("sigils" + mapName, "drawable"));
			TextureAtlas.fromXMLFile("maps/" + mapName + "/sigils1.xml", atlasScales[0]);
		}
		chat = new ChatListener();
		board = new Scoreboard();
		network = new Network();
	}
	
	public void setupHostNetwork() {
		if (network == null) {
			network = new Network();
			port = SERVER_PORT;
			network.host(port);
			network.setLog(false);
			network.setMeasureBandwithUse(false);
		}
	}
	
	public void hostGame(String mapName, boolean allowJoins, boolean randomJoin) {
		
		this.randomJoin = randomJoin;
		
		createWorld(mapName);
		setupHostNetwork();
		
		world.pause();
		
		network.addListener(chat);
		network.addListener(board);
		
		if (allowJoins) {
			System.out.print("Setting up networking...");
			network.addListener(world);
			backConnect(3001, SERVER_ADDRESS);
			System.out.println(" ...successful");
		} 
		
		System.out.print("Starting game...");
		startGame();
		System.out.println(" ...successful");

		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			world.nextPlayerID++;
			world.players.add(world.getPlayerController());
			
			if (randomJoin) {
				joinGame();
			}
		}

	
	}
	
	public void startGame() {
		if (!randomJoin) {
			startGame(0, world.getPlayerController().getHouse() == null ? world.getHighestPotentialHouse() : world.getPlayerController().getHouse().id);
		} else {
			startGame(0, world.getPlayerController().getHouse() == null ? world.getRandomHouse() : world.getPlayerController().getHouse().id);
		}
	}
	
	public void startGame(int playerID, int houseID) {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			startMenu.hide();
			state = GameState.SELECT;		
			
			board.show();
			
			SceneManager.getInstance().addFrameListener(chat);
			InputManager.getInstance().addKeyboardListener(chat);
			SceneManager.getInstance().addFrameListener(board);
			
			InputManager.getInstance().addKeyboardListener(new IKeyboardListener() {
				@Override
				public boolean onPress(IKeyboardEvent e) {
					char c = e.getKeyChar();
					if (c == 'q' || c == 'Q') {
						world.saveAs("save");
						chat.addChatEntry("<< Quicksave successful >>");
					} else if (c == 'l' || c == 'L') {
						//world.destroy();
					} else if (e.getKeyCode() == InputManager.VK_ESC) {
						Media.CONTEXT.quit();
					} else if (e.getKeyCode() == InputManager.VK_SPACE) {
						if (network.isHost()) {
							ImageBox play = (ImageBox) WindowManager.getInstance().getWindow("DATE_WINDOW/PLAY_BUTTON");
							if (world.isPaused()) {
								world.unpause();
								play.setAtlasRegion("PAUSE");
							} else {
								world.pause();
								play.setAtlasRegion("PLAY");
							}
						}
					} else if (e.getKeyCode() == InputManager.VK_PLUS) {
						camController.zoom(KEY_ZOOM);
					} else if (e.getKeyCode() == InputManager.VK_MINUS) {
						camController.zoom(-KEY_ZOOM);
					} 
					
					//InputManager.getInstance().isKeyPressed(InputManager.)
					
					return false;
				}

				@Override
				public boolean onRelease(IKeyboardEvent e) {
					return false;
				}

				@Override
				public long getPriority() {
					// TODO Auto-generated method stub
					return 0;
				}
			
			});
			
			camController = new CameraController(camNode);
			InputManager.getInstance().addTouchListener(camController);
			InputManager.getInstance().addKeyboardListener(camController);
			
			Vector3 pos = SceneManager.getInstance().getActiveCamera().getAbsolutePos();
			camController.speed.speed = pos.z * ScrollListener.SCROLL_SPEED;
			
			if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.PC_TARGET) {
				zoomer = new IScrollListener() {
					
					public static final float MIN_CAM_POS = 10;
					
					@Override
					public boolean onScroll(float rotation) {
						float zoom = rotation/3;
						
						Camera camera = SceneManager.getInstance().getActiveCamera();
						Vector3 pos = camera.getAbsolutePos();
						
						if (pos.z < MIN_CAM_POS && zoom > 0) return true;
						
						camController.zoom(zoom);
						camController.speed.speed = pos.z * ScrollListener.SCROLL_SPEED;
						
						return true;
					}
					
				};
				InputManager.getInstance().addScrollListener(zoomer);
				sl = new ScrollListener();
				InputManager.getInstance().addMoveListener(sl);
				SceneManager.getInstance().getRenderer().addFrameListener(sl);
			}
			
			world.getPlayerController().playerID = playerID;
			world.getPlayerController().control(world.getHouses().get(houseID));
			House house = world.getPlayerController().getHouse();
			camController.focus(house.getHoldings().get(0).holdingData.worldEntity.getParent());
			
			mainGUI = new MainGUI();
			mainGUI.display();
			world.mainGUI = mainGUI;
			
		} else {
			joinGame();
		}

	}
	
	public void joinGame() {
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
		
			world.mainGUI.mainDisplay();
			
			for (int i = 0; i < World.getInstance().players.size(); ++i) {
				World.getInstance().players.get(i).unmarkControlledHouse();
			}
			
			network.broadcast(new JoinGame(World.getInstance().getPlayerController().getHouse().id, World.getInstance().getPlayerController().playerID));
			
		}
		
		if (network.isClient()) {
			
		} else {
			AIThread.create();
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				House house = World.getInstance().getHouses().get(i);
				if (house.getController() instanceof AIController) {
					AIThread.getInstance().addAI((AIController)house.getController());
				}
			}
			AIThread.getInstance().start();
			world.unpause();
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				board.addPlayer(0, world.getPlayerController().getHouse().id);
				world.getPlayerController().hasJoined = true;
			}
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			world.getPlayerController().getHouse().setHouseController(world.getPlayerController());
		} else {
			System.out.println("... successful");
		}
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			world.getHouses().get(i).findVisibleBaronies();
			world.getHouses().get(i).updateNeighbours();
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			world.getMap().getFogMap().build();
		}
		
		world.getPlayerController().unmarkControlledHouse();
		
		state = GameState.MAIN;
		
	}
	
	public void connectToGame(String adress) {
		
		String split[] = adress.split(":");
		
		int port = DEFAULT_PORT;
		
		if (split.length > 1) {
			port = Integer.valueOf(split[1]);
		}
		
		network = new Network();
		network.addListener(this);
		network.setLog(false);
		boolean success = false;
		
		try {
			network.connectTo(split[0], port);
			success = true;
		} catch (Exception e)  {
			e.printStackTrace();
		}
		
		if (success) {
			startMenu.hide();
		}
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public void destroyInputObjects() {
		camController.destroy();
		if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.PC_TARGET) {
			InputManager.getInstance().removeScrollListener(zoomer);
			InputManager.getInstance().removeMoveListener(sl);
			SceneManager.getInstance().getRenderer().removeFrameListener(sl);
		}
	}

	public List<HouseController> getPlayers() {
		return world.players;
	}

	public void backConnect(int port, String address) {
		matchmakerNetwork = new Network();
		matchmakerNetwork.setLog(false);
		
		try {
			matchmakerNetwork.connectTo(address, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewConnection(Connection c) {
		c.openToBroadcasts = true;
	}

	@Override
	public void onConnectionLost(Connection c) {
	}

	@Override
	public void onReceivedData(final Connection c, final Object o) {
		if (o instanceof MapInfo) {
			final MapInfo info = (MapInfo) o;
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				public void run() {
					createWorld(info.name);
					world.pause();
					network.addListener(chat);
					network.addListener(board);
					network.addListener(world);
					network.send(new RequestWorld(), c);
				}
			});
		} 
	}

}
