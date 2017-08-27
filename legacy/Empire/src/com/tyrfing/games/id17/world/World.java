package com.tyrfing.games.id17.world;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.actions.Executor;
import com.tyrfing.games.id17.diplomacy.MessageExecutor;
import com.tyrfing.games.id17.geometry.Grass;
import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.gui.PlayerController;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.houses.XMLHouseFactory;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.IntrigueStarter;
import com.tyrfing.games.id17.networking.DisplayedArmyStats;
import com.tyrfing.games.id17.networking.DisplayedHoldingStats;
import com.tyrfing.games.id17.networking.DisplayedHouseStats;
import com.tyrfing.games.id17.networking.DisplayedLawStats;
import com.tyrfing.games.id17.networking.DisplayedTechStats;
import com.tyrfing.games.id17.networking.HouseState;
import com.tyrfing.games.id17.networking.LevyState;
import com.tyrfing.games.id17.networking.MapInfo;
import com.tyrfing.games.id17.networking.NetworkController;
import com.tyrfing.games.id17.networking.NetworkMessage;
import com.tyrfing.games.id17.networking.PlayerQuit;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrfing.games.id17.networking.WorldState;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyTreeSet;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;
import com.tyrfing.games.id17.trade.Meat;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.War;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.particles.Affector;
import com.tyrlib2.graphics.particles.BasicParticleFactory;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.Particle;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Overlay;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.networking.Network;
import com.tyrlib2.util.Color;

public class World implements IUpdateable, Serializable, INetworkListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8926993271712524150L;

	private class PointRanker implements Comparator<House> {

		@Override
		public int compare(House lhs, House rhs) {
			if ((short)lhs.points  < (short)rhs.points) return 1;
			if ((short)lhs.points  > (short)rhs.points) return -1;
			return 0;
		}
	}
	
	public static final float START_TIME = 310000;
	public static final float SECONDS_PER_DAY = 1.5f;
	public static final int DAYS_PER_SEASON = 120;
	public static final int SEASONS_PER_YEAR = 4;
	public static final int DAYS_PER_YEAR = DAYS_PER_SEASON * SEASONS_PER_YEAR;
	public static final float MORAL_PER_SEASON = 0.05f;
	
	public static final float PROD_CHECK_INTERVAL = 6;
	
	private transient HoldingTypes holdingTypes;
	public float worldTime= START_TIME;
	private transient Updater gameUpdater;
	
	private transient Octree octree;
	
	private List<Barony> baronies = new ArrayList<Barony>();
	private List<Holding> holdings = new ArrayList<Holding>();
	private Map<String, Holding> holdingsByName = new HashMap<String, Holding>();
	
	private transient List<House> houses = new ArrayList<House>();
	private transient List<House> rankedHouses;
	private transient HashMap<Entity, Holding> holdingMap = new HashMap<Entity, Holding>();
	private transient HashMap<Entity, Army> armyMap = new HashMap<Entity, Army>();
	private List<Army> raisedArmies = new ArrayList<Army>();
	public List<Army> armies = new ArrayList<Army>();
	private boolean detailsVisible = true;
	
	private WorldMap map;
	
	private float supplyFactor = 1;
	
	public transient MainGUI mainGUI;
	
	private static World singleton_world;
	
	public int nextPlayerID;
	
	private int speed = 2;
	
	private PlayerController playerController;
	
	private int day;
	private int days;
	private int season;
	private int seasons;
	private int years;
	
	private transient int daysLastFrame;
	
	private float prodCheckTime;
	
	private transient List<ComplexParticleSystem> waterfalls = new ArrayList<ComplexParticleSystem>();
	private transient List<ComplexParticleSystem> waterfallSmokes = new ArrayList<ComplexParticleSystem>();
	
	public float passedUpdateRankTime;
	public final static float UPDATE_RANK_TIME = 2;
	
	public TechnologyTreeSet techTreeSet;
	public float goalPoints = 20000;
	
	public MessageExecutor messageResponses = new MessageExecutor();
	public Executor executor = new Executor();
	public IntrigueStarter starter = new IntrigueStarter();
	
	public List<HouseController> players = new ArrayList<HouseController>();
	
	private int updateHouses;
	
	public static final float SERVER_UPDATE = 0.5f;
	public float passedUpdateTime;
	
	private Color ambientColor = new Color(1,1,1,1);
	
	private int oldSeasons = -1;
	
	public static int CHUNKS = 0;
	
	private MapFile mapFile;
	
	public static final String[] SEASON_TOOLTIPS = {
		"Spring\n" +
		"---------------\n" +
		"- Grain<img GOODS Grain> " + Util.getFlaggedText("-50%", false),
		"Summer",
		"Autumn\n" + 
		"---------------\n" +
		"- Grain<img GOODS Grain> " + Util.getFlaggedText("+50%", true),
		"Winter\n" +
		"---------------\n" +
		"- Supplies " + Util.getFlaggedText("-50%", false) + "\n" +
		"- Movement " + Util.getFlaggedText("-75%", false) + "\n" +
		"- Grain<img GOODS Grain> " + Util.getFlaggedText("-100%", false) + "\n" + 
		"- Meat<img GOODS Meat> " + Util.getFlaggedText("-50%", false)
	};
	
	public static final float[][] SEASONAL_GOOD_MULT = new float[4][Good.COUNT_GOODS];
	
	static {
		SEASONAL_GOOD_MULT[0][Grain.ID] = -0.5f;
		SEASONAL_GOOD_MULT[2][Grain.ID] = 0.5f;
		SEASONAL_GOOD_MULT[3][Grain.ID] = -1f;
		SEASONAL_GOOD_MULT[3][Meat.ID] = -0.5f;
	}
	
	public World(MapFile mapFile) {
		this.mapFile = mapFile;
		
		int width = mapFile.tileMap.getWidth();
		int height = mapFile.tileMap.getHeight();
		
		map = new WorldMap(width, height);
		holdingTypes = new HoldingTypes();
		singleton_world = this;
		
		Technology.COUNT_TECHS = 0;
		this.techTreeSet = new TechnologyTreeSet();
		
		gameUpdater = new Updater();
		SceneManager.getInstance().addFrameListener(gameUpdater);
		gameUpdater.addItem(this);
		
		playerController = new PlayerController();
		
		octree = new Octree(5, 20, new Vector3(), 200);
		
		gameUpdater.addItem(messageResponses);
		gameUpdater.addItem(executor);
		gameUpdater.addItem(starter);
	}
	
	public Octree getOctree() {
		return octree;
	}
	
	public float getWorldTime() {
		return worldTime;
	}
	
	public float getSupplyFactor() {
		return supplyFactor;
	}
	
	public List<House> getHouses() {
		return houses;
	}
	
	public void addHouse(House house) {
		houses.add(house);
		map.insertHouse(house);
	}
	
	public void removeHouse(House house) {
		int index = houses.indexOf(house);
		if (index != -1) {
			houses.remove(index);
			for (int i = index; i < houses.size(); ++i) {
				houses.get(i).id--;
			}
		}
		
		XMLHouseFactory.ID--;
	}
	
	public void addHolding(Holding holding) {
		
		holdings.add(holding);
		holdingsByName.put(holding.getFullName(), holding);
		
		if (holding instanceof Barony) {
			((Barony)holding).setIndex(baronies.size());
			baronies.add((Barony)holding);
		}
		
		gameUpdater.addItem(holding);
		
		addHoldingEntity(holding);
	}
	
	public void addHoldingEntity(Holding holding) {
		holdingMap.put(holding.holdingData.worldEntity, holding);
	}
	
	public Holding getHolding(Entity entity) {
		if (holdingMap.containsKey(entity)) {
			return holdingMap.get(entity);
		}
		
		return null;
	}
	
	public Holding getHolding(int id) {
		if (id >= 0 && id < holdings.size()) {
			return holdings.get(id);
		} else {
			return null;
		}
	}
	
	public Barony getBarony(int index) {
		return baronies.get(index);
	}
	
	public int getCountBaronies() {
		return baronies.size();
	}
	
	public Holding getHoldingByFullName(String fullname) {
		return holdingsByName.get(fullname);
	}
	
	public int getSeason() {
		days = (int) (worldTime / SECONDS_PER_DAY);
		seasons = days / DAYS_PER_SEASON;
		season = seasons % SEASONS_PER_YEAR;
		return season;
	}
	
	public float getWinter() {
		days = (int) (worldTime / SECONDS_PER_DAY);
		seasons = days / DAYS_PER_SEASON;
		
		day = days % DAYS_PER_SEASON + 1;
		season = seasons % SEASONS_PER_YEAR;
		
		float remainder = (worldTime - days * SECONDS_PER_DAY) / SECONDS_PER_DAY;
		
		if (season == 2 && day > 100) {
			return (day+remainder-101) / 30.f;
		} else if  (season == 3 && day < 20) {
			return (day+remainder+19)/30.f;
		} else if (season == 3 && day >= 20 && day <= 100) {
			return 1.5f;
		} else if (season == 3 && day > 100) {
			return (121-day-remainder+19) / 30.f;
		} else if (season == 0 && day < 20) {
			return (20-day-remainder)/30.f;
		} else {
			return 0;
		}
	}
	
	public List<Holding> getHoldings() {
		return holdings;
	}

	public void reveal() {

		for (int i = 0; i < baronies.size(); ++i) {
			baronies.get(i).explore(World.getInstance().getPlayerController().getHouse(), false);
		}
		
	}
	
	@Override
	public void onUpdate(float time) {
		
		if (time >= 10) return;
		
		worldTime += time * getPlaySpeed();
		prodCheckTime += time * getPlaySpeed();
		
		if (seasons != oldSeasons) {
			for (int i = 0; i < baronies.size(); ++i) {
				if (!baronies.get(i).getLevy().isFighting() && baronies.get(i).getLevy().getOwner().getGold() >= 0) {
					baronies.get(i).getLevy().regenMoral(MORAL_PER_SEASON);
				}
				if (!baronies.get(i).getGarrison().isFighting() && baronies.get(i).getGarrison().getOwner().getGold() >= 0) {
					baronies.get(i).getGarrison().regenMoral(MORAL_PER_SEASON);
				}
			}
			
			if (season != 3) {
				supplyFactor = 1;
			} else {
				supplyFactor = 0.5f;
			}
			
			for (int i = 0; i < baronies.size(); ++i) {
				Barony barony = baronies.get(i);
				barony.holdingData.storedGrain = Math.min(barony.holdingData.storeGrain, barony.holdingData.storeGrainMax);
			}
		}
		
		oldSeasons = seasons;
		
		if (prodCheckTime >= World.PROD_CHECK_INTERVAL) {
			prodCheckTime = 0;
			checkProductions();
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
		
			int days = (int) (worldTime / SECONDS_PER_DAY);
			if (days != daysLastFrame) {
				Window window = WindowManager.getInstance().getWindow("DATE_WINDOW/DATE");
				if (window != null) {
					String date = getDate();
					((Label)window).setText(date);
					daysLastFrame = days;
					
					Label dateTooltip = (Label) WindowManager.getInstance().getWindow(window.getName() + "/TooltipText");
					dateTooltip.setText(SEASON_TOOLTIPS[season]);
				}
			}
			
			float winter = getWinter();
			
			
			ambientColor.r = 0.72f + winter/40;
			ambientColor.g = 0.72f + winter/40;
			ambientColor.b = 0.77f + winter/5;
			ambientColor.a = 0;
			SceneManager.getInstance().setAmbientLight(ambientColor);
		
		
			for (int i = 0; i < waterfalls.size(); ++i) {
				ComplexParticleSystem waterfall = waterfalls.get(i);
				ComplexParticleSystem waterfallSmoke = waterfallSmokes.get(i);
				
				BasicParticleFactory f = (BasicParticleFactory)waterfall.getEmitter(0).getFactory();
				f.getMaterial().setColor(new Color(0.3f+winter/3, 0.6f+winter/4, 0.7f+winter/6, 0.4f+winter/16));
				
				if (winter < 0.8f) {
					waterfallSmoke.getEmitter(0).setAmount(1);
				} else {
					waterfallSmoke.getEmitter(0).setAmount(0);
				}
				
				waterfall.onUpdate(time*(1-winter*0.99f));
				waterfallSmoke.onUpdate(time);
			}
			
			for (int i = 0; i < baronies.size(); ++i) {
				Barony barony = baronies.get(i);
				barony.getWorldChunk().setOwnerValue(barony.getInfluence(playerController.getHouse()));
			}
		}
		
		passedUpdateRankTime += time;
		if (passedUpdateRankTime >= UPDATE_RANK_TIME) {
			passedUpdateRankTime = 0;
			
			Collections.sort(rankedHouses, new PointRanker());
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode() && playerController.getHouse() != null) {
				for (int i = 0; i < rankedHouses.size(); ++i) {
					rankedHouses.get(i).rank = i+1;
				}
			}
			
			houses.get(updateHouses++ % houses.size()).updateFamily();
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			passedUpdateTime += time;
			if (passedUpdateTime > SERVER_UPDATE) {
				passedUpdateTime = 0;
				
				if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
					for (int i = 0; i < players.size()-1; ++i) {
						updatePlayer(i, 1);
					}
				} else {
					for (int i = 0; i < players.size(); ++i) {
						updatePlayer(i, 0);
					}
				}
				

			}
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (rankedHouses.get(0).points >= goalPoints) {
				//win();
				
				if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
					// showResultScreen();
				} else {
					// Media.CONTEXT.quit();
				}
			} 
		}
	}
	
	public void win() {
		pause();
		EmpireFrameListener.MAIN_FRAME.destroyInputObjects();
	}
	
	public void showResultScreen() {
		Overlay o = (Overlay) WindowManager.getInstance().createOverlay("RESULT_OVERLAY", Color.BLACK);
		o.setAlpha(0);
		o.fadeIn(0.8f, 1);
		o.setReceiveTouchEvents(true);
	}
	
	private void updatePlayer(int i, int offset) {
		NetworkController nc = (NetworkController) players.get(i+offset);
		EmpireFrameListener.MAIN_FRAME.getNetwork().send(getWorldState(i+offset), nc.getConnection());
		
		if (nc.displayRequest == RequestDisplayData.HOLDING_DATA) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().send(new DisplayedHoldingStats(holdings.get(nc.displayParam)), nc.getConnection());
		} else if (nc.displayRequest == RequestDisplayData.TECH_DATA) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().send(new DisplayedTechStats(houses.get(nc.displayParam)), nc.getConnection());
		} else if (nc.displayRequest == RequestDisplayData.HOUSE_DATA) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().send(new DisplayedHouseStats(houses.get(nc.displayParam), nc.getHouse()), nc.getConnection());
		} else if (nc.displayRequest == RequestDisplayData.LAW_STATS) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().send(new DisplayedLawStats(houses.get(nc.displayParam)), nc.getConnection());
		} else if (nc.displayRequest == RequestDisplayData.ARMY_STATS) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().send(new DisplayedArmyStats(armies.get(nc.displayParam)), nc.getConnection());
		}
	}
	
	public String getDate() {
		days = (int) (worldTime / SECONDS_PER_DAY);
		seasons = days / DAYS_PER_SEASON;
		years = days / DAYS_PER_YEAR;
		
		day = days % DAYS_PER_SEASON + 1;
		season = seasons % SEASONS_PER_YEAR;
		
		return (day < 10 ? "0" : "")  + day + "/" + getSeasonName(season) + "/" + years;
	}
	
	public static String toDate(float time) {
		int days = (int) (time / SECONDS_PER_DAY);
		int seasons = days / DAYS_PER_SEASON;
		int years = days / DAYS_PER_YEAR;
		
		int day = days % DAYS_PER_SEASON + 1;
		int season = seasons % SEASONS_PER_YEAR;
		
		return (day < 10 ? "0" : "")  + day + "/" + getSeasonName(season) + "/" + years;
	}
	
	public static String getSeasonName(int season) {
		switch (season) {
		case 0:
			return "SPR";
		case 1:
			return "SUM";
		case 2:
			return "AUT";
		default:
			return "WIN";
		}
	}
	
	public static String getSeasonNameFull(int season) {
		switch (season) {
		case 0:
			return "Spring";
		case 1:
			return "Summer";
		case 2:
			return "Autumn";
		default:
			return "Winter";
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public Updater getUpdater() {
		return gameUpdater;
	}
	
	public static World getInstance() {
		return singleton_world;
	}
	
	public WorldMap getMap() {
		return map;
	}
	
	public HoldingTypes getHoldingTypes() {
		return holdingTypes;
	}
	
	public boolean isPaused() {
		return gameUpdater.isPaused();
	}
	
	public void pause() {
		gameUpdater.pause();
	}
	
	public void unpause() {
		gameUpdater.unPause();
	}
	
	public void setPlaySpeed(int speed) {
		this.speed = speed;
	}
	
	public int getPlaySpeed() {
		return speed*speed;
	}
	
	public PlayerController getPlayerController() {
		return playerController;
	}
	
	public void addRaisedArmy(Army army) {
		raisedArmies.add(army);
		armyMap.put(army.getEntity(), army);
	}
	
	public void addArmyMapEntry(Army army, Entity entity) {
		armyMap.put(entity, army);
	}
	
	public int getCountRaisedArmies() {
		return raisedArmies.size();
	}
	
	public Army getRaisedArmy(int index) {
		return raisedArmies.get(index);
	}
	
	public Army getArmy(Entity entity) {
		if (armyMap.containsKey(entity)) {
			return armyMap.get(entity);
		}
		
		return null;
	}

	public MainGUI getMainGUI() {
		return mainGUI;
	}
	
	public void removeRaisedArmy(Army army) {
		raisedArmies.remove(army);
		armyMap.remove(army.getEntity());
	}
	
	public void checkProductions() {
		
		for (int i = 0; i < holdings.size(); ++i) {
			Holding holding = holdings.get(i);
			holding.resetGoods();
		}
		
		for (int i = 0; i < holdings.size(); ++i) {
			Holding holding = holdings.get(i);
			holding.spreadGoods();
		}
		
		for (int i = 0; i < holdings.size(); ++i) {
			Holding holding = holdings.get(i);
			for (int j = 0; j < holding.getCountProductions(); ++j) {
				GoodProduction prod = holding.getProduction(j);
				prod.checkProduction(holding);
			}
		}
		
		for (int i = 0; i < baronies.size(); ++i) {
			Barony barony = baronies.get(i);
			
			int storedGrain = barony.holdingData.storedGrain;;
			if (storedGrain > 0) {
				Good grain = Good.createGood("Grain", storedGrain, barony);
				barony.addGood(grain, 0);
			}
			
		}
		
		for (int i = 0; i < holdings.size(); ++i) {
			Holding holding = holdings.get(i);
			holding.spreadGoodsFinished();
		}
		

	}
	
	public void addWaterfall(ComplexParticleSystem waterfall, ComplexParticleSystem waterfallSmoke) {
		this.waterfalls.add(waterfall);
		this.waterfallSmokes.add(waterfallSmoke);
		
		waterfall.addAffector(new Affector() {

			@Override
			public void onUpdate(Particle particle, float time) {
				float winter = getWinter();
				
				if (winter < 1 && winter > 0) {
				
					particle.floatArray.buffer[particle.dataIndex + 3] += winter/8 * time/(1-winter+0.001f);
					particle.floatArray.buffer[particle.dataIndex + 4] += winter/12 * time/(1-winter+0.001f);
					particle.floatArray.buffer[particle.dataIndex + 5] += winter/20 * time/(1-winter+0.001f);
					particle.floatArray.buffer[particle.dataIndex + 6] += winter/64 * time/(1-winter+0.001f);
					
					particle.floatArray.buffer[particle.dataIndex + 3 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
					particle.floatArray.buffer[particle.dataIndex + 4 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
					particle.floatArray.buffer[particle.dataIndex + 5 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
					particle.floatArray.buffer[particle.dataIndex + 6 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
					
					particle.floatArray.buffer[particle.dataIndex + 3 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
					particle.floatArray.buffer[particle.dataIndex + 4 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
					particle.floatArray.buffer[particle.dataIndex + 5 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
					particle.floatArray.buffer[particle.dataIndex + 6 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
					
					particle.floatArray.buffer[particle.dataIndex + 3 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
					particle.floatArray.buffer[particle.dataIndex + 4 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
					particle.floatArray.buffer[particle.dataIndex + 5 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
					particle.floatArray.buffer[particle.dataIndex + 6 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
				}
			}

			@Override
			public Affector copy() {
				return null;
			}
		});
	}
	
	public void finishBuild() {
		map.finishBuild();
		rankedHouses = new ArrayList<House>();
		rankedHouses.addAll(houses);
	}
	
	public void saveAs(String fileName) {
		String target = "/res/sav";
		Media.CONTEXT.serializeTo(this, target, fileName + ".ser");	
	}
	
	public void destroy() {
		EmpireFrameListener.MAIN_FRAME.getNetwork().removeListener(this);
		SceneManager.getInstance().getRenderer().removeFrameListener(gameUpdater);
		SceneManager.getInstance().getRenderer().destroyRenderables(OpenGLRenderer.DEFAULT_CHANNEL);
	}
	
	public static World loadFrom(String fileName) {
		World world = (World) Media.CONTEXT.deserializeFrom("/res/sav", fileName + ".ser");
		world.ambientColor = new Color(1,1,1,1);
		world.mapFile.load();
		world.techTreeSet.update();
		world.octree = new Octree(5, 2000, new Vector3(), 200);
		world.gameUpdater = new Updater();
		world.holdingTypes = new HoldingTypes();
		SceneManager.getInstance().addFrameListener(world.gameUpdater);
		world.gameUpdater.addItem(world);
		world.waterfalls = new ArrayList<ComplexParticleSystem>();
		world.waterfallSmokes = new ArrayList<ComplexParticleSystem>();
		world.holdingMap = new HashMap<Entity, Holding>();
		world.armyMap = new HashMap<Entity, Army>();
		
		world.gameUpdater.addItem(world.messageResponses);
		world.gameUpdater.addItem(world.executor);
		world.gameUpdater.addItem(world.starter);
		return world;
	}

	@Override
	public void onNewConnection(final Connection c) {
		System.out.println("New player connection added");
		final Network network = EmpireFrameListener.MAIN_FRAME.getNetwork();
		if (network.isHost()) {
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					MapInfo info = new MapInfo(mapFile.mapName);
					network.send(info, c);
				}
			});
		} else {
			c.openToBroadcasts = true;
		}
		
	}

	@Override
	public void onConnectionLost(final Connection c) {
		SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
			@Override
			public void run() {
				HouseController hc = getPlayer(c);
				
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					if (hc.hasJoined) {
						hc.getHouse().setHouseController(new AIController(new BehaviorModel()));
						AIThread.getInstance().addAI((AIController)hc.getHouse().getController());
						hc.hasJoined = false;
						
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new PlayerQuit(hc.playerID));
					}
					players.remove(hc);
				}
			}
		}); 
	}

	@Override
	public void onReceivedData(final Connection c, Object o) {
		if (o instanceof NetworkMessage) {
			final NetworkMessage nm = (NetworkMessage) o;
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					nm.process(c);
				}
			});
		} 
	}
	
	public WorldState getWorldState(int playerID) {
		WorldState state = new WorldState();
		
		HouseController hc = players.get(playerID);
		
		state.timeStamp = worldTime;
		
		state.moral = new float[World.getInstance().armies.size()];
		state.troops = new float[World.getInstance().armies.size()][Army.MAX_REGIMENTS];
		
		for (int i = 0; i < armies.size(); ++i) {
			Army army = armies.get(i);
			state.moral[i] = army.getMoral();
			for (int j = 0; j < Army.MAX_REGIMENTS; ++j) {
				Regiment r = army.getRegiment(j);
				if (r != null) state.troops[i][j] = r.troops;
			}
		}
		
		if (hc.getHouse() != null) {
			state.gold = hc.getHouse().getGold();
			state.honor = hc.getHouse().getHonor();
			state.males = hc.getHouse().getMales();
			state.females = hc.getHouse().getFemales();
			state.visibleProjectCount = 0;
			state.taxIncome = hc.getHouse().taxIncome;
			state.tradeIncome = hc.getHouse().tradeIncome;
			state.armyMaint = hc.getHouse().armyMaint;
			state.holdingMaint = hc.getHouse().holdingMaint;
			state.countMarriages = hc.getHouse().countMarriages;
			state.techProgress = hc.getHouse().techProject != null ? hc.getHouse().techProject.getProgress() : 0;
			
			for (int i = 0; i < holdings.size(); ++i) {
				if (holdings.get(i).isProjectVisibleFor(hc.getHouse())) {
					state.visibleProjectCount++;
				}
			}
			
			state.projectHolding = new short[state.visibleProjectCount];
			state.projectProgress = new float[state.visibleProjectCount];
			
			short index = 0;
			
			for (int i = 0; i < holdings.size(); ++i) {
				if (holdings.get(i).isProjectVisibleFor(hc.getHouse())) {
					state.projectHolding[index] = (short) i;
					state.projectProgress[index] = holdings.get(i).getProject().getProgress();
					index++;
				}
			}
		} 
		
		state.points = new short[houses.size()];
		state.income = new float[houses.size()];
		for (int i = 0; i < houses.size(); ++i) {
			state.points[i] = (short) World.getInstance().getHouses().get(i).points;
			state.income[i] = World.getInstance().getHouses().get(i).income;
		}
		
		return state;
	}
	
	public LevyState getLevyState() {
		LevyState state = new LevyState(armies.size());
		for (int i = 0; i < armies.size(); ++i) {
			Army army = armies.get(i);
			state.raised[i] = army.isRaised();
			if (state.raised[i]) {
				state.holdingIDs[i] = army.getCurrentHolding().getHoldingID();
			}
			state.movingTo[i] = (short) army.getMoveTarget();
		}
		
		return state;
	}
	
	public HouseState getHouseState(int id) {
		House house = houses.get(id);
		HouseState state = new HouseState();

		state.houseID = (short) id;
		if (house.getOverlord() != null) {
			state.overlordID = (short) house.getOverlord().id;
		} else {
			state.overlordID = -1;
		}
		state.researched = house.researched;
		state.discovered = house.discovered;
		
		if (house.intrigueProject != null) {
			state.intrigueID = (byte) Intrigue.actions.indexOf(house.intrigueProject.action);
			state.intrigueOptions = house.intrigueProject.options;
			state.intrigueTarget = (short) house.intrigueProject.receiver.id;
		} else {
			state.intrigueID = -1;
		}
		
		state.justifications = house.getJustifications();
		
		return state;
	}
	
	public Army getArmy(int id) {
		if (id < armies.size()) {
			return armies.get(id);
		} else {
			return null;
		}
	}
	
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
    	stream.writeFloat(worldTime);
    	stream.writeObject(mapFile);
    	
    	stream.writeInt(holdings.size());
    	for (int i = 0; i < holdings.size(); ++i) {
    		stream.writeObject(holdings.get(i));
    	}
    	for (int i = 0; i < holdings.size(); ++i) {
    		holdings.get(i).writeInternalsToStream(stream);
    	}
    	
    	stream.writeInt(houses.size());
    	for (int i = 0; i < houses.size(); ++i) {
    		stream.writeObject(houses.get(i));
    	}
    	
    	for (int i = 0; i < houses.size(); ++i) {
    		houses.get(i).writeInternalsToStream(stream);
    	}
    	
    	for (int i = 0; i < houses.size(); ++i) {
        	stream.writeInt(houses.get(i).wars.size());
        	for (int j = 0; j < houses.get(i).wars.size(); ++j) {
        		stream.writeObject(houses.get(i).wars.get(j));
        	}
    		stream.writeObject(houses.get(i).getOverlord());
    		stream.writeObject(houses.get(i).getController());
    	}
    	
    	stream.writeObject(armies);
    	stream.writeObject(raisedArmies);
    	stream.writeObject(map);
    	stream.writeFloat(supplyFactor);
    	stream.writeByte(speed);
    	stream.writeObject(playerController);
    	stream.writeObject(techTreeSet);
    	stream.writeObject(messageResponses);
    	stream.writeObject(executor);
    	stream.writeObject(starter);
    	stream.writeObject(players);
    	stream.writeFloat(goalPoints);
    	stream.writeBoolean(detailsVisible);
    }

    @SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
    	World.singleton_world = this;
    	worldTime = stream.readFloat();
    	mapFile = (MapFile) stream.readObject();
    	
    	baronies = new ArrayList<Barony>();
    	
    	int countHoldings = stream.readInt();
    	holdings = new ArrayList<Holding>();
    	for (int i = 0; i < countHoldings; ++i) {
    		Holding h = (Holding) stream.readObject();
    		holdings.add(h);
    		
    		if (h instanceof Barony) {
    			baronies.add((Barony)h);
    		}
    	}
    	
    	for (int i = 0; i < countHoldings; ++i) {
    		holdings.get(i).readInternalsFromStream(stream);
    	}

    	int countHouses = stream.readInt();
    	houses = new ArrayList<House>();
    	for (int i = 0; i < countHouses; ++i) {
    		houses.add((House) stream.readObject());
    	}
    	
    	for (int i = 0; i < countHouses; ++i) {
    		houses.get(i).readInternalsFromStream(stream);
    	}
    	
    	for (int i = 0; i < countHouses; ++i) {
        	int countWars = stream.readInt();
        	houses.get(i).wars = new ArrayList<War>();
        	for (int j = 0; j < countWars; ++j) {
        		houses.get(i).wars.add((War) stream.readObject());
        	}
    		houses.get(i).setOverlord((House) stream.readObject());
    		houses.get(i).controller = (HouseController) stream.readObject();
    	}
    	
    	armies = (List<Army>) stream.readObject();
    	raisedArmies = (List<Army>) stream.readObject();
    	map = (WorldMap) stream.readObject();
    	supplyFactor = (Float) stream.readFloat();
    	speed = stream.readByte();
    	playerController = (PlayerController) stream.readObject();
    	techTreeSet = (TechnologyTreeSet) stream.readObject();
    	messageResponses = (MessageExecutor) stream.readObject();
    	executor = (Executor) stream.readObject();
    	starter = (IntrigueStarter) stream.readObject();
    	players = (List<HouseController>) stream.readObject();
    	goalPoints = stream.readFloat();
    	detailsVisible = stream.readBoolean();
    }
    
    public HouseController getPlayer(int id) {
    	for (int i = 0; i < players.size(); ++i) {
    		if (players.get(i).playerID == id) {
    			return players.get(i);
    		}
    	}
    	
    	return null;
    }
    
    public NetworkController getPlayer(Connection c) {
    	for (int i = 0; i < players.size(); ++i) {
    		if (players.get(i) instanceof NetworkController) {
	    		if (((NetworkController)players.get(i)).getConnection() == c) {
	    			return (NetworkController)players.get(i);
	    		}
    		}
    	}
    	
    	return null;
    }

	public List<House> getRankedHouses() {
		return rankedHouses;
	}

	public int getDay() {
		return day;
	}
	
	public int getHighestPotentialHouse() {
		
		float bestInc = 0;
		int index = 0;
		for (int i = 0; i < houses.size(); ++i) {
			houses.get(i).updatePointInc();
			float inc = houses.get(i).getTotalPointInc();
			if (houses.get(i).getHoldings().size() > 0 && inc > bestInc) {
				bestInc = inc;
				index = i;
			}
		}
		return index;
	}

	public void setDetailVisibility(boolean b) {
		
		for (int i = 0; i < baronies.size(); ++i) {
			if (baronies.get(i).isExplored()) {
				baronies.get(i).setWaterfallSystemsVisible(b);
			}
		}
		
		for (int i = 0; i < baronies.size(); ++i) {
			WorldChunk c = baronies.get(i).getWorldChunk();
			for (int j = 0; j < c.getCountObjects(); ++j) {
				c.getObject(j).setVisible(b);
			}
			
			for (int k = 0; k < 2; ++k){
				for (int j = 0; j < c.getCountGrasses(k); ++j) {
					Grass grass = c.getGrass(k, j);
					if (grass != null) {
						grass.grass.setVisible(b);
					}
				}
			}
		}
		
		for (int i = 0; i < armies.size(); ++i) {
			Army army = armies.get(i);
			Entity entity = army.getEntity();
			if (entity != null && army.getCurrentHolding() != null && army.getCurrentHolding().holdingData.barony.isExplored()) {
				entity.setVisible(b);
			}
		}
		
		detailsVisible = b;
		
	}

	public void setTerrainStrategic(boolean b) {
		for (int i = 0; i < baronies.size(); ++i) {
			Barony barony = baronies.get(i);
			if (barony.isExplored()) {
				for (int j = 0; j < barony.getWorldChunk().blockTypes.length; ++j) {
					barony.getWorldChunk().blockTypes[j].mat.setStrategic(b, barony.getOwner().getController().getStrategicColor());
				}
			}
		}
	}

	public float getGoodMult(int id) {
		return SEASONAL_GOOD_MULT[season][id] + 1;
	}

	public float getTravelFactor() {
		return season == 3 ? 0.25f : 1;
	}

	public boolean areDetailsVisible() {
		return detailsVisible;
	}

	public int getRandomHouse() {
		
		List<House> availableHouses = new ArrayList<House>();
		for (int i = 0; i < houses.size(); ++i) {
			if (houses.get(i).isIndependend() && houses.get(i).getController() instanceof AIController) {
				availableHouses.add(houses.get(i));
			}
		}
		
		House random = availableHouses.get((int)(Math.random()*availableHouses.size()));
		
		return random.id;
	}

	public MapFile getMapFile() {
		return mapFile;
	}
}
