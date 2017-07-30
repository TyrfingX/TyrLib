package com.tyrfing.games.id17.holdings;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.GameUpdater;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.HandleRevolt;
import com.tyrfing.games.id17.gui.CameraController;
import com.tyrfing.games.id17.holdings.projects.IProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.TravellerMessage;
import com.tyrfing.games.id17.networking.UnrestSourceChange;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldMap;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.Particle;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Holding implements IUpdateable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7541736836226998936L;
	
	public static final float PILLAGED_DURATION = World.DAYS_PER_YEAR * World.SECONDS_PER_DAY * 2;
	public static final float PILLAGE_DEATH = 0.6f;
	public static final float PILLAGE_MONEY = 0.75f;
	
	public static final float TRAVELLER_TRESHOLD = 5f;
	
	public HoldingData holdingData;
	protected transient House owner;
	private float passedTime = (float) Math.random();
	private float incomeTime = (float) Math.random();
	
	private Vector3 armyBasePos;
	private Vector3 armyOffset;
	
	public static final Vector3 OFFSET = new Vector3(-1.5f,-1.7f, 0.0f);
	public static final Vector3 OFFSET_PROGRESS = new Vector3(-0.6f,-1f, -0.2f);
	public static final float WANDER_OFF = World.SECONDS_PER_DAY * 10;
	public static final float WANDER_FACTOR = 0.00025f;
	public static final Vector2 OWNERSHIP_SIGIL_SIZE = new Vector2(50f, 50);
	public static final float REVOLT_DELAY = 4 * World.SECONDS_PER_DAY;
	private float passedRevoltDelay = (float) Math.random();
	
	protected List<GoodProduction> productions = new ArrayList<GoodProduction>();
	protected List<Building> buildings = new ArrayList<Building>();
	protected float[] localStats;
	protected float[] remainingAttractivity = new float[PopulationType.VALUES.length];
	
	protected IProject project;
	protected transient ParticleSystem projectSystem;
	protected transient ParticleSystem ownerSystem;
	protected transient TextureRegion projectProgressRegion;
	
	protected transient ParticleSystem smokeTrail;
	
	private List<Army> positionedArmies = new ArrayList<Army>();
	
	private float hungerTime = (float) Math.random();
	
	protected boolean pillaged;
	public float pillageTimestamp;
	
	public static final float HUNGER_POVERTY_INC = 0.001f;
	public static final float PILLAGED_POVERTY = 0.2f;
	public static final float MAX_POVERTY = 0.0005f;
	
	public static final float TECH_SPREAD_HOLDING = 0.004f;
	public static final float TECH_SPREAD_BARONY = 0.1f;
	public static final float TECH_SPREAD_OUTSIDE_DYNASTY = 0.0f;
	
	private List<UnrestSource> unrestSources = new ArrayList<UnrestSource>();
	protected List<Barony> tradeNeighbours = new ArrayList<Barony>();
	
	protected List<Good> suppliedGoods = new ArrayList<Good>();
	protected List<Good> suppliedGoodsTmp = new ArrayList<Good>();

	private String fullName;

	public float growth;

	public float hunger;
	public float totalWander;
	private float trackWander;
	
	private static Map<Mesh, Vector3> HUD_OFFSET = new HashMap<Mesh, Vector3>();
	public TIntFloatHashMap demandMap = new TIntFloatHashMap();

	private transient ParticleMaterial ownerMaterial;
	
	private TObjectFloatMap<Holding> wanderMap;
	private List<Holding> roads = new ArrayList<Holding>();

	public Holding(HoldingData holdingData, float[] stats) {
		this.holdingData = holdingData;
		localStats = new float[HoldingTypes.COUNT_STATS];
		System.arraycopy(stats, 0, localStats, 0, HoldingTypes.COUNT_STATS);
	}
	
	public void initWanderTable() {
		wanderMap = new TObjectFloatHashMap<Holding>();
	}
	
	public House getOwner() {
		return owner;
	}
	
	public void controleBy(House house) {
		
		if (owner != null) {
			hideOwner();
		}
		
		this.owner = house;
		
		displayOwner();
	}
	
	public String getName() {
		return holdingData.name;
	}
	
	public String getLinkedName() {
		return "<link=" + getFullName() + ">" + holdingData.name + "\\l";
	}
	
	public int getCountInhabitants() {
		return (int) holdingData.inhabitants;
	}
	
	public String getFullName() {
		if (fullName != null) {
			return fullName;
		} else {
			fullName = holdingData.barony.getName() + "/" + holdingData.name;
			return fullName;
		}
		
	}
	
	@Override
	public void onUpdate(float time) {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			
			float worldTime = time*World.getInstance().getPlaySpeed();
			
			incomeTime += worldTime;
			if (incomeTime >= World.SECONDS_PER_DAY*4) {

				holdingData.prodBuildings = holdingData.prodBuildings;
				
				float goodProd = 0;
				for (int i = 0; i < Good.COUNT_GOODS; ++i) {
					if (holdingData.prodPerGood[i] != 0) {
						Good g = getGood(i);
						if (g != null) {
							goodProd += holdingData.prodPerGood[i] * g.getQuantity();
						}
					}
				}
				
				holdingData.prodPop = 0;
				
				holdingData.researchBuildings = holdingData.researchBonus * holdingData.researchMult * owner.stats[House.RESEARCH_MULT];
				holdingData.researchPop = 0;

				holdingData.taxes = holdingData.incomeBonus;
				PopulationType[] types = PopulationType.VALUES;
				for (int i = 0; i < types.length; ++i) {
					float[] stats = PopulationType.POP_STATS[i];
					float pop = holdingData.population[i];
					holdingData.taxes += pop * (stats[PopulationType.BASE_TAX]+getStats()[HoldingTypes.TAX_BONUS+i]);
					holdingData.prodPop += pop * stats[PopulationType.PRODUCTIVITY];
					holdingData.researchPop += pop * stats[PopulationType.RESEARCH];
				}
				
				float taxFactor = owner.getTaxFactor();
				holdingData.taxes *= holdingData.incomeMult;
				holdingData.taxes *= owner.stats[House.INCOME_MULT];
				holdingData.taxes *= owner.stats[House.POP_TAXES];
				holdingData.trade = holdingData.tradeIncome * owner.stats[House.TRADE_TAXES];
				holdingData.maint = (1-taxFactor) * (holdingData.taxes + holdingData.trade);
				holdingData.income = holdingData.taxes + holdingData.trade;
				
				holdingData.researchPop *= holdingData.researchMult * owner.stats[House.RESEARCH_MULT];
				holdingData.research = holdingData.researchPop + holdingData.researchBuildings;
				
				holdingData.prod = holdingData.prodBuildings+holdingData.prodPop;
				holdingData.prodTrade = holdingData.prod * (holdingData.prodMult-1) + goodProd;
				holdingData.prod +=	holdingData.prodTrade;
				
				owner.addTaxedGold(4*taxFactor*holdingData.income*World.SECONDS_PER_DAY/World.DAYS_PER_SEASON);
				
				holdingData.buildingMaint = 0;
				for (int i = 0; i < buildings.size(); ++i) {
					holdingData.buildingMaint += Building.getMaintenance(this, buildings.get(i));
				}
				
				incomeTime -= World.SECONDS_PER_DAY*4;
				
				float totalInhabitants = 0;
				for (int i = 0; i < holdingData.barony.getCountSubHoldings(); ++i) {
					totalInhabitants += holdingData.barony.getSubHolding(i).holdingData.inhabitants;
				}
				float freeSupplies = holdingData.barony.holdingData.supplies - totalInhabitants;
				
				if (freeSupplies < 0) {
					hungerTime += World.SECONDS_PER_DAY*4;
				} else {
					hungerTime = 0;
				}
				if (freeSupplies > 0) {
					growth = holdingData.growth*(freeSupplies/1000000); 
					hunger = 0;
				} else {
					growth = 0;
					hunger = 0.1f*freeSupplies*hungerTime/1000000;
				}
				
				float educated = Math.min(holdingData.population[PopulationType.Peasants.ordinal()], 4*holdingData.education/World.DAYS_PER_SEASON);
				holdingData.changePop(PopulationType.Peasants.ordinal(), -educated);
				educated /= PopulationType.VALUES.length - 1;
				
				for (int i = 0; i < holdingData.population.length; ++i) {
					if (i != PopulationType.Peasants.ordinal()) {
						holdingData.changePop(i, educated);
					}
				}
				
				holdingData.inhabitants = 0;
				for (int i = 0; i < holdingData.population.length; ++i) {
					float pop = holdingData.population[i];
					holdingData.changePop(i, growth*pop);
					holdingData.changePop(i, hunger*pop);
					holdingData.inhabitants += holdingData.population[i];
				}
				
			}
			
			passedTime += worldTime;
			
			if (passedTime >= WANDER_OFF) {
				totalWander = trackWander;
				trackWander = 0;
				updateAttractivities();
				wanderOff(holdingData.barony);
				Barony[] baronies = World.getInstance().getMap().getNeighbours(holdingData.barony);
				for (int i = 0; i < baronies.length; ++i) {
					wanderOff(baronies[i]);
				}
				passedTime = 0;
			}
			
			passedRevoltDelay += worldTime;
			if (passedRevoltDelay >= REVOLT_DELAY) {
				passedRevoltDelay = 0;
				checkRevolts(REVOLT_DELAY);
				checkTechSpread();
			}
			
			owner.addIncome(holdingData.income * owner.getTaxFactor());
			owner.addResearch(holdingData.research);
		} 
		
		if (project != null) {
			project.onUpdate(time*World.getInstance().getPlaySpeed());
			if (projectSystem != null) {
				float progress = project.getProgress();
				projectProgressRegion.u2 = 1 - progress/2f;
				projectProgressRegion.u1 = 0.5f - progress/2f;
			}
		}
		
		if (pillaged) {
			if (pillageTimestamp + PILLAGED_DURATION < World.getInstance().getWorldTime()) {
				setPillaged(false);
			}
		}
		
	}
	
	public int getGrowth() {
		double exp = Math.exp(growth);
		return (int) (holdingData.inhabitants * Math.pow(exp, World.DAYS_PER_SEASON/4) - holdingData.inhabitants);
	}
	
	public int getHunger() {
		double exp = Math.exp(hunger);
		return (int) (holdingData.inhabitants * Math.pow(exp, World.DAYS_PER_SEASON/4) - holdingData.inhabitants);
	}
	
	public boolean isPillaged() {
		return pillaged;
	}
	
	private void updateAttractivities() {
		
		PopulationType[] types = PopulationType.VALUES;
		int countSubHoldings = holdingData.barony.getCountSubHoldings();
		
		float totalInhabitants = 0;
		for (int i = 0; i < countSubHoldings; ++i) {
			totalInhabitants += holdingData.barony.getSubHolding(i).holdingData.inhabitants;
		}
		
		float freeSupplies = holdingData.barony.holdingData.supplies - totalInhabitants;
		
		float supplyAttractivity = freeSupplies / 1000;
		float factor = (1-owner.stats[House.POP_TAXES]);
		if (pillaged) {
			factor *= 0;
		}
		
		float tradeFactor = (1-owner.stats[House.TRADE_TAXES]);

		float goodsAttractivity = getSupplyAttractivity();
		
		for (int j = 0; j < types.length; ++j) {
			float baseAttractivity = (localStats[HoldingTypes.ATTRACTIVITY + j] + supplyAttractivity) * (1+goodsAttractivity) * factor;
			if(types[j] == PopulationType.Traders) {
				baseAttractivity *= tradeFactor;
			}
			remainingAttractivity[j] = (baseAttractivity - holdingData.population[j] * (pillaged ? 50 : 1))*WANDER_FACTOR / (holdingData.population[j]+1);
		}
	}
	
	private void wanderOff(Barony barony) {
		
		int countSubHoldingsBarony = barony.getCountSubHoldings();
		PopulationType[] types = PopulationType.VALUES;

		for (int i = 0; i < countSubHoldingsBarony; ++i) {
			Holding holding = barony.getSubHolding(i);
			if (holding != this && World.getInstance().getMap().getRoadMap().getRealizedPath(this.getHoldingID(), holding.getHoldingID()).size() > 0) {
				for (int j = 0; j < types.length; ++j) {
					float attraction = holding.remainingAttractivity[j]-remainingAttractivity[j];
					float pop = holdingData.population[j];
					float wander = attraction * pop;
					
					if (wander > 1000) {
						wander = 1000;
					} else if (wander < -1000) {
						wander = -1000;
					}
					
					if (wander > 0 && wander > holdingData.population[j]*0.1f) {
						wander = holdingData.population[j]*0.1f;
					} else if (wander < 0 && -wander > holding.holdingData.population[j]*0.1f) {
						wander = -holding.holdingData.population[j]*0.1f;
					} 
					
					holding.holdingData.changePop(j, wander);
					holdingData.changePop(j, -wander);
					
					holding.trackWander += wander;
					holding.holdingData.inhabitants += wander; 
					holdingData.inhabitants -= wander;
					trackWander -= wander;
					
					if (wander > 0) {
						addWander(wander, holding);
					} else {
						holding.addWander(-wander, this);
					}
				}
			}

		}
		
	}
	
	private void addWander(float wander, Holding toHolding) {
		
		if (wanderMap == null) {
			initWanderTable();
		}
		
		float accWander = wanderMap.get(toHolding) + wander;
		
		while (accWander > TRAVELLER_TRESHOLD) {
			int srcHolding = this.getHoldingID();
			int dstHolding = toHolding.getHoldingID();
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				Traveller traveller = new Traveller(srcHolding, dstHolding);
			}  else {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TravellerMessage(srcHolding, dstHolding));
			}
			accWander -= TRAVELLER_TRESHOLD;
		}
		
		wanderMap.put(toHolding, accWander);
	}
	
	public int getWander() {
		return (int) (totalWander * World.SECONDS_PER_DAY * World.DAYS_PER_SEASON / WANDER_OFF);
	}
	
	public HoldingData getHoldingData() {
		return holdingData;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public int getCountProductions() {
		return productions.size();
	}
	
	public GoodProduction getProduction(int index) {
		return productions.get(index);
	}
	
	public void startProject(IProject project) {
		this.project = project;
		
		if (	!SceneManager.getInstance().getRenderer().isInServerMode() 
			&& 	isProjectVisibleFor(World.getInstance().getPlayerController().getHouse())) {
			displayProject();
		}
	}
	
	public boolean isProjectVisibleFor(House house) {
		return		project != null && 
				( 	this.getOwner() == house
				|| 	house.hasSpy(this.getOwner())
				||  this.getOwner().haveSameOverlordWith(house));
	}
	
	private void createProjectIcon() {
		Particle particle = new Particle(0.7f);
		particle.setLifeTime(0);
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(project.getIconAtlasName());
		ParticleMaterial mat = new ParticleMaterial(project.getIconAtlasName(), atlas.getRegion(project.getIconRegionName()), new Color(1,1,1,1));
		mat.setBlending(true);
		particle.setMaterial(mat);
		particle.setVelocity(Vector3.ORIGIN);
		
		projectSystem.addParticle(particle);
		((ComplexParticleSystem) projectSystem).checkBoundingBox(particle);
		
		Vector3 offset = HUD_OFFSET.get(holdingData.worldEntity.getSubEntity(0).getMesh());
		Vector3 basePos = holdingData.worldEntity.getParent().getAbsolutePos();
		basePos = basePos.add(offset).add(OFFSET);
		
		particle.setPos(basePos);
	}
	
	private void createProjectBar() {
		Particle particle = new Particle(1.8f);
		particle.setLifeTime(0);
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("PROGRESS");
		projectProgressRegion = new TextureRegion(atlas.getRegion("BROWN"));
		ParticleMaterial mat = new ParticleMaterial("PROGRESS", projectProgressRegion, new Color(1,1,1,1));
		mat.setBlending(true);
		particle.setMaterial(mat);
		particle.setVelocity(Vector3.ORIGIN);
		
		projectSystem.addParticle(particle);
		((ComplexParticleSystem) projectSystem).checkBoundingBox(particle);
		
		Vector3 offset = HUD_OFFSET.get(holdingData.worldEntity.getSubEntity(0).getMesh());
		Vector3 basePos = holdingData.worldEntity.getParent().getAbsolutePos();
		basePos = basePos.add(offset).add(OFFSET_PROGRESS);
		
		particle.setPos(basePos);
	}
	
	public boolean hasActiveProject() {
		return project != null;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void upgradeBuilding(int id) {
		buildings.get(id).changeLevel(1);
	}
	
	public Building getBuilding(int id) {
		return buildings.get(id);
	}
	
	public Building isBuilt(Building.TYPE type) {
		for (int i = 0; i < buildings.size(); ++i) {
			if (buildings.get(i).getType() == type) {
				return buildings.get(i);
			}
		}
		
		return null;
	}
	
	public int getBuildingLevel(Building.TYPE type) {
	
		for (int i = 0; i < buildings.size(); ++i) {
			if (buildings.get(i).getType() == type) {
				return buildings.get(i).getLevel();
			}
		}
		
		return 0;
		
	}
	
	public void addBuilding(Building building) {
		buildings.add(building);
	}
	
	public float[] getStats() {
		return localStats;
	}
	
	public Army getMainPositionedArmy() {
		return positionedArmies.size() > 0 ? positionedArmies.get(0) : null;
	}
	
	public void addPositionedArmy(Army army) {
		if (!positionedArmies.contains(army)) {
			positionedArmies.add(army);
		} 
	}
	
	public void removePositionedArmy(Army army) {
		positionedArmies.remove(army);
	}
	
	public int getCountPositionedArmies() {
		return positionedArmies.size();
	}
	
	public Vector3 getArmyBasePos() {
		return armyBasePos;
	}
	
	public Vector3 getArmyOffset() {
		return armyOffset;
	}
	
	public void initOffsets() {
		
		Entity e = holdingData.worldEntity;
		Mesh m = e.getSubEntity(0).getMesh();
	
		if (!HUD_OFFSET.containsValue(m)) {
			Vector3 offset = e.getBoundingBox().max.sub(e.getBoundingBox().min);
			offset.z *= 1.2f;
			offset.x *= 0.55f;
			offset.y *= 0.2f;
			HUD_OFFSET.put(m, offset);
		}
		
		armyBasePos = e.getParent().getAbsolutePos();
		armyOffset = e.getBoundingBox().max.sub(e.getBoundingBox().min);
		armyOffset.x = 0;
		armyOffset.y = 0;
		armyOffset.z *= 1.7f;
	}
	
	public boolean isPillageableByArmy(Army army) {
		return 		!pillaged 
				&& 	(!(this instanceof Barony) || this.holdingData.barony.getOccupee() == army.getOwner()) 
				&& (this.getOwner().isEnemy(army.getOwner()) != null || this.getOwner().isSubjectOf(army.getOwner()));
	}
	
	public void setPillaged(boolean pillaged) {
		if (!this.pillaged && pillaged) {
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				smokeTrail = SceneManager.getInstance().createParticleSystem("particle/smoketrail.xml");
				SceneManager.getInstance().getRenderer().removeRenderable((BoundedSceneObject)smokeTrail, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
				SceneManager.getInstance().getRenderer().addRenderable((BoundedSceneObject)smokeTrail, OpenGLRenderer.TRANSLUCENT_CHANNEL_1);
				holdingData.worldEntity.getParent().attachSceneObject(smokeTrail);
				World.getInstance().getUpdater().addItem(smokeTrail);
			}
			
			pillageTimestamp = World.getInstance().getWorldTime();
			
			holdingData.inhabitants = 0;
			for (int i = 0; i < holdingData.population.length; ++i) {
				holdingData.changePop(i, -holdingData.population[i]*PILLAGE_DEATH);
				holdingData.inhabitants += holdingData.population[i];
			}
			
			if (this.project != null) {
				project.abort();
				hideProject();
				project = null;
			}
			
		} else if (this.pillaged && !pillaged) {
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				SceneManager.getInstance().destroyRenderable(smokeTrail, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
				World.getInstance().getUpdater().removeItem(smokeTrail);
			}
		}
		
		this.pillaged = pillaged;
	}
	
	public void addUnrestSource(UnrestSource unrest) {
		
		for (int i = 0; i < unrestSources.size(); ++i) {
			if (unrestSources.get(i).name.equals(unrest.name)) {
				unrestSources.get(i).probability += unrest.probability;
				return;
			}
		}
		
		unrestSources.add(unrest);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork() != null) {
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UnrestSourceChange(
						holdingData.index,
						unrest,
						UnrestSourceChange.ADD
				));
				updateRevoltRisks();
			}
		}
	}
	
	public void removeUnrestSource(UnrestSource unrest) {

		unrestSources.remove(unrest);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UnrestSourceChange(
					holdingData.index,
					unrest,
					UnrestSourceChange.REMOVE
			));
			updateRevoltRisks();
		}
	}
	
	public void removeUnrestSource(String name) {
		
		for (int i = 0; i < unrestSources.size(); ++i) {
			if (unrestSources.get(i).name.equals(name)) {
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {			
					EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UnrestSourceChange(
						holdingData.index,
						unrestSources.get(i),
						UnrestSourceChange.REMOVE
					));
				}
				unrestSources.remove(unrestSources.get(i));

				return;
			}
		}
		
	}
	
	public UnrestSource getUnrestSource(int index) {
		return unrestSources.get(index);
	}
	
	public void clearUnrest() {
		unrestSources.clear();
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork() != null) {
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UnrestSourceChange(
						holdingData.index,
						null,
						UnrestSourceChange.CLEAR
				));
				updateRevoltRisks();
			}
		}
	}
	
	public float getRevoltRisk(UnrestSource src) {
		float stability = (owner.stats[House.UNSTABLE] == 0) ? 1 : 2;
		float revoltees = 0;
		for (int j = 0; j < PopulationType.VALUES.length; ++j) {
			revoltees += (int) (holdingData.population[j] * src.strength);
		}
		
		int totalTroops = holdingData.barony.getLevy().getTotalTroops() + holdingData.barony.getGarrison().getTotalTroops();
		float revoltRisk = 0;
		if (totalTroops > 0) {
			revoltRisk = src.probability * revoltees/(float)Math.sqrt(totalTroops) * stability;
		} else {
			revoltRisk = src.probability * stability * revoltees;
		}
		
		return revoltRisk;
	}
	
	public void updateRevoltRisks() {
		holdingData.revoltRisk = 0;
		int totalTroops = holdingData.barony == this ? (holdingData.barony.getLevy().getTotalTroops() + holdingData.barony.getGarrison().getTotalTroops() ) : 0;
		holdingData.troopRevoltStop = (float) Math.sqrt(totalTroops);
		int countUnrestSources = unrestSources.size();
		
		for (int i = 0; i < countUnrestSources; ++i) {	
			UnrestSource s = unrestSources.get(i);
			holdingData.revoltRisk += getRevoltRisk(s);
		}
		
		holdingData.troopRevoltStop = Math.max(0, holdingData.revoltRisk * holdingData.troopRevoltStop - holdingData.revoltRisk);
		
	}
	
	public void checkRevolts(float time) {
		updateRevoltRisks();
		
		holdingData.accRevoltRisk += holdingData.revoltRisk * time / (World.DAYS_PER_SEASON * World.SECONDS_PER_DAY);

		int countUnrestSources = unrestSources.size();
		
		if (holdingData.accRevoltRisk >= 1) {
		
			holdingData.accRevoltRisk = 0;
			float totalProb = 0;
			
			for (int i = 0; i < countUnrestSources; ++i) {
				totalProb += unrestSources.get(i).probability;
			}
			
			for (int i = 0; i < countUnrestSources; ++i) {
				float rnd = (float) Math.random() * totalProb;
				if (rnd <= unrestSources.get(i).probability) {
					if (owner.getBaronies().size() > 0) {
						Message m = new Message(new HandleRevolt(1), owner, owner, new int[] { holdingData.index, i, 1});
						m.action.send(m.sender, m.receiver, m.options);
					} else {
						Message m = new Message(new HandleRevolt(0), owner, owner, new int[] { holdingData.index, i, 0});
						m.action.send(m.sender, m.receiver, m.options);
					}
					passedRevoltDelay = -REVOLT_DELAY*10;
				}
			}
		
		}
		
	}
	
	public void addProductionAdditive(GoodProduction p) {
		findProd: for (int i = 0; i < productions.size(); ++i) {
			GoodProduction p2 = productions.get(i);
			
			if (p2.getCountInputGoods() != p.getCountInputGoods()) continue;
			if (p2.getCountOutputGoods() != p.getCountOutputGoods()) continue;
			
			for (int j = 0; j < p2.getCountInputGoods(); ++j) {
				if (!p2.getInputGood(j).getName().equals(p.getInputGood(j).getName())) {
					continue findProd;
				}
			}
			
			for (int j = 0; j < p2.getCountOutputGoods(); ++j) {
				if (!p2.getOutputGood(j).getName().equals(p.getOutputGood(j).getName())) {
					continue findProd;
				}
			}
			
			for (int j = 0; j < p.getCountOutputGoods(); ++j) {
				p2.getOutputGood(j).changeQuantity((int)p.getOutputGood(j).getQuantity(), this);
			}
			
			return;
		}
	
		productions.add(p);
	}
	
	public void removeProduction(GoodProduction p) {
		productions.remove(p);
	}
	
	public void checkTechSpread() {
		checkTechSpread(holdingData.barony, Holding.TECH_SPREAD_HOLDING);
		Barony[] baronies = World.getInstance().getMap().getNeighbours(holdingData.barony);
		for (int i = 0; i < baronies.length; ++i) {
			if (getOwner().isVisible(baronies[i].getIndex()))  {
				checkTechSpread(baronies[i], Holding.TECH_SPREAD_BARONY * Holding.TECH_SPREAD_HOLDING);
			}
		}
	}
	
	public void checkTechSpread(Barony barony, float spreadFactor) {
		for (int j = 0, countTechs = getOwner().getCountTechnologies(); j < countTechs; ++j) {
			Technology t = getOwner().getTechnology(j);
			for (int i = 0, countSubHoldings = barony.getCountSubHoldings(); i < countSubHoldings; ++i) {
				Holding h = barony.getSubHolding(i);
				House hOwner = h.getOwner();
				if (hOwner != owner && hOwner.canResearch(t)) {
					float spreadFactorOwner = hOwner.haveSameOverlordWith(owner) ? 1 : Holding.TECH_SPREAD_OUTSIDE_DYNASTY;
					double rnd = Math.random();
					if (rnd <= spreadFactorOwner * spreadFactor) {
						t.onSpread(owner, hOwner);
					}
				}
			}
		}
	}
	
	public void addTradeNeighbour(Barony target) {
		tradeNeighbours.add(target);
	}
	
	public void removeTradeNeighbour(Barony target) {
		tradeNeighbours.remove(target);
	}
	
	public Barony getTradeNeighbour(int index) {
		return tradeNeighbours.get(index);
	}
	
	public void displayProject() {
		if (project != null && projectSystem == null && holdingData.barony.isExplored()) {
			projectSystem = SceneManager.getInstance().createParticleSystem(2);
			GameUpdater.getUpdater().addItem(projectSystem);
			((ComplexParticleSystem) projectSystem).setZWritingDisabled(false);
			holdingData.worldEntity.getParent().createChild().attachSceneObject(projectSystem);
			
			createProjectIcon();
			createProjectBar();
			
			((ComplexParticleSystem) projectSystem).setDynamic(false);
		}
	}
	
	public void hideProject() {
		if (projectSystem != null) {
			SceneManager.getInstance().destroyRenderable(projectSystem);
			projectSystem = null;
		}
	}

	public void finishActiveProject() {
		project.finish();
		
		hideProject();
		
		project = null;
	}
	
	public void displayOwner() {
		if (	!SceneManager.getInstance().getRenderer().isInServerMode() 
			&& 	holdingData.barony.isExplored()) {
			
			if (ownerSystem == null) {
				ownerSystem = SceneManager.getInstance().createParticleSystem(2);
				ownerSystem.setScalable(CameraController.STRATEGIC_VIEW_HEIGHT, 0.5f, 8f);
				((ComplexParticleSystem) ownerSystem).setFixInsertionID(true);
				GameUpdater.getUpdater().addItem(ownerSystem);
				((ComplexParticleSystem) ownerSystem).setZWritingDisabled(false);
				
				Particle particle = new Particle(0.7f);
				particle.setLifeTime(0);
				
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
				ownerMaterial = new ParticleMaterial("SIGILS1", atlas.getRegion(owner.getSigilName()), new Color(1,1,1,1));
			
				ownerMaterial.setBlending(true);
				particle.setMaterial(ownerMaterial);
				particle.setVelocity(Vector3.ORIGIN);
				
				ownerSystem.addParticle(particle);
				
				((ComplexParticleSystem) ownerSystem).checkBoundingBox(particle);
				
				Entity e = holdingData.worldEntity;
				e.getParent().createChild().attachSceneObject(ownerSystem);
				Vector3 offset = HUD_OFFSET.get(e.getSubEntity(0).getMesh());
				Vector3 basePos = e.getParent().getAbsolutePos();
				Vector3.add(basePos, offset, basePos);
				particle.setPos(basePos);
				
				((ComplexParticleSystem) ownerSystem).setDynamic(false);
			
			} else {
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
				ownerMaterial.setRegion(atlas.getRegion(owner.getName()));
				if (!GameUpdater.getUpdater().hasItem(ownerSystem)) {
					GameUpdater.getUpdater().addItem(ownerSystem);
					SceneManager.getInstance().getRenderer().addRenderable((BoundedSceneObject)ownerSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
				}
			}
		} 
	}

	public void hideOwner() {
		GameUpdater.getUpdater().removeItem(ownerSystem);
		SceneManager.getInstance().getRenderer().removeRenderable((BoundedSceneObject)ownerSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
	}
	
	public String toString() {
		return fullName;
	}
	
	public short getHoldingID() {
		return holdingData.index;
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
	}
	
	private void readObject(ObjectInputStream stream) throws IOException {
	}
	
	public void writeInternalsToStream(ObjectOutputStream stream) throws IOException {
		stream.writeFloat(passedTime);
		stream.writeFloat(incomeTime);
		stream.writeFloat(armyBasePos.x);
		stream.writeFloat(armyBasePos.y);
		stream.writeFloat(armyBasePos.z);
		stream.writeFloat(armyOffset.x);
		stream.writeFloat(armyOffset.y);
		stream.writeFloat(armyOffset.z);
		stream.writeFloat(passedRevoltDelay);
		stream.writeFloat(hungerTime);
		stream.writeBoolean(pillaged);
		stream.writeFloat(pillageTimestamp);
		stream.writeObject(holdingData);

		stream.writeShort(productions.size());
		for (int i = 0; i < productions.size(); ++i) {
			stream.writeObject(productions.get(i));
		}
		stream.writeShort(buildings.size());
		for (int i = 0; i < buildings.size(); ++i) {
			stream.writeObject(buildings.get(i));
		}
		stream.writeObject(localStats);
		stream.writeObject(remainingAttractivity);
		stream.writeObject(project);
		stream.writeShort(positionedArmies.size());
		for (int i = 0; i < positionedArmies.size(); ++i) {
			stream.writeObject(positionedArmies.get(i));
		}

		stream.writeShort(unrestSources.size());
		for (int i = 0; i < unrestSources.size(); ++i) {
			stream.writeObject(unrestSources.get(i));
		}
		stream.writeObject(tradeNeighbours);
		stream.writeObject(fullName);
		stream.writeObject(demandMap);
	}
	
	public void readInternalsFromStream(ObjectInputStream stream) throws OptionalDataException, ClassNotFoundException, IOException {
		passedTime = stream.readFloat();
		incomeTime = stream.readFloat();
		armyBasePos = new Vector3(stream.readFloat(), stream.readFloat(), stream.readFloat());
		armyOffset = new Vector3(stream.readFloat(), stream.readFloat(), stream.readFloat());
		passedRevoltDelay = stream.readFloat();
		hungerTime = stream.readFloat();
		pillaged = stream.readBoolean();
		pillageTimestamp = stream.readFloat();
		
		holdingData = (HoldingData) stream.readObject();

		productions = new ArrayList<GoodProduction>();
		short countProductions = stream.readShort();
		for (int i = 0; i < countProductions; ++i) {
			productions.add((GoodProduction) stream.readObject());
		}
		
		buildings = new ArrayList<Building>();
		short countBuildings = stream.readShort();
		for (int i = 0; i < countBuildings; ++i) {
			buildings.add((Building) stream.readObject());
		}
		
		localStats = (float[]) stream.readObject();
		remainingAttractivity = (float[]) stream.readObject();
		project = (IProject) stream.readObject();
		
		positionedArmies = new ArrayList<Army>();
		short countPositionedArmies = stream.readShort();
		for (int i = 0; i < countPositionedArmies; ++i) {
			positionedArmies.add((Army) stream.readObject());
		}
		
		unrestSources = new ArrayList<UnrestSource>();
		short countUnrestSources = stream.readShort();
		for (int i = 0; i < countUnrestSources; ++i) {
			unrestSources.add((UnrestSource) stream.readObject());
		}
		
		tradeNeighbours = (List<Barony>) stream.readObject();
		fullName = (String) stream.readObject();
		
		demandMap = (TIntFloatHashMap) stream.readObject();
	}
	
	public void addDemand(int goodID, float amount) {
		demandMap.put(goodID, demandMap.get(goodID) +  amount);
	}
	
	public float getDemand(int goodID) {
		return demandMap.get(goodID);
	}
	
	public float getSupply(int goodID) {
		float res = 0;
		for (int i = 0; i < holdingData.barony.suppliedGoods.size(); ++i) {
			Good g = holdingData.barony.suppliedGoods.get(i);
			if (goodID == g.getID()) {
				res += g.getQuantity();
			}
		}
		return res;
	}
	
	public float getFoodDemand() {
		return holdingData.inhabitants / 100;
	}
	
	public float getFoodSupply() {
		float res = 0;
		for (int i = 0; i < holdingData.barony.suppliedGoods.size(); ++i) {
			Good g = holdingData.barony.suppliedGoods.get(i);
			if (Arrays.binarySearch(Good.FOOD_IDS, g.getID()) >= 0) {
				res += g.getQuantity();
			}
		}
		return res;
	}
	
	public float getSupplyAttractivity() {
		float totalSupply = 0;
		float totalDemand = 0;
		for (int i = 0; i < Good.COUNT_GOODS; ++i) {
			float demand = getDemand(i);
			totalDemand += demand;
			totalSupply += Math.min(demand, getSupply(i));
		}
		return (getFoodSupply()+totalSupply+1) / (getFoodDemand()+totalDemand+1);
	}

	public int getCountUnrestSources() {
		return unrestSources.size();
	}

	public ParticleSystem getOwnerSystem() {
		return ownerSystem;
	}

	public Army getPositionedArmy(int index) {
		return positionedArmies.get(index);
	}
	
	public void setColor(Color color) {
		Entity e = holdingData.worldEntity;
		((DefaultMaterial3)e.getSubEntity(0).getMaterial()).setColor(color);
	}
	
	public Color getColor() {
		Entity e = holdingData.worldEntity;
		return ((DefaultMaterial3)e.getSubEntity(0).getMaterial()).getColor();
	}
	
	public void removeColor() {
		setColor(Color.BLACK);
	}
	
	public int getCountRoads() {
		return roads.size();
	}
	
	public void addRoad(Holding to) {
		roads.add(to);
		World.getInstance().getMap().getRoadMap().realizePath(getHoldingID(), to.getHoldingID());
	}
	
	public boolean isRoadBuilt(Holding to) {
		return roads.contains(to) || to.roads.contains(this);
	}
	
	public void addGoodAdditive(Good good, int k) {
		if (good.getQuantity(k) == 0) return;
		
		for (int i = 0; i < suppliedGoodsTmp.size(); ++i) {
			
			if (suppliedGoodsTmp.get(i).getName().equals(good.getName())) {
				
				suppliedGoodsTmp.get(i).onRemoveSupply(this);
				
				int producentID = suppliedGoodsTmp.get(i).getProducentID(good.getProducers().get(k));
				
				if (producentID != -1) {
					suppliedGoodsTmp.get(i).changeQuantity(good.getQuantity(k), producentID);
				} else {
					suppliedGoodsTmp.get(i).addProducent(good.getQuantity(k), good.getProducers().get(k));
				}
				
				suppliedGoodsTmp.get(i).onAddSupply(this);
				return;
				
			}
		}
		
		good.onAddSupply(this);
		
		suppliedGoodsTmp.add(good);
	}
	
	public void addGood(Good good, int k) {
		
		if (good.getQuantity(k) == 0) return;
		
		for (int i = 0; i < suppliedGoodsTmp.size(); ++i) {
			
			Good suppliedGood = suppliedGoodsTmp.get(i);
			
			if (suppliedGood.getName().equals(good.getName())) {
				
				int quantity = good.getQuantity(k);
				int producentID = suppliedGood.getProducentID(good.getProducers().get(k));
				
				if (producentID != -1) {
					if (suppliedGood.getQuantity(producentID) >= good.getQuantity(k)) {
						return;
					} else {
						quantity = good.getQuantity(k) - suppliedGood.getQuantity(producentID);
					}
				}
				
				suppliedGood.onRemoveSupply(this);
				
				
				if (producentID != -1) {
					suppliedGood.changeQuantity(quantity, producentID);
				} else {
					suppliedGood.addProducent(quantity, good.getProducers().get(k));
				}
				
				suppliedGood.onAddSupply(this);
				
				
				return;
				
			}
		}

		good.onAddSupply(this);
		
		suppliedGoodsTmp.add(good);
	}
	
	public void resetGoods() {
		for (int i = 0, countSuppliedGoods = suppliedGoods.size(); i < countSuppliedGoods; ++i) {
			Good good = suppliedGoods.get(i);
			
			good.onRemoveSupply(this);
			
			
			for (int j = 0; j < good.getProducers().size(); ++j) {
				good.getProducers().get(j).holdingData.tradeIncome = 0;
			}
		}
	}
	
	public void spreadGoods() {
		WorldMap map = World.getInstance().getMap();
		for (int i = 0, countSuppliedGoods = suppliedGoods.size(); i < countSuppliedGoods; ++i) {
			Good good = suppliedGoods.get(i);
			for (int k = 0, countProducers = good.getProducers().size(); k < countProducers; ++k) {
				Holding neighbours[] = map.getNeighboursHolding(this);
				for (int j = 0; j < neighbours.length; ++j) {
					if (neighbours[j].isRoadBuilt(this)) {
						spreadGoodTo(good, neighbours[j], k);
					}
				}
				for (int j = 0; j < tradeNeighbours.size(); ++j) {
					spreadGoodTo(good, tradeNeighbours.get(j), k);
				}
			}
		}
	}
	
	public void spreadGoodTo(Good good, Holding dest, int k)  {
		House destOwner = dest.getOwner();
		if (destOwner.haveSameOverlordWith(this.getOwner()) || destOwner.getSupremeOverlord().getHouseStat(this.getOwner().getSupremeOverlord(), House.HAS_TRADE_AGREEMENT) == 1) {
			Good goodK = good.copy(k);
			goodK.changeQuantity(-1, 0);
			dest.addGood(goodK, 0);
		} 
	}
	
	public void spreadGoodsFinished() {
		suppliedGoods.clear();
		for (int i = 0, countSuppliedGoodsTmp = suppliedGoodsTmp.size(); i < countSuppliedGoodsTmp; ++i) {
			suppliedGoods.add(suppliedGoodsTmp.get(i));
		}
		suppliedGoodsTmp.clear();
	}
	
	public boolean hasGood(Good good) {
		for (int i = 0, countSuppliedGoods = suppliedGoods.size(); i < countSuppliedGoods; ++i) {
			if (suppliedGoods.get(i).getName().equals(good.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
	public Good getGood(String name) {
		for (int i = 0, countSuppliedGoods = suppliedGoods.size(); i < countSuppliedGoods; ++i) {
			if (suppliedGoods.get(i).getName().equals(name)) {
				return suppliedGoods.get(i);
			}
		}
		
		return null;
	}
	
	public Good getGood(int id) {
		for (int i = 0, countSuppliedGoods = suppliedGoods.size(); i < countSuppliedGoods; ++i) {
			if (suppliedGoods.get(i).getID() == id) {
				return suppliedGoods.get(i);
			}
		}
		
		return null;
	}
	
	public int getCountSuppliedGoods() {
		return suppliedGoods.size();
	}
	
	public Good getSuppliedGood(int index) {
		return suppliedGoods.get(index);
	}

	public int getRoadTarget(int index) {
		return roads.get(index).getHoldingID();
	}
	
	public float getGoodFactor(int goodID) {
		float totalDemand = getDemand(goodID)+1;
		float totalSupply = getSupply(goodID)+1;
		float goodFactor = totalDemand /totalSupply;
		return goodFactor;
	}
}
