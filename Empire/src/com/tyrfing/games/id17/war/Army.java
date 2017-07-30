package com.tyrfing.games.id17.war;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.GameUpdater;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.actions.RevokeHolding;
import com.tyrfing.games.id17.gui.CameraController;
import com.tyrfing.games.id17.gui.PickerGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrfing.games.id17.world.WorldMap;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.game.Stats;
import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.Particle;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderables.Text2;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;


public class Army implements IUpdateable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4258026386076807536L;
	
	public static final int ARMY_REINF_VOLUNTEER = 0;
	public static final int ARMY_REINF_CONSCRIPT = 1;
	public static final int ARMY_REINF_MERC = 2;
	public static final int ARMY_REINF_VASSAL = 3;
	
	public static final float PILLAGE_MORAL = 0.5f;
	public static final float PILLAGE_RULER_FACTOR = 1/3.f;
	
	public static final float MARAUDE_RELATION_HIT = -100;
	public static final float MARAUDE_RELATION_RECOVERY = -0.5f;
	public static final float PILLAGE_RELATION_HIT_ENEMY = -10;
	public static final float PILLAGE_RELATION_HIT_VASSAL = -30;
	public static final float PILLAGE_VASSAL_TYRANNY = 2;
	public static final float PILLAGE_RELATION_HIT_DURATION = World.DAYS_PER_YEAR * World.SEASONS_PER_YEAR * 10;
	public static final float PILLAGE_HONOR_HIT = -1;
	
	public static final int MAX_REGIMENTS = 4;
	public static final float SCALE = -0.85f;
	public static final float MORAL_REGEN = 0.002f;
	
	public static final float MAINTANANCE = 0.01f;
	public static final float STATIONED_FACTOR = 1;
	
	public static final float PILLAGE_TIME = World.SECONDS_PER_DAY * 30;
	
	public static final float DESERTION_NO_MONEY = 0.01f;
	
	public boolean retreating;
	
	private Regiment[] regiments = new Regiment[MAX_REGIMENTS];
	protected transient Entity entity;
	
	public static final float SPEED = 0.7f;
	
	protected float moral = 1;
	
	protected transient boolean headerVisible = false;
	protected transient SceneNode infoNode;
	private transient Particle particle;
	private transient Particle ownerParticle;
	private transient ParticleMaterial mat;
	
	private transient Text2 text;
	private transient SceneNode textNode;
	
	private House owner;
	private Barony home;
	
	private Holding current;
	
	private List<Integer> path = new ArrayList<Integer>();
	private Integer nextTarget;
	
	protected transient SceneNode node;
	
	private float travelled;
	
	private boolean travelling;
	private Battle battle;
	
	public float totalTroops;
	
	private int troopsLastFrame;
	
	private Vector3 forward = new Vector3(0,-1,0);
	
	private transient List<ArrowPath> arrowPaths = new ArrayList<ArrowPath>();
	
	public static final float EPS = 0.001f;
	
	public transient TextureRegion green;
	public transient TextureRegion brown;
	public transient TextureRegion moralRegion;
	public transient TextureRegion pillageRegion;
	
	public static final Vector3 BATTLE_OFFSET = new Vector3(1, 1, 0);
	
	private transient ParticleSystem system;
	private transient ComplexParticleSystem pillageSystem;
	
	private boolean dead = false;
	
	private boolean raised = false;
	
	private int countRegiments;
	
	private float desertionRate;
	
	private float pillageProgress = -1;
	private float pillageFactor = 1;
	
	public short id;
	
	public int maint;

	public float reinforcements;
	public float[] reinf = new float[4];
	public float raisedTime;
	
	public static final float MARAUDERS_RAISED_MIN = 1 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	public static final int MARAUDER_LOSS_HONOR_HIT = -100;
	
	public Army() {
		World.getInstance().getUpdater().addItem(this);
		id = (short) World.getInstance().armies.size();
		World.getInstance().armies.add(this);
	}
	
	public float getMaintainanceCosts() {
		
		float baseMaintainance = 0;
		if (getHome() != null) {
			for (int i = 0; i < MAX_REGIMENTS; ++i) {
				Regiment r = regiments[i];
				if (r != null && r.unitType != UnitType.Walls) {
					Stats stats = UnitType.UNIT_STATS.get(r.unitType);
					float demandFactor = 1;
					int goodID = (int) stats.getStat(UnitType.GOOD_DEMAND_1_ID);
					if (goodID != 0) {
						goodID--;
						float demand = stats.getStat(UnitType.GOOD_DEMAND_1_AMOUNT);
						demandFactor *= getHome().getGoodFactor(goodID)*demand;
					}
					
					demandFactor = Math.max(1, demandFactor);
					
					goodID = (int) stats.getStat(UnitType.GOOD_DEMAND_2_ID);
					if (goodID != 0) {
						goodID--;
						float demand = stats.getStat(UnitType.GOOD_DEMAND_2_AMOUNT);
						demandFactor *= getHome().getGoodFactor(goodID)*demand;
					}
					
					demandFactor = Math.max(1, (float)Math.sqrt(demandFactor));
					baseMaintainance += r.troops * MAINTANANCE * demandFactor;
				}
			}
		}
		
		if (isRaised()) {
			float supplies = current.holdingData.supplies;
			float troops = this.getTotalTroops();
			float boughtSupplies = Math.max(troops, troops - supplies);
			float positionFactor = current.getOwner().haveSameOverlordWith(owner) ? 1 : 3;
			return (baseMaintainance + boughtSupplies * MAINTANANCE * positionFactor) * owner.stats[House.ARMY_MAINT_MULT];
		} else {
			return baseMaintainance * STATIONED_FACTOR * owner.stats[House.ARMY_MAINT_MULT];
		}
	}
	
	public float getDesertionRate() {
		return desertionRate;
	}
	
	public float getTroopChangeRate() {
		return reinforcements - desertionRate * World.SECONDS_PER_DAY * World.DAYS_PER_SEASON * getTotalTroops();
	}

	public void setOwner(House house) {
		this.owner = house;
		
		if (isRaised()) {
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (ownerParticle != null) {
					TextureRegion region = SceneManager.getInstance().getTextureAtlas("SIGILS1").getRegion(owner.getSigilName());
					ownerParticle.getMaterial().setRegion(region);
				}
			}
			if (isFighting()) {
				getBattle().end();
			}
		}
	}
	
	public void setHome(Barony barony) {
		home = barony;

	}
	
	public Barony getHome() {
		return home;
	}
	
	public void addRegiment(Regiment regiment) {
		regiments[regiment.formationPos]= regiment;
		if (regiment != null && regiment.unitType != UnitType.Walls) {
			totalTroops += regiment.troops;
		}
		countRegiments++;
	}
	
	public void changeTroops(int regimentIndex, float count) {
		if (regiments[regimentIndex].troops > -count) {
			regiments[regimentIndex].troops += count;
			if (regiments[regimentIndex].unitType != UnitType.Walls) {
				totalTroops += count;
			}
		} else {
			if (regiments[regimentIndex].unitType != UnitType.Walls) {
				totalTroops -= regiments[regimentIndex].troops;
			}
			regiments[regimentIndex].troops = 0;
		}
	}
	
	public int getCountRegiments() {
		return countRegiments;
	}
	
	public void raise() {
		raise(home);
	}
	
	public void raise(Holding holding) {
		if (!raised) {
			createOn(holding);
			
			World.getInstance().addRaisedArmy(this);
			
			current = holding;
			
			raised = true;
			
			arrive();
			
			reposition();
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																						LevyAction.RAISE,
																						holding.getHoldingID()));	
			}
		}
	}
	
	public void reposition() {
		if (!this.isFighting()) {
			position(current);
			setRotation(Quaternion.fromAxisAngle(new Vector3(0,0,1), -135));
		} else {
			position(current);
			setRotation(Quaternion.fromAxisAngle(new Vector3(0,0,1), -135));
		}
	}
	
	public void createOn(Holding holding) {
		entity = SceneManager.getInstance().createEntity("entities/knight.iqm", !SceneManager.getInstance().getRenderer().isInServerMode());
		entity.setMask(WorldChunk.ARMY_MASK);
		
		node = SceneManager.getInstance().getRootSceneNode().createChild();
		node.scale(new Vector3(SCALE, SCALE, SCALE));
		node.attachSceneObject(entity);
		entity.playAnimation("Walk");
		entity.onUpdate(0.1f);
		
		//entity.setBoundingBoxVisible(true);
		
		infoNode = entity.getParent().createChild();
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode() && holding.holdingData.barony.isExplored()) {
			checkCreateHeader();		
		}
		
		if (!World.getInstance().areDetailsVisible() || !holding.holdingData.barony.isExplored()) {
			entity.setVisible(false);
		}
		
	}
	
	public void setCurrentBarony(Barony barony) {
		current = barony;
	}
	
	public void unraise() {
		if (isFighting()) {
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				battle.win(battle.getOther(this), this);
			}
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																					LevyAction.UNRAISE,
																					(short) 0));	
		}
		
		if (owner.isMarauder()) {
			if (owner.getCountWars() > 0) {
				House other = owner.getWar(0).getOther(owner);
				owner.getWar(0).end();
				other.updateBorders();
			}
			destroyMarauder();
		}
		
		destroy();
	}
	
	public boolean isRaised() {
		return raised;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public float getMoral() {
		return moral;
	}
	
	public void setMoral(float value) {
		moral = value;
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	public Particle getOwnerParticle() {
		return ownerParticle;
	}
	
	public Text2 getText2() {
		return text;
	}
	
	public int getTotalTroops() {
		return (int) totalTroops;
	}
	
	public int getTotalTroopsMax() {
		int res = 0;
		for(int i = 0; i < MAX_REGIMENTS; ++i) {
			if (regiments[i] != null && regiments[i].unitType != UnitType.Walls) {
				res += regiments[i].maxTroops;
			}
		}
		
		return res;
	}
	
	public void select() {
		if (mat != null) {
			mat.setBlending(true);
		}
	}
	
	public void deselect() {
		if (mat != null) {
			mat.setBlending(true);
		}
	}
	
	public House getOwner() {
		return owner;
	}
	
	public String toString() {
		if (home != null) {
			if (owner.isMarauder()) {
				return "Marauder";
			} else {
				return home.getLinkedName() + " Army";
			}
		} else {
			return owner.getWar(0).getOther(owner).getName() + " Rebels";
		}
	}
	
	public Regiment getRegiment(int index) {
		return regiments[index];
	}
	
	public float getRegimentAverage() {
		float res = 0;
		int countRegiments = 0;
		for(int i = 0; i < MAX_REGIMENTS; ++i) {
			if (regiments[i] != null) {
				res += regiments[i].troops; 
				countRegiments++;
			}
		}
		
		return res / countRegiments;
	}
	
	public float getRegimentAverage(int index) {
		float average = getRegimentAverage();
		if ((int) average == 0) return 0;
		return regiments[index].troops / average;
	}
	
	public void moveTo(Holding holding) {
		
		if (retreating && travelling || !raised) {
			return;
		}
		
		if (holding == current) {
			endTravel();
			arrive();
			return;
		}
		
		if (pillageProgress != -1) {
			endPillage();
		}
		
		if (isFighting()) {
			retreating = true;
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				battle.win(battle.getAttacker() == this ? battle.getDefender() : battle.getAttacker(), this);
			}
		} else {
		
			Army army = holding.getMainPositionedArmy();
			
			if (retreating || army == null || army.getOwner().isEnemy(this.getOwner()) != null) {
				
				if (!retreating) {
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																								LevyAction.MOVETO,
																								holding.getHoldingID()));	
					}
				}
				
				path = World.getInstance().getMap().getPath(owner, current.getHoldingID(), holding.getHoldingID());
				travelled = 0;
				nextTarget = null;
				
				if (path.size() == 1) {
					if (World.getInstance().getHoldings().get(path.get(0)) == current) {
						path = null;
						return;
					}
				}
				
				if (!travelling) {
					travelling = true;
					entity.playAnimation("Walk");
				} else {
					for (int i = 0; i < arrowPaths.size(); ++i) {
						arrowPaths.get(i).destroy();
					}
					arrowPaths.clear();
				}
				
				buildArrowPaths();
			
			}
		}
	
	}
	
	public void createArrowPaths() {
		arrowPaths = new ArrayList<ArrowPath>();
		
		if (this.isTravelling()) {
			ArrowPath arrowPath = new ArrowPath(this, getCurrentHolding(), 
												World.getInstance().getHolding(nextTarget));
			arrowPaths.add(arrowPath);
			
			for (int i = 0; i < path.size(); ++i) {
				Holding src = World.getInstance().getHolding(nextTarget);
				if (i > 0){
					src = World.getInstance().getHoldings().get(path.get(i-1));
				}
				arrowPath = new ArrowPath(this, src, World.getInstance().getHolding(path.get(i)));
				arrowPaths.add(arrowPath);
				if (!current.holdingData.barony.isExplored()) {
					arrowPath.setVisible(false);
				}
			}
		}
	}
	
	private void buildArrowPaths() {
		for (int i = 0; i < path.size(); ++i) {
			Holding src = null;
			if (i > 0){
				src = World.getInstance().getHoldings().get(path.get(i-1));
			}
			ArrowPath arrowPath = new ArrowPath(this, src, World.getInstance().getHolding(path.get(i)));
			arrowPaths.add(arrowPath);
			if (!isInExploredBarony()) {
				arrowPath.setVisible(false);
			}
		}
		
	}

	public float getMoralRegen() 
	{
		float regen = 0;
		if (owner.getHonor() < 0) {
			regen = MORAL_REGEN * owner.getHonor()/300;
		}
		
		if (owner.getGold() >= 0) {
			regen += MORAL_REGEN;
		} else {
			regen -= MORAL_REGEN;
		}
		
		return regen;
	}
	
	@Override
	public void onUpdate(float time) {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (this.isRaised() && !this.isFighting() && (int)this.totalTroops <= 0) {
				this.kill();
			}
		}
		
		if (isRaised()) {
			raisedTime += World.getInstance().getPlaySpeed() * time;
		} else {
			raisedTime = 0;
		}
		
		if (travelling) {
			entity.onUpdate(time);
			if (nextTarget == null) {
				if (!path.isEmpty()) {
					next();
				} else {
					travelling = false;
				}
			} else {
				
				Army other = World.getInstance().getHolding(nextTarget).getMainPositionedArmy();
				if (!retreating && other != null && (other.getOwner().isEnemy(owner) == null || (other.isFighting() && !other.getBattle().isSiege()))) {
					for (int i = 0; i < arrowPaths.size(); ++i) {
						arrowPaths.get(i).destroy();
					}
					arrowPaths.clear();
					travelling = false;
					path.clear();
					
					nextTarget = null;
					position(current);
					travelled = 0;
				} else {
				
					float distance = World.getInstance().getMap().getDistance(current.getHoldingID(), nextTarget);
					float percent = travelled / distance;
					if (percent > 1) percent = 1;
					arrowPaths.get(0).update(percent);
					
					float factor = 1;
					Holding targetHolding = World.getInstance().getHolding(nextTarget);
					if (!owner.isVisible(targetHolding.holdingData.barony.getIndex())) {
						factor += WorldMap.UNEXPLORED_FACTOR;
					}
					
					if (current.isRoadBuilt(targetHolding)) {
						factor *= 0.5f;
					}
					
					travelled += time * SPEED * World.getInstance().getPlaySpeed() * World.getInstance().getTravelFactor() * owner.stats[House.ARMY_SPEED_MULT] / factor;
					
					if (travelled > distance && EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					
						if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
							EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																									LevyAction.ARRIVE,
																									(short) 0));	
						}
						
						arriveAtWaypoint();
					
					}
				}
			}
		} 
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			
			updateMoral(time * getMoralRegen() * World.getInstance().getPlaySpeed());
			
			
			if (!this.isRaised() && this.isFighting()) {
				if (home != null && home.getMainPositionedArmy() != null) {
					Battle battle = this.getBattle();
					float duration = battle.getDuration();
					
					float totalInhabitants = 0;
					for (int i = 0; i < home.holdingData.barony.getCountSubHoldings(); ++i) {
						totalInhabitants += home.holdingData.barony.getSubHolding(i).holdingData.inhabitants;
					}
					
					float freeSupplies = home.holdingData.barony.holdingData.supplies - totalInhabitants;
					
					float defValue = this.getTotalTroops() + (freeSupplies/10-duration*1.5f)/10;
					if (defValue <= 0) defValue = 1;
				
					float change = -time * World.getInstance().getPlaySpeed() * (MORAL_REGEN/25) * home.getMainPositionedArmy().getTotalTroops() / defValue;
					updateMoral(change);
				}
			}
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode() && isInExploredBarony()) {
			if (this.isRaised() || this.isFighting()) {
				updateRaisedOrFighting(time);
			}	
		}
		
		desertion(time);
		
		maint = (int) getMaintainanceCosts();
		float maintenance = -maint * World.getInstance().getPlaySpeed() * time/(World.DAYS_PER_SEASON * World.SECONDS_PER_DAY);
		owner.changeGold(maintenance);
		
		updatePillage(time);
	}

	public void updateRaisedOrFighting(float time) {
		checkCreateHeader();
		
		int troops = getTotalTroops();
		if (troops != troopsLastFrame) {
			text.setText(String.valueOf(troops));
			troopsLastFrame = troops;
		}
		
		moralRegion.u2 = 1 - moral/2f;
		moralRegion.u1 = 0.5f - moral/2f;
	
		if (World.getInstance().getPlayerController().isEnemy(owner) != null) {
			particle.getMaterial().setTexture(TextureManager.getInstance().getTexture("MORALENEMY"));
		} else if (owner.haveSameOverlordWith(World.getInstance().getPlayerController().getHouse()) || owner.getController() == World.getInstance().getPlayerController()) {
			particle.getMaterial().setTexture(TextureManager.getInstance().getTexture("MORALALLY"));
		} else {
			particle.getMaterial().setTexture(TextureManager.getInstance().getTexture("MORAL"));
		}
		
		if (isFighting() && this.isRaised()) {
			entity.onUpdate(time);
		}
	}
	
	private void updatePillage(float time) {
		if (pillageProgress >= 0 && this.isInExploredBarony() && pillageRegion != null) {
			pillageProgress += time * World.getInstance().getPlaySpeed();
			
			float progress = pillageProgress/(PILLAGE_TIME* pillageFactor);
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				pillageRegion.u2 = 1 - progress/2f;
				pillageRegion.u1 = 0.5f - progress/2f;
			}
			
			if (pillageProgress >= PILLAGE_TIME * pillageFactor) {
				applyPillage();
				
				if (!owner.isMarauder()) {
					owner.changeHonor(Army.PILLAGE_HONOR_HIT);
				}
				
				float relationHit = Army.PILLAGE_RELATION_HIT_ENEMY;
				
				if (current.getOwner().getSupremeOverlord() == this.getOwner()) {
					relationHit = Army.PILLAGE_RELATION_HIT_VASSAL;
					for (int i = 0; i < owner.getSubHouses().size(); ++i) {
						owner.getSubHouses().get(i).addStatModifier(new StatModifier("Tyranny", House.RELATION_STAT, owner.getSubHouses().get(i), owner, RevokeHolding.TYRANNY_DURATION, -Army.PILLAGE_VASSAL_TYRANNY));
					}
				} 
				
				if (current.getOwner() != this.getOwner()) {
					current.getOwner().addStatModifier(new StatModifier("Pillaged", House.RELATION_STAT, current.getOwner(), owner,  Army.PILLAGE_RELATION_HIT_DURATION, relationHit));
				}
			}
		}
	}
	
	public void applyPillage() {
		endPillage();
		
		float stolenMoney = 0;
		if (current.getOwner().getGold() >= 0) {
			stolenMoney = current.getOwner().getGold() * (1.0f/current.getOwner().getHoldings().size()) * Holding.PILLAGE_MONEY;
			current.getOwner().changeGold(-stolenMoney);
		}
		
		float plunder = 0;
		for (int i = 0; i < current.holdingData.population.length; ++i) {
			plunder += current.holdingData.population[i] * 0.2f;
		}
		
		plunder += current.holdingData.tradeIncome;
		
		owner.changeGold(stolenMoney + plunder);
		updateMoral(PILLAGE_MORAL);
		
		current.setPillaged(true);
		
		War war = current.getOwner().isEnemy(owner);
		if (war != null) {
			int factor = 1;
			if (war.defenders.contains(owner)) {
				factor = -1;
			} 
			war.pillageProgress += factor * 0.05f;
			war.changeProgress(factor * 0.05f, owner);
		}
	}
	
	private void desertion(float time) {
		desertionRate = 0;
		
		if (this.moral < 0.1f) {
			desertionRate = Army.DESERTION_NO_MONEY;
		}
		
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Regiment r =  regiments[i];
			if (r != null && r.unitType != UnitType.Walls) {
				float deserters = desertionRate * time * World.getInstance().getPlaySpeed() * r.troops / this.getCountRegiments();
				
				if (r.troops < deserters) {
					totalTroops -= r.troops;
					r.troops = 0;
				} else {
					r.troops -= deserters;
					totalTroops -= deserters;
				}
			}
		}
	}
	
	public int getPillagePotential() {
		float stolenMoney = 0;
		if (current.getOwner() != this.getOwner()) {
			if (current.getOwner().getGold() >= 0) {
				stolenMoney = current.getOwner().getGold() * (1.0f/current.getOwner().getHoldings().size()) * Holding.PILLAGE_MONEY;
			}
		}
		
		float plunder = 0;
		for (int i = 0; i < current.holdingData.population.length; ++i) {
			plunder += current.holdingData.population[i] * 0.2f;
		}
		
		plunder += current.holdingData.tradeIncome;
	
		return (int) (stolenMoney + plunder);
	}
	
	public boolean isInExploredBarony() {
		return current != null && current.holdingData.barony.isExplored();
	}
	
	public void arriveAtWaypoint() {
		
		if (nextTarget != null) {
			current.removePositionedArmy(this);
			
			current = World.getInstance().getHolding(nextTarget);
			nextTarget = null;
			
			position(current);
			travelled = 0;
			
			arrowPaths.get(0).destroy();
			arrowPaths.remove(0);
		}
		arrive();
		
	}
	
	public void updateMoral(float time) {
		moral = Math.max(Math.min(1, moral +  time), 0);
	}
	
	public void arrive() {
		
		if (current == null) return ;
		
		if (owner != null) {
			if (!owner.isVisible(current.holdingData.barony.getIndex())) {
				current.holdingData.barony.explore(owner, true);
			}
		}
		
		if (World.getInstance().areDetailsVisible()) {
			if (current.holdingData.barony.isExplored()) {
				entity.setVisible(true);
			} else {
				entity.setVisible(false);
			}
		}
		
		retreating = false;
		Army army = current.getMainPositionedArmy();
		current.addPositionedArmy(this);
		
		if (army == null || army == this) {
			House occupee = current.holdingData.barony.getOccupee();
			if (occupee == null || owner.isEnemy(occupee) != null) {
				if (arrowPaths.size() == 0 && current instanceof Barony) {
					War war = current.getOwner().isEnemy(this.getOwner());
						
					if (war != null) {
						if (!(this.getOwner().isMarauder() && current.isPillaged())) {
							initSiege(war);
						}
					}
				}
			}
			
		} else {
			War war = army.getOwner().isEnemy(this.getOwner());
			if (war != null) {
				
				endTravel();
				army.endTravel();
				
				Battle ongoing = army.getBattle();
				
				if (ongoing != null) {
					ongoing.end();
				}
				
				Battle battle = new Battle(this, army, current, war);
				if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
					PickerGUI gui = World.getInstance().getMainGUI().pickerGUI;
					if (gui.armyGUI.displayed == this || gui.armyGUI.displayed == army) {
						gui.showBattle(battle);
					}
				}
			}
		}
	}
	
	public void endTravel() {
		for (int i = 0; i < arrowPaths.size(); ++i) {
			arrowPaths.get(i).destroy();
		}
		arrowPaths.clear();
		travelling = false;
		nextTarget = null;
		
		if (path != null) {
			path.clear();
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																					LevyAction.STOP,
																					(short) 0));
		}
	}
	
	public void positionAsAttacker() {
		this.position(current);
		entity.getParent().translate(BATTLE_OFFSET);
		if (!SceneManager.getInstance().getRenderer().isInServerMode() && isInExploredBarony()) {
			checkCreateHeader();
		}
	}
	
	public void positionAsDefender() {
		this.position(current);
	}
	
	private void initSiege(War war) {
		
		Battle battle = new Battle(this, ((Barony)current).getGarrison(), current, war);
		battle.makeSiege();
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			PickerGUI gui = World.getInstance().getMainGUI().pickerGUI;
			if (gui.armyGUI.displayed == this) {
				gui.showBattle(battle);
			}
		}
		
		((Barony)current).getGarrison().setBesieged(true);
	}
	
	public void setBesieged(boolean besieged) {
		if (besieged) {
			if (!SceneManager.getInstance().getRenderer().isInServerMode() && home.isExplored()) {
				infoNode = home.getNode().createChild();
				
				system = SceneManager.getInstance().createParticleSystem(1);
				system.setScalable(CameraController.STRATEGIC_VIEW_HEIGHT, 0.5f, 6f);
				GameUpdater.getUpdater().addItem(system);
				
				particle = new Particle(1.4f);
				particle.setLifeTime(0);
				
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MORAL");
				green = atlas.getRegion("GREEN");
				
				moralRegion = new TextureRegion(green);
				
				mat = new ParticleMaterial("MORAL", moralRegion, new Color(1,1,1,1));
				mat.setBlending(true);
				particle.setMaterial(mat);
				particle.setVelocity(Vector3.ORIGIN);
				
				system.addParticle(particle);
				
				infoNode.attachSceneObject(system);
				
				text = new Text2("" + getTotalTroops(), -135, Color.WHITE, SceneManager.getInstance().getFont(WindowManager.getInstance().getSkin().LABEL_FONT));
				SceneManager.getInstance().getRenderer().addRenderable(text, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
				textNode = SceneManager.getInstance().getRootSceneNode().createChild();
				textNode.attachSceneObject(text);
				textNode.translate(new Vector3(-1, -1, 0));
				headerVisible = true;
				
				updateHeader();
			}
		} else if (headerVisible) {
			SceneManager.getInstance().destroyRenderable(system);
			SceneManager.getInstance().destroyRenderable(text, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			GameUpdater.getUpdater().removeItem(system);
		}
	}
	
	public void regenMoral(float moral) {
		this.moral = Math.min(1, this.moral + moral);
	}
	
	public boolean isFighting() {
		return battle != null;
	}
	
	private void next() {
		nextTarget = path.get(0);
		path.remove(0);
		if (entity.getParent() != null) {
			Vector3 pos = entity.getParent().getCachedAbsolutePos();
			Vector3 targetPos = World.getInstance().getHolding(nextTarget).holdingData.worldEntity.getParent().getCachedAbsolutePos();
			pos.z = targetPos.z;
			
			Vector3 vectorTo = pos.vectorTo(targetPos);
			vectorTo.normalize();
			Vector3 dep = forward.cross(vectorTo);
			forward.normalize();
			
			if (Math.abs(dep.x) > EPS || Math.abs(dep.y) > EPS || Math.abs(dep.z) > EPS) {
				Quaternion quat = Quaternion.rotateTo(forward, vectorTo);
				rotate(quat);
			} else if (Math.abs(forward.x + vectorTo.x) < EPS && Math.abs(forward.y + vectorTo.y) < EPS && Math.abs(forward.z + vectorTo.z) < EPS) {
				rotate(Quaternion.fromAxisAngle(new Vector3(0,0,1), 180));
			}
		}
	}
	
	public void rotate(Quaternion rot) {
		forward = rot.multiply(forward);
		node.rotate(rot);
	}
	
	public void setRotation(Quaternion rot) {
		forward = rot.multiply(new Vector3(0,-1,0));
		node.setRelativeRot(rot);
	}

	@Override
	public boolean isFinished() {
		return dead;
	}
	
	public void position(Holding holding) {
		
		if (!owner.isVisible(holding.holdingData.barony.getIndex())) {
			holding.holdingData.barony.explore(owner, true);
		} 
		
		
		Vector3 basePos = holding.getArmyBasePos();
		Vector3 offset = new Vector3(holding.getArmyOffset());
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (holding.holdingData.barony.isExplored()) {
				checkCreateHeader();
				updateHeader(holding);
			} else {
				checkDestroyHeader();
			}
		} 
		
		basePos = basePos.add(offset);
		entity.getParent().setRelativePos(basePos);
	}
	
	public void checkCreateHeader() {
		if (!headerVisible && infoNode != null) {
			headerVisible = true;
			system = SceneManager.getInstance().createParticleSystem(2);
			system.setScalable(CameraController.STRATEGIC_VIEW_HEIGHT, 0.5f, 6f);
			GameUpdater.getUpdater().addItem(system);
			
			particle = new Particle(1.75f);
			particle.setLifeTime(0);
			
			TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MORAL");
			green = atlas.getRegion("GREEN");
			
			moralRegion = new TextureRegion(green);
			
			mat = new ParticleMaterial("MORAL", moralRegion, new Color(1,1,1,1));
			mat.setBlending(true);
			particle.setMaterial(mat);
			particle.setVelocity(Vector3.ORIGIN);
			
			system.addParticle(particle);
			
			ownerParticle = new Particle(0.8f);
			ownerParticle.setLifeTime(0);
			ownerParticle.setScaleSpeed(0.85f);
			
			atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
			ParticleMaterial mat2 = new ParticleMaterial("SIGILS1", atlas.getRegion(owner.getSigilName()), new Color(1,1,1,1));
			mat2.setBlending(true);
			ownerParticle.setMaterial(mat2);
			ownerParticle.setVelocity(new Vector3());
			
			system.addParticle(ownerParticle);
			
			infoNode.attachSceneObject(system);
			
			text = new Text2("" + getTotalTroops(), -135, Color.WHITE, SceneManager.getInstance().getFont(WindowManager.getInstance().getSkin().LABEL_FONT));
			SceneManager.getInstance().getRenderer().addRenderable(text, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			textNode = SceneManager.getInstance().getRootSceneNode().createChild();
			textNode.attachSceneObject(text);	
		} else if (!headerVisible && infoNode == null) {
			setBesieged(true);
		}
		
		updateHeader();
	}
	
	public Text2 getTroopText() {
		return text;
	}
	
	public void updateTroopTextRot() {
		textNode.setRelativeRot(Quaternion.rotateTo(new Vector3(0,0,1), SceneManager.getInstance().getActiveCamera().getLookDirection().multiply(-1)));
	}
	
	public void checkDestroyHeader() {
		if (headerVisible) {
			headerVisible = false;
			SceneManager.getInstance().destroyRenderable(system);
			SceneManager.getInstance().destroyRenderable(text, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			textNode.detach();
		}
	}
	
	public void setBattle(Battle battle) {
		this.battle = battle;
		
		if (battle != null) {
			if (entity != null) {
				entity.stopAnimation("Walk");
				entity.playAnimation("Attack");
				entity.onUpdate((float)Math.random()*10);
			}
		} else if (entity != null) {
			entity.stopAnimation("Attack");
			entity.playAnimation("Walk");
			entity.onUpdate((float)Math.random()*10);
		}
	}
	
	public Battle getBattle() {
		return battle;
	}
	
	public void receiveDamage(float moralDmg) {
		moral -= moralDmg;
	}
	
	public void randomRetreat() {
		
		retreating = true;
		if (current != null) {
			current.removePositionedArmy(this);
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(current);
				int random = (int)(Math.random() * neighbours.length);
				if (neighbours.length > 0)  {
					Holding randomHolding = neighbours[random];
					
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																								LevyAction.RETREATTO,
																								randomHolding.getHoldingID()));	
					}
					
					moveTo(randomHolding);
				}
			}
		}
	}
	
	public void kill() {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																					LevyAction.KILL,
																					(short) 0));	
		}
		
		if (isFighting() && EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			battle.win(battle.getOther(this), this);
		}
		
		if (owner.isMarauder()) {
			if (owner.getCountWars() > 0) {
				War war = owner.getWar(0);
				war.win(war.getOther(owner));
			}
		}
		
		destroy();
	}
	
	public void onMarauderWarLost(War war) {
		House other = war.getOther(owner);
		other.addJustification(new WarJustification("Revenge", null, owner, other));
		
		if (owner.getController() == World.getInstance().getPlayerController()) {
			HeaderedMail mail = new HeaderedMail(	
					"Our marauding army stands defeated!", 
					"Our troops have surrendered to the\n" +
					other.getLinkedName() + " and it has become public\n" +
					"knowledge that we were controlling them.\n\n" +
					"We lose: " + Util.getFlaggedText(String.valueOf(MARAUDER_LOSS_HONOR_HIT), false) + "<img MAIN_GUI HONOR_ICON>, " 
					+ Util.getFlaggedText(String.valueOf((int)MARAUDE_RELATION_HIT), false) + " Relation\n" +
					"They gain: Revenge War Justification", 
					World.getInstance().getPlayerController().getHouse(), 
					other);
			mail.setIconName("War");
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
		
		if (other.getController() == World.getInstance().getPlayerController()) {
			HeaderedMail mail = new HeaderedMail(	
				"We have crushed marauding army!", 
				"A marauding army that had infested our\n" +
				"lands has finally fallen. Torture revealed that\n" +
				"the " + owner.getLinkedName() + " were behind this.\n\n" +
				"We gain: Revenge War Justification", 
				World.getInstance().getPlayerController().getHouse(), 
				other
			);
			mail.setIconName("War");
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
		
		other.addStatModifier(new VaryingStatModifier(
			"Marauding", 
			House.RELATION_STAT,
			other, 
			owner,  
			-1, 
			MARAUDE_RELATION_HIT,
			MARAUDE_RELATION_RECOVERY,
			0
		));
		
		other.addStatModifier(new VaryingStatModifier(
			"Marauding", 
			House.RELATION_STAT,
			owner, 
			other,  
			-1, 
			MARAUDE_RELATION_HIT,
			MARAUDE_RELATION_RECOVERY,
			0
		));
		
		destroyMarauder();
		other.updateBorders();
		owner.changeHonor(MARAUDER_LOSS_HONOR_HIT);
	}
	
	public void destroy() {
		
		if (raised) {
		
			World.getInstance().removeRaisedArmy(this);
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (World.getInstance().getMainGUI().pickerGUI.pickedArmy == this) {
					World.getInstance().getMainGUI().pickerGUI.pickedArmy = null;
					deselect();
					World.getInstance().getMainGUI().pickerGUI.hideAll();
				}
			}
			
			if (current != null) {
				current.removePositionedArmy(this);
			}
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				checkDestroyHeader();
				SceneManager.getInstance().destroyRenderable(entity);
			}
			
			node.detach();
			GameUpdater.getUpdater().removeItem(system);
		
		}
		
		endTravel();
		endPillage();
		
		if (isFighting() && EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			battle.win(battle.getOther(this), this);
		}
		
		raised = false;
		travelling = false;
		current = null;
		setReinforcementEnabled(true);
		
		if (infoNode != null) {
			infoNode.detach();
			infoNode = null;
		}
	}
	
	public Holding getCurrentHolding() {
		return current;
	}
	
	public boolean isTravelling() {
		return travelling;
	}
	
	public void switchRegiments(int index1, int index2) {
		Regiment r1 = regiments[index1];
		Regiment r2 = regiments[index2];
		
		regiments[index1] = r2;
		regiments[index2] = r1; 
		
		if (r1 != null) {
			r1.formationPos = index2;
		}
		
		if (r2 != null) {
			r2.formationPos = index1;
		}
	}
	
	public void switchRegiments(int index1, Army other, int index2) {
		Regiment r1 = regiments[index1];
		Regiment r2 = other.regiments[index2];
		
		regiments[index1] = r2;
		other.regiments[index2] = r1; 
		
		if (r1 != null) {
			r1.formationPos = index2;
		}
		
		if (r2 != null) {
			r2.formationPos = index1;
		}
	}
	
	public int getMoveTarget() {
		if (path.size() > 0) {
			return path.get(path.size()-1);
		} else if (nextTarget != null){
			return nextTarget;
		} else {
			return -1;
		}
	}
	
	
	public void setReinforcementEnabled(boolean enabled) {
		for (int i = 0; i < MAX_REGIMENTS; ++i) {
			if (regiments[i] != null) {
				regiments[i].reinforcementEnabled = enabled;
			}
		}
	}
	
	public void pillage() {
		if (pillageProgress == -1 && !travelling && !isFighting()) {
			
			pillageProgress = 0;
			if (!SceneManager.getInstance().getRenderer().isInServerMode() && current.holdingData.barony.isExplored()) {
				pillageSystem = (ComplexParticleSystem) SceneManager.getInstance().createParticleSystem(1);
				GameUpdater.getUpdater().addItem(pillageSystem);
				
				Particle bar = new Particle(1.8f);
				bar.setLifeTime(0);
				
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("PROGRESS");
				brown = atlas.getRegion("BROWN");
				
				pillageRegion = new TextureRegion(brown);
				
				mat = new ParticleMaterial("PROGRESS", pillageRegion, new Color(1,1,1,1));
				mat.setBlending(true);
				bar.setMaterial(mat);
				bar.setVelocity(Vector3.ORIGIN);
				
				bar.setPos(particle.getPos().add(new Vector3(0,0,0.4f)));
				
				pillageSystem.addParticle(bar);
				
				infoNode.attachSceneObject(pillageSystem);
			
			}
			
			if (current.getOwner().getSupremeOverlord() == this.getOwner()) {
				pillageFactor = PILLAGE_RULER_FACTOR;
			} else {
				pillageFactor = 1;
			}
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	id, 
																						LevyAction.PILLAGE,
																						(short) 0));	
			}
			
		} else {
			endPillage();
		}
	}
	
	public boolean isPillaging() {
		return pillageProgress != -1;
	}
	
	public void endPillage() {
		pillageProgress = -1;
		SceneManager.getInstance().destroyRenderable(pillageSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
		GameUpdater.getUpdater().removeItem(pillageSystem);
	}
	
	public float getAtkMult() {
		if (home != null) {
			return home.holdingData.atkMult;
		} else {
			return 1;
		}
	}
	
	public float getDefMult() {
		if (home != null) {
			return home.holdingData.defMult;
		} else {
			return 1;
		}
	}
	
	public float getTypeMult(int index) {
		if (home != null) {
			return home.holdingData.typeMult[index];
		} else {
			return 1;
		}
	}

	public void addReinforcements(float reinforcements, int category) {
		this.reinforcements += reinforcements;
		reinf[category] += reinforcements;
	}

	public void resetReinforcements() {
		reinforcements = 0;
		
		for (int i = 0; i < reinf.length; ++i) {
			reinf[i] = 0;
		}
	}

	public int getEstimatedTravelTime() {
		if (nextTarget != null) {
			float factor = 1;
			Holding h = World.getInstance().getHolding(nextTarget);
			if (h != null) {
				if (!h.holdingData.barony.isExplored()) {
					factor += WorldMap.UNEXPLORED_FACTOR;
				}
				
				if (current.isRoadBuilt(h)) {
					factor *= 0.5f;
				}
			} else {
				return 0;
			}
			
			float distance = World.getInstance().getMap().getDistance(current.getHoldingID(), nextTarget);
			return (int)((distance-travelled) / (World.getInstance().getTravelFactor() * SPEED * owner.stats[House.ARMY_SPEED_MULT] / factor));
		}
		
		return 0;
	}

	public String getReinforcementTooltip() {
		
		String text = 	"Reinforcements\n" +
						"-----------------";
		
		if (reinf[0] > 0) {
			text += "\nVolunteers " +  Util.getFlaggedText(Util.getSignedText((int)reinf[0]), true);
		}
		
		if (reinf[1] > 0) {
			text += " \nConscription " +  Util.getFlaggedText(Util.getSignedText((int)reinf[1]), true);
		}
		
		if (reinf[2] > 0) {
			text += "\nMercenaries " +  Util.getFlaggedText(Util.getSignedText((int)reinf[2]), true);
		}
		
		if (reinf[3] > 0) {
			text += "\nVassals " +  Util.getFlaggedText(Util.getSignedText((int)reinf[3]), true);
		}
		
		if (desertionRate > 0) {
			text += "\nDeserters " +  Util.getFlaggedText(String.valueOf((int)(-desertionRate*World.SECONDS_PER_DAY*World.DAYS_PER_SEASON*totalTroops)), false);
		}
		
		if (reinf[0] == 0 && reinf[1] == 0 && reinf[2] == 0 && reinf[3] == 0 && desertionRate == 0) {
			text += "\nNone required.";
		}
		
		return text;
	}
	
	public static final float MIN_SCALE = 0.038f;
	public static final float MAX_SCALE = 0.2f;
	
	
	public void updateHeader(Holding holding) {
		if (headerVisible) {
			
			float scale = MIN_SCALE;
			
			float distance = SceneManager.getInstance().getActiveCamera().getParent().getRelativePos().z;
			if (distance < CameraController.STRATEGIC_VIEW_HEIGHT) {
				scale = scale / (1.0f - distance / CameraController.STRATEGIC_VIEW_HEIGHT);
				scale = Math.max(scale, MIN_SCALE);
				scale = Math.min(scale, MAX_SCALE);
			} else {
				scale = MAX_SCALE;
			}
			
			if (this.isRaised()) {
				
				getTroopText().setScale(scale);
				
				Vector3 basePos = holding.getArmyBasePos();
				Vector3 offset = holding.getArmyOffset();
				
				Vector3 particlePos = basePos.add(offset.multiply(-Army.SCALE*1.8f));
				Vector3 ownerPos = basePos.add(offset.multiply(-Army.SCALE*1.8f));
				Vector3 textPos = basePos.add(offset.multiply(-Army.SCALE*1.9f));
				Vector3.add(ownerPos, 0.2f+15*scale,0.2f+15*scale,0.1f, ownerPos);
				Vector3.add(textPos, -0.5f+1*scale,-10f*scale,0.2f, textPos);
				
				if (!this.isFighting() || battle.getDefender() == this) {
					
					if (this.isFighting()) {
						Vector3.add(particlePos, -16*scale,0,0, particlePos);
						Vector3.add(ownerPos, -16*scale,0,0, ownerPos);
						Vector3.add(textPos, -16*scale,0,0, textPos);
					}
					
					if (basePos != null) {
						if (particle != null) {
							particle.setPos(particlePos);
						}
						
						if (ownerParticle != null) {
							ownerParticle.setPos(ownerPos);
						}
						
						if (text != null && text.getParent() != null) {
							text.getParent().setRelativePos(textPos);
						}
					}
				} else {
					
					Vector3.add(particlePos, 16*scale,0,0, particlePos);
					Vector3.add(ownerPos, 16*scale,0,0, ownerPos);
					Vector3.add(textPos, 16*scale,0,0, textPos);
					
					text.getParent().setRelativePos(textPos.add(BATTLE_OFFSET));
					particle.setPos(particlePos.add(BATTLE_OFFSET));
					ownerParticle.setPos(ownerPos.add(BATTLE_OFFSET));
				}
				
				if (current != null && current.getOwnerSystem() != null) {
					system.setInsertionID(current.getOwnerSystem().getInsertionID()+1);
				}
			} else {
				getTroopText().setScale(scale*0.9f);
				
				Vector3 basePos = home.holdingData.worldEntity.getParent().getCachedAbsolutePos();
				Vector3 offset = home.holdingData.worldEntity.getBoundingBox().max.sub(home.holdingData.worldEntity.getBoundingBox().min);
				offset.z *= 1.5f;
				offset.x *= 0.55f;
				offset.y *= 0.2f;
				basePos = basePos.add(offset).add(Barony.OFFSET_PROGRESS);
				
				Vector3.add(basePos, -18*scale,0,0, basePos);
				
				particle.setPos(basePos);
				if (text != null && text.getParent() != null) {
					Vector3 textPos = basePos.add(offset.multiply(-Army.SCALE*scale));
					Vector3.add(textPos, -0.3f+1*scale,-9f*scale,0.2f, textPos);
					text.getParent().setRelativePos(textPos);
				}
			}
			
			updateTroopTextRot();
		}
	}
	
	public void updateHeader() {
		if (current != null) {
			updateHeader(current);
		} else if (home != null){
			updateHeader(home);
		}
	}

	public void hide() {
		entity.setVisible(false);
		checkDestroyHeader();
		for (int i = 0; i < arrowPaths.size(); ++i) {
			arrowPaths.get(i).setVisible(false);
		}
	}

	public boolean canUnraise() {
		return 		getCurrentHolding() == getHome() 
				|| 	(getOwner().isMarauder()  && raisedTime >= MARAUDERS_RAISED_MIN);
	}
	
	public void destroyMarauder() {
		owner.armies.clear();
		owner.getController().removeSubFaction(owner);
		
		World.getInstance().removeHouse(owner);
		for (short i = 0; i < World.getInstance().getHouses().size(); ++i) {
			World.getInstance().getHouses().get(i).id = i;
		}
		
		setOwner(owner.getController().getHouse());
		owner.getController().getHouse().updateBorders();
	}
}
