package com.tyrfing.games.id17.holdings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.geometry.Grass;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.world.FogMaterial;
import com.tyrfing.games.id17.world.MapFile;
import com.tyrfing.games.id17.world.SeasonMaterial;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.Particle;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Barony extends Holding {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6325256957049816354L;

	public static final float REINFORCEMENT_TICK = World.SECONDS_PER_DAY;
	public static final float MERC_COST = 5;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	private transient List<ParticleSystem> waterfallSystems = new ArrayList<ParticleSystem>();
	
	private transient Particle occupeeParticle;
	private transient WorldChunk worldChunk;
	private transient SceneNode node;
	
	static {
		stats[HoldingTypes.PEASANTS] = 600.f;
		stats[HoldingTypes.WORKERS] = 200.f;
		stats[HoldingTypes.MERCHANTS] = 100.f;
		stats[HoldingTypes.SCHOLARS] = 80.f;
		stats[HoldingTypes.GROWTH] = 0.2f;
		stats[HoldingTypes.INCOME] = 30.f;
		stats[HoldingTypes.MERCHANT_ATTRACTIVITY] = 200f;
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = 300f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 400f;
		stats[HoldingTypes.PEASANTS_ATTRACTIVITY] = 800f;
	}
	
	public boolean explored = true;
	
	private int index;
	
	protected Army garrison;
	protected Army levy;
	
	private Holding[] subHoldings;
	private int countSubHoldings;
	
	protected float reinforcementTime;
	
	private House occupee;
	private MapFile mapFile;
	private BaronyWindow baronyWindow;
	
	public float mercCosts;
	
	public Barony(int maxSubHoldings, HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		node = SceneManager.getInstance().getRootSceneNode().createChild();
		subHoldings = new Holding[maxSubHoldings];
		subHoldings[countSubHoldings++] = this;
	}
	
	public SceneNode getNode() {
		return node;
	}
	
	public void build(MapFile mapFile, BaronyWindow baronyWindow) {
		
		this.mapFile = mapFile;
		this.baronyWindow = baronyWindow;
		
		setWorldChunk(WorldChunk.createFromMapFile(mapFile, node, baronyWindow));
	}
	
	public void build() {
		node = SceneManager.getInstance().getRootSceneNode().createChild(node.getRelativePos());
		setWorldChunk(WorldChunk.createFromMapFile(mapFile, node, baronyWindow));
		worldChunk.build();
	}
	
	public WorldChunk getWorldChunk() {
		return worldChunk;
	}

	public String toString() {
		return holdingData.name;
	}
	
	@Override
	public void onUpdate(float time) {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			holdingData.supplies = (int) ((holdingData.baseSupplies+holdingData.tradeSupplies) * World.getInstance().getSupplyFactor() * owner.stats[House.SUPPLY_MULT]);
			
			reinforcementTime += time;
		
			if (reinforcementTime >= REINFORCEMENT_TICK) {
				reinforcementTime -= REINFORCEMENT_TICK;
				
				float freeTroops = getFreeTroops();
				
				levy.resetReinforcements();
				garrison.resetReinforcements();
				
				mercCosts = 0;
				
				if (freeTroops > 0) {
				
					PopulationType[] types = PopulationType.VALUES;
					float newTroops = 0;
					
					for (int i = 0; i < countSubHoldings; ++i) {
						Holding holding = subHoldings[i];
						if (holding.getOwner() == owner) {
							for (int j = 0; j < types.length && freeTroops > 0; ++j) {
								float troops = (holding.holdingData.population[j] * (PopulationType.POP_STATS[types[j].ordinal()][PopulationType.ARMY_PROB]) * REINFORCEMENT_TICK);
	
								if (troops <= freeTroops) {
			
								} else {
									troops = freeTroops;
								}
								
								holding.holdingData.inhabitants -= troops;
								holding.holdingData.population[j] -= troops;
								
								reinforce(troops, freeTroops, Army.ARMY_REINF_VOLUNTEER);
								freeTroops -= troops;
								newTroops += troops;
								
								if (freeTroops > 0) {
									troops = (holding.holdingData.population[j] * owner.stats[House.CONSCRIPTION] * REINFORCEMENT_TICK);
									
									if (troops <= freeTroops) {
				
									} else {
										troops = freeTroops;
									}
									
									holding.holdingData.inhabitants -= troops;
									holding.holdingData.population[j] -= troops;
									
									reinforce(troops, freeTroops, Army.ARMY_REINF_CONSCRIPT);
									freeTroops -= troops;
									newTroops += troops;
								}
								
							}
						}
					}
					
					if (freeTroops > 0) {
						float mercs = newTroops * owner.stats[House.MERCENARIES];
						if (mercs + newTroops > freeTroops) { 
							mercs = freeTroops - newTroops;
						}
						
						mercCosts += -mercs * Barony.MERC_COST;
						
						reinforce(mercs, freeTroops, Army.ARMY_REINF_MERC);
					}
				}
			
			}
		}
		
		super.onUpdate(time);
		
	}
	
	private float getFreeTroops() {
		float freeTroops = 0;
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Regiment r = levy.getRegiment(i);
			if (r != null && r.reinforcementEnabled) {
				freeTroops += r.maxTroops - r.troops;
			}
		}
		
		for (int i = 2; i < Army.MAX_REGIMENTS; ++i) {
			Regiment r = garrison.getRegiment(i);
			if (r != null && r.reinforcementEnabled) {
				freeTroops += r.maxTroops - r.troops;
			}
		}
		
		if (!owner.isIndependend()) {
			List<Barony> baronies = owner.getOverlord().getBaronies();
			
			for (int i = 0; i < baronies.size(); ++i) {
				freeTroops += baronies.get(i).getFreeTroops() * owner.stats[House.VASSAL_ARMY];
			}
			
			
		}
		return freeTroops;
	}
	
	private void reinforce(float newTroops, float freeTroops, int category) {
		float reinforcements = 0;
		if (!levy.isFighting() && levy.getMoral() > 0) {
			for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
				Regiment r = levy.getRegiment(i);
				if (r != null && r.reinforcementEnabled) {
					float reinforce = newTroops * (r.maxTroops - r.troops) / freeTroops;
					reinforcements += reinforce;
					levy.changeTroops(i, reinforce);
				}
			}
		}
		levy.addReinforcements(reinforcements * World.DAYS_PER_SEASON, category);
		
		reinforcements = 0;
		if (!garrison.isFighting() && garrison.getMoral() > 0) {
			for (int i = 2; i < Army.MAX_REGIMENTS; ++i) {
				Regiment r = garrison.getRegiment(i);
				if (r != null && r.reinforcementEnabled) {
					float reinforce = newTroops * (r.maxTroops - r.troops) / freeTroops;
					reinforcements += reinforce;
					garrison.changeTroops(i, reinforce);
				}
			}
		}
		garrison.addReinforcements(reinforcements * World.DAYS_PER_SEASON, category);
		
		if (!owner.isIndependend()) {
			List<Barony> baronies = owner.getOverlord().getBaronies();
			for (int i = 0; i < baronies.size(); ++i) {
				baronies.get(i).reinforce(newTroops * owner.stats[House.VASSAL_ARMY], freeTroops, Army.ARMY_REINF_VASSAL);
			}
		}
	}
	
	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void addHolding(Holding holding) {
		subHoldings[countSubHoldings++] = holding;
	}
	
	public void raiseArmy() {
		if (levy.getTotalTroops() >= 0.3f * levy.getTotalTroopsMax()) {
			levy.raise();
		}
	}
	
	public Army getGarrison() {
		return garrison;
	}
	
	public Army getLevy() {
		return levy;
	}
	
	public void setLevy(Army levy) {
		this.levy = levy;
		levy.setHome(this);
	}
	
	public void setGarrison(Army garrison) {
		this.garrison = garrison;
		garrison.setHome(this);
		garrison.setCurrentBarony(this);
	}
	
	@Override
	public void controleBy(House house) {
		super.controleBy(house);
		if (levy != null) {
			levy.setOwner(house);
		}
		if (garrison != null) {
			garrison.setOwner(house);
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getCountSubHoldings() {
		return countSubHoldings;
	}
	
	public Holding getSubHolding(int index) {
		return subHoldings[index];
	}
	
	public void createOccupeeParticle(House occupee) {
		occupeeParticle = new Particle(0.7f);
		occupeeParticle.setLifeTime(0);
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
		
		ParticleMaterial mat = new ParticleMaterial("SIGILS1", atlas.getRegion(occupee.getName()), new Color(1,1,1,1));
		mat.setBlending(true);
		occupeeParticle.setMaterial(mat);
		occupeeParticle.setVelocity(Vector3.ORIGIN);
	}
	
	public void updateOccupeeParticlePos() {
		Vector3 basePos = ownerSystem.getParent().getAbsolutePos();
		Vector3 offset = holdingData.worldEntity.getBoundingBox().max.sub(holdingData.worldEntity.getBoundingBox().min);
		offset.z *= 1.2f;
		offset.x *= 0.55f;
		offset.y *= 0.2f;
		basePos = basePos.add(offset).add(OFFSET);
		
		occupeeParticle.setPos(basePos);
	}
	
	public void setOccupied(House occupee){
		if (!SceneManager.getInstance().getRenderer().isInServerMode() && this.isExplored()) {
			if (occupeeParticle == null && occupee != null) {
				createOccupeeParticle(occupee);
								
				if (explored) {
					ownerSystem.addParticle(occupeeParticle);
				}				
				
				updateOccupeeParticlePos();
			} else if (occupeeParticle != null && occupee != null) {
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
				occupeeParticle.getMaterial().setRegion(atlas.getRegion(occupee.getSigilName()));
			} else if (occupeeParticle != null && occupee == null) {
				if (explored) {
					((ComplexParticleSystem) ownerSystem).removeParticle(0, occupeeParticle.getMaterial());
					occupeeParticle = null;
				}
			}
			getWorldChunk().setOccupiedTextures(occupee != null);
		}
		
		this.occupee = occupee;
	}
	
	public House getOccupee() {
		return occupee;
	}
	
	public boolean hasSubHolding(Holding holding) {
		for (int i = 0; i < countSubHoldings; ++i) {
			if (holding == subHoldings[i]) return true;
		}
		
		return false;
	}

	public void setWorldChunk(WorldChunk worldChunk) {
		this.worldChunk = worldChunk;
	}

	public float getInfluence(House house) {
		if (house == null) return SeasonMaterial.NONE;
		if (owner.haveSameOverlordWith(house)) return SeasonMaterial.FULL;
		if (house.isEnemy(owner) != null) return SeasonMaterial.FULL;
		for (int i = 0; i < subHoldings.length; ++i) {
			if (subHoldings[i].getOwner() == house) {
				return SeasonMaterial.FULL;
			}
		}
		
		float influence = SeasonMaterial.NONE;
		
		if (owner.hasMarriage(house)) influence += 0.3f;
		if (house.hasSpy(owner)) influence += 0.3f;
		if (house.hasDiplomaticRelation(owner)) influence += 0.3f;
		if (house.hasClaim(this)) influence += 0.3f;
		if (house.getRival() == owner || owner.getRival() == house) influence += 0.3f;
		
		if (house.isIndependend()) {
			if (house.hasDefensivePact(owner.getSupremeOverlord())) influence += 0.5f;
			if (house.hasTradeAgreement(owner.getSupremeOverlord())) influence += 0.5f;
		} 
		
		if (influence > SeasonMaterial.FULL) influence = SeasonMaterial.FULL;
		
		return influence;
	}

	public void setExplored(boolean state) {
		if (!state && explored) {
			this.explored = state;
			for (int i = 0; i < worldChunk.blockTypes.length; ++i) {
				worldChunk.blockTypes[i].mat.setFogged(true);
				worldChunk.blockTypes_occu[i].mat.setFogged(true);
				
				if (worldChunk.blockTypes[i].baseTile != i) {
					worldChunk.blockTypes[i].mat.setVisible(false);
				}
			}
			
			setWaterfallSystemsVisible(false);
			
			for (int i = 0; i < worldChunk.getCountObjects(); ++i) {
				Entity e = worldChunk.getObject(i);
				e.setCastShadow(false);
				e.getSubEntity(0).setMaterial(new FogMaterial(e.getSubEntity(0).getMaterial(), e.getSkeleton()));
				e.setSkeleton(new Skeleton());
			}
			
			for (int i = 0; i < worldChunk.getCountGrassBuckets(); ++i) {
				for (int j = 0; j < worldChunk.getCountGrasses(i); ++j) {
					Grass g = worldChunk.getGrass(i,j);
					if (g != null) {
						g.grass.getSubEntity(0).setMaterial(new FogMaterial(g.grass.getSubEntity(0).getMaterial(), 
																			g.grass.getSkeleton()));
					}
				}
			}
			
			for (int i = 0; i < countSubHoldings; ++i) {
				subHoldings[i].hideOwner();
				for (int j = 0; j < subHoldings[i].getCountPositionedArmies(); ++j) {
					Army army = subHoldings[i].getPositionedArmy(j);
					army.hide();
				}
			}
			
			getGarrison().checkDestroyHeader();
			
		} else if (state && !explored){
			this.explored = state;
			for (int i = 0; i < worldChunk.blockTypes.length; ++i) {
				worldChunk.blockTypes[i].mat.setFogged(false);
				worldChunk.blockTypes_occu[i].mat.setFogged(false);
				
				if (worldChunk.blockTypes[i].baseTile != i) {
					worldChunk.blockTypes[i].mat.setVisible(true);
				}
			}
			
			for (int i = 0; i < worldChunk.getCountObjects(); ++i) {
				Entity e = worldChunk.getObject(i);
				e.setCastShadow(true);
				FogMaterial m = (FogMaterial) e.getSubEntity(0).getMaterial();
				e.getSubEntity(0).setMaterial(m.getOldMaterial());
				e.setSkeleton(m.getOldSkeleton());
			}
			
			for (int i = 0; i < worldChunk.getCountGrassBuckets(); ++i) {
				for (int j = 0; j < worldChunk.getCountGrasses(i); ++j) {
					Grass g = worldChunk.getGrass(i,j);
					if (g != null) {
						FogMaterial m = (FogMaterial) g.grass.getSubEntity(0).getMaterial();
						g.grass.getSubEntity(0).setMaterial(m.getOldMaterial());
					}
				}
			}
			
			for (int i = 0; i < countSubHoldings; ++i) {
				if (subHoldings[i].isProjectVisibleFor(World.getInstance().getPlayerController().getHouse())) {
					subHoldings[i].displayProject();
				}
				subHoldings[i].displayOwner();
				
				for (int j = 0; j < subHoldings[i].getCountPositionedArmies(); ++j) {
					Army army = subHoldings[i].getPositionedArmy(j);
					army.getEntity().setVisible(true);
					army.checkCreateHeader();
				}
			}
			
			setWaterfallSystemsVisible(true);
			
			if (this.occupee != null) {
				setOccupied(occupee);
			}
			
		}
		
		this.worldChunk.getBorder().setRender(explored);

		World.getInstance().getMap().getFogMap().updateFog(this);
		World.getInstance().getMap().getFogMap().updateFogTexture();
	}

	public boolean isExplored() {
		return explored;
	}

	public void explore(House owner, boolean discovery) {
		owner.addVisibleBarony(this, discovery);
	}

	public void addWaterfall(ParticleSystem waterfall, ParticleSystem waterfallSmoke) {
		waterfallSystems.add(waterfall);
		waterfallSystems.add(waterfallSmoke);
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		
	}

	private void readObject(ObjectInputStream stream) throws IOException {

	}
	
	@Override
	public void writeInternalsToStream(ObjectOutputStream stream) throws IOException {
		super.writeInternalsToStream(stream);
		stream.writeInt(index);
		stream.writeBoolean(explored);
		stream.writeObject(garrison);
		stream.writeObject(levy);
		stream.writeInt(countSubHoldings);
		for (int i = 0; i < countSubHoldings; ++i) {
			stream.writeObject(subHoldings[i]);
		}
		
		stream.writeInt(suppliedGoods.size());
		for (int i = 0; i < suppliedGoods.size(); ++i) {
			stream.writeObject(suppliedGoods.get(i));
		}
		
		stream.writeInt(suppliedGoodsTmp.size());
		for (int i = 0; i < suppliedGoodsTmp.size(); ++i) {
			stream.writeObject(suppliedGoodsTmp.get(i));
		}
		
		stream.writeFloat(reinforcementTime);
		stream.writeObject(occupee);
		stream.writeObject(mapFile);
		stream.writeObject(baronyWindow);
		stream.writeObject(node);
	}
	
	@Override
	public void readInternalsFromStream(ObjectInputStream stream) throws OptionalDataException, ClassNotFoundException, IOException {
		super.readInternalsFromStream(stream);
		index = stream.readInt();
		explored = stream.readBoolean();
		garrison = (Army) stream.readObject();
		levy = (Army) stream.readObject();
		
		countSubHoldings = stream.readInt();
		subHoldings = new Holding[countSubHoldings];
		for (int i = 0; i < countSubHoldings; ++i) {
			subHoldings[i] = (Holding) stream.readObject();
		}
		
		int countSuppliedGoods = stream.readInt();
		suppliedGoods = new ArrayList<Good>();
		for (int i = 0; i < countSuppliedGoods; ++i) {
			suppliedGoods.add((Good) stream.readObject());
		}
		
		int countSuppliedGoodsTmp = stream.readInt();
		suppliedGoodsTmp = new ArrayList<Good>();
		for (int i = 0; i < countSuppliedGoodsTmp; ++i) {
			suppliedGoodsTmp.add((Good) stream.readObject());
		}
		
		reinforcementTime = stream.readFloat();
		occupee = (House) stream.readObject();
		mapFile = (MapFile) stream.readObject();
		baronyWindow = (BaronyWindow) stream.readObject();
		node = (SceneNode) stream.readObject();
		
		waterfallSystems = new ArrayList<ParticleSystem>();
	}

	public void setWaterfallSystemsVisible(boolean b) {
		if (!b) {
			for (int i = 0; i < waterfallSystems.size(); ++i) {
				SceneManager.getInstance().getRenderer().removeRenderable((BoundedSceneObject)waterfallSystems.get(i), OpenGLRenderer.TRANSLUCENT_CHANNEL_1);
			}
		} else {
			for (int i = 0; i < waterfallSystems.size(); ++i) {
				SceneManager.getInstance().getRenderer().addRenderable((BoundedSceneObject)waterfallSystems.get(i), OpenGLRenderer.TRANSLUCENT_CHANNEL_1);
			}
		}
	}
	
	public int getTotalCountRoads() {
		int res = 0;
		for (int i = 0; i < subHoldings.length; ++i) {
			res += subHoldings[i].getCountRoads();
		}
		return res;
	}

}
