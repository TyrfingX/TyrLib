package com.tyrfing.games.id17.war;

import gnu.trove.map.hash.TObjectFloatHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.HonorDefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.HonorProtect;
import com.tyrfing.games.id17.gui.war.WarGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.networking.EndWar;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.networking.NewWar;
import com.tyrfing.games.id17.world.Border.Status;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneManager;

public class War implements IUpdateable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3277168085217847679L;
	
	public static final float OCCU_PROGRESS = 0.006f / (World.SECONDS_PER_DAY * World.DAYS_PER_YEAR);
	public static final float MAIN_TARGET_BONUS = 10;
	
	public static final int RELATION_AT_WAR = -100;
	
	public static final float TRUCE_DURATION = 3 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public float totalProgress;
	public float occupyProgress;
	public float battleProgress;
	public float pillageProgress;
	
	protected List<Battle> battles = new ArrayList<Battle>();
	//private List<WarContribution> contributions = new ArrayList<WarContribution>();
	public House attacker;
	public House defender;
	public List<House> attackers = new ArrayList<House>();
	public List<House> defenders = new ArrayList<House>();
	public List<Barony> occupied = new ArrayList<Barony>();
	
	private transient WarGUI warGUI;
	
	public final WarGoal goal;
	public final WarJustification justification;
	
	private float startTime;
	
	private int playerSide = 0;
	
	private TObjectFloatHashMap<House> warContributions = new TObjectFloatHashMap<House>();
	private float totalContributionAttacker = 0;
	private float totalContributionDefender = 0;
	
	public static final int MAX_FAVOR = 80;
	
	public War(House attacker, House defender, WarGoal goal, WarJustification justification) {
		
		this.attacker = attacker;
		this.defender = defender;
		
		this.addAttackerAlly(attacker);
		this.addDefenderAlly(defender);
		
		this.goal = goal;
		this.justification = justification;
		
		startTime = World.getInstance().getWorldTime();
		
		if (attacker != defender){
			inviteDefenders();
			World.getInstance().getUpdater().addItem(this);
		} else {
			System.out.println("Invalid declaration of War: " +  attacker.getName() + " to " +  defender.getName());
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new NewWar(this));
		}
	}
	
	public void inviteDefenders() {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				House house = World.getInstance().getHouses().get(i);
				if (house.isIndependend()) {
					if (house.getHouseStat(defender, House.HAS_DEFENSIVE_PACT) == 1) {
						Message m = new Message(new HonorDefensivePact(), defender, house, new int[] { attacker.id });
						m.action.send(m.sender, m.receiver, m.options);
					} else if (house.isProtector(defender)) {
						Message m = new Message(new HonorProtect(), defender, house, new int[] { attacker.id });
						m.action.send(m.sender, m.receiver, m.options);
					}
				}
			}
		}
	}
	
	public void addAttackerAlly(House house) {
		if (!attackers.contains(house)) {
			attackers.add(house);
			
			if (playerSide == 2) {
				useEnemyBorders(house);
			}
			
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				House h = World.getInstance().getHouses().get(i);
				if (h.isSubjectOf(house)) {
					if (!attackers.contains(h)) {
						attackers.add(h);
						h.addWar(this);
					}
				}
			}
			
			
			for (int i = 0; i < defenders.size(); ++i) {
				house.makeEnemy(defenders.get(i));
			}
			
			house.addWar(this);
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (World.getInstance().getPlayerController().isSubjectOf(house)) {
					for (int i = 0; i < defenders.size(); ++i) {
						if (defenders.get(i).getController() != World.getInstance().getPlayerController()) {
							useEnemyBorders(defenders.get(i));
						}
					}
					
					createGUI();
					
					playerSide = 1;
				}
			}
		};
	}
	
	public void addDefenderAlly(House house) {
		if (!defenders.contains(house)) {
			defenders.add(house);
			
			if (playerSide == 1) {
				useEnemyBorders(house);
			}
			
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				House h = World.getInstance().getHouses().get(i);
				if (h.isSubjectOf(house)) {
					if (!defenders.contains(h)) {
						defenders.add(h);
						h.addWar(this);
					}
				}
			}
			
			for (int i = 0; i < attackers.size(); ++i) {
				house.makeEnemy(attackers.get(i));
			}
			
			house.addWar(this);
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (World.getInstance().getPlayerController().isSubjectOf(house)) {
					for (int i = 0; i < attackers.size(); ++i) {
						if (attackers.get(i).getController() != World.getInstance().getPlayerController()) {
							useEnemyBorders(attackers.get(i));
						}
					}
					
					createGUI();
					
					playerSide = 2;
				}
			}
		}
	}
	
	public void createGUI() {
		House enemy = attackers.contains(World.getInstance().getPlayerController().getHouse()) ?
						defender : attacker;
		warGUI = new WarGUI(this, World.getInstance().getPlayerController().getHouse(), 
								  enemy);
	}
	
	public void destroyGUI() {
		warGUI.destroy();
	}
	
	private void useEnemyBorders(House enemy) {
		List<Barony> baronies = enemy.getBaronies();
		for (int i = 0; i < baronies.size(); ++i) {
			baronies.get(i).getWorldChunk().getBorder().setStatus(Status.ENEMY);
		}
		
		for (int i = 0; i < enemy.getSubHouses().size(); ++i) {
			useEnemyBorders(enemy.getSubHouses().get(i));
		}
	}
	
	public boolean areEnemies(House house1, House house2) {
		if (attackers.contains(house1) && defenders.contains(house2)) return true;
		if (attackers.contains(house2) && defenders.contains(house1)) return true;
		
		return false;
	}
	
	public float getProgress() {
		return totalProgress;
	}
	
	public float getProgress(House house) {
		return defenders.contains(house) ? -totalProgress : totalProgress;
	}
	
	public void changeProgress(float progress, House contributor) {
		
		if (attackers.contains(contributor)) {
			totalContributionAttacker += Math.abs(progress);
		} else {
			totalContributionDefender += Math.abs(progress);
		}
		
		warContributions.put(contributor, warContributions.get(contributor) +  Math.abs(progress));
		
		totalProgress = Math.max(-1, Math.min(1, totalProgress + progress));
		
		if (warGUI != null) {
			warGUI.updateProgress();
		}
	}
	
	public float getWarContribution(House house) {
		if (attackers.contains(house)) {
			if (totalContributionAttacker != 0) {
				return warContributions.get(house) / totalContributionAttacker;
			} else {
				return 0;
			}
		} else {
			if (totalContributionDefender != 0) {
				return warContributions.get(house) / totalContributionDefender;
			} else {
				return 0;
			}
		}
	}
	
	public void end() {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()){
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new EndWar(defender.wars.indexOf(this), defender.id));
		
		
			for (int i = 0; i < defenders.size(); ++i) {
				for (int j = 0; j < attackers.size(); ++j) {
					defenders.get(i).removeStatModfifier("At War", attackers.get(j));
					attackers.get(j).removeStatModfifier("At War", defenders.get(i));
				}
			}
		
		}
		
		if (warGUI != null) {
			warGUI.destroy();
		}
		
		for (int i = 0; i < attackers.size(); ++i) {
			attackers.get(i).removeWar(this);
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (attackers.get(i) == World.getInstance().getPlayerController().getHouse().getSupremeOverlord()) {
					for (int j = 0; j < defenders.size(); ++j) {
						if (World.getInstance().getPlayerController().isEnemy(defenders.get(j)) == null) {
							defenders.get(j).updateBorders();
						}
					}
				}
			}
		}
		
		for (int i = 0; i < defenders.size(); ++i) {
			defenders.get(i).removeWar(this);
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (defenders.get(i) == World.getInstance().getPlayerController().getHouse().getSupremeOverlord()) {
					for (int j = 0; j < attackers.size(); ++j) {
						if (World.getInstance().getPlayerController().isEnemy(attackers.get(j)) == null) {
							attackers.get(j).updateBorders();
						}
					}
				}
			}
			
		}

		for (int i = 0; i < occupied.size(); ++i) {
			occupied.get(i).setOccupied(null);
		}
		
		while(battles.size() > 0) {
			battles.get(0).end();
		}
		
		for (int i = 0; i < attackers.size(); ++i) {
			House h = attackers.get(i);
			for (int j = 0; j < h.getBaronies().size(); ++j) {
				Barony b = h.getBaronies().get(j);
				Army levy = b.getLevy();
				if (levy != null && levy.getCurrentHolding() != null) {
					if (levy.getCurrentHolding().isPillageableByArmy(levy) && levy.isPillaging()) {
						levy.endPillage();
					}
				}
			}
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (goal.warMode == WarGoal.REVOLT) {
				if (attacker.armies.size() > 0) {
					if (attacker.armies.get(0) < World.getInstance().armies.size() && !((RebelArmy)World.getInstance().getArmy(attacker.armies.get(0))).destroyed) {
						
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(attacker.armies.get(0), LevyAction.DESTROY, (short) 0));
						
						World.getInstance().getArmy(attacker.armies.get(0)).destroy();
					}
				}
			}
			
			if (justification != null) {
				attackers.get(0).removeJustification(justification);
			}
		}
		
		World.getInstance().getUpdater().removeItem(this);
	}
	
	public WarGUI getGUI() {
		return warGUI;
	}
	
	public void win(House victor) {
		end();
		
		House looser = attacker;
		if (victor == attacker) {
			looser = defender;
			goal.enact(victor, looser, this);
			
			for (int i = 0; i < attackers.size(); ++i) {
				float contrib = MAX_FAVOR*getWarContribution(attackers.get(i));
				attackers.get(i).changeHouseStat(attacker, House.FAVOR_STAT, contrib);
				attacker.changeHouseStat(attackers.get(i), House.FAVOR_STAT, -contrib);
			}
		} else {
			float reparations = getReparations(victor, looser);
			for (int i = 0; i < defenders.size(); ++i) {
				defenders.get(i).changeGold(reparations*getWarContribution(defenders.get(i)));
			}
			
			looser.changeGold(-reparations);
			
			if (goal.warMode == WarGoal.REVOLT) {
				goal.enact(victor, looser, this);
			} else if (goal.warMode == WarGoal.MARAUDE) {
				short armyID = attackers.get(0).armies.get(0);
				World.getInstance().getArmy(armyID).onMarauderWarLost(this);
			}
		}
		
		if (attacker.getHouseStat(defender, House.HAS_TRUCE) == 0) {
			attacker.addStatModifier(new StatModifier("Truce", House.HAS_TRUCE, attacker, defender, TRUCE_DURATION, 1));
		}
		
		if (defender.getHouseStat(attacker, House.HAS_TRUCE) == 0) {
			defender.addStatModifier(new StatModifier("Truce", House.HAS_TRUCE, defender, attacker, TRUCE_DURATION, 1));
		}
	}
	
	public void addOccupied(Barony barony) {
		occupied.add(barony);
		
		List<Barony> baronies = barony.getOwner().getBaronies();
		
		for (int i = 0; i < baronies.size(); ++i) {
			if (baronies.get(i).getOccupee() == null) {
				return;
			}
		}
		
		if (barony.getOwner() == attacker) {
			totalProgress = -1;
			
			if (warGUI != null) {
				warGUI.updateProgress();
			}
		} else if (barony.getOwner() == defender) {
			totalProgress = 1;
			
			if (warGUI != null) {
				warGUI.updateProgress();
			}
		}
	}
	
	public float getStartTime() {
		return startTime;
	}
	
	public float getDuration() {
		return World.getInstance().getWorldTime() - startTime;
	}
	
	
	public House getOther(House house) {
		if (house == attacker) {
			return defender;
		} else {
			return attacker;
		}
	}
	
	public static float getReparations(House winner, House looser) {
		return winner.getIncome() * 8 + looser.getIncome() * 8 + (looser.getGold() > 0 ? looser.getGold() * 0.25f : 0);
	}

	@Override
	public void onUpdate(float time) {
		
		if (this.getProgress() != 1 && this.getProgress() != -1) {
		
			float factor = time*World.getInstance().getPlaySpeed() * OCCU_PROGRESS;
			
			float progress = 0;
			
			for (int i = 0; i < this.occupied.size(); ++i) {
				float contribution = 1.0f / occupied.get(i).getOwner().getBaronies().size();
	
				if (defenders.contains(occupied.get(i).getOccupee())) {
					contribution *= -1;
				} else if (occupied.get(i).hasSubHolding(goal.goalHolding)) {
					contribution *= War.MAIN_TARGET_BONUS;
				}
				
				this.changeProgress(contribution*factor, this.occupied.get(i).getOccupee());
				progress += contribution;
			}
			
			if (goal.goalHolding != null && !occupied.contains(goal.goalHolding.holdingData.barony)) {
				this.changeProgress(-War.MAIN_TARGET_BONUS*factor, goal.goalHolding.holdingData.barony.getOwner());
				progress -= War.MAIN_TARGET_BONUS;
			}
	
			if (goal.warMode == WarGoal.REVOLT) {
				this.changeProgress(War.MAIN_TARGET_BONUS*factor, attacker);
				progress += War.MAIN_TARGET_BONUS;
			} else if (goal.warMode == WarGoal.MARAUDE) {
				this.changeProgress(-War.MAIN_TARGET_BONUS*factor, attacker);
				progress -= War.MAIN_TARGET_BONUS;
			}
			
			progress = progress * factor;
			this.occupyProgress += progress;
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
