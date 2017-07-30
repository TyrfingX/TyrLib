package com.tyrfing.games.id17.war;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.mails.BattleMail;
import com.tyrfing.games.id17.gui.war.BattleGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.networking.BattleResult;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

public class Battle implements IUpdateable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1139477509538299276L;
	private Army attacker;
	private Army defender;
	
	private BattlePhase phase = BattlePhase.BATTLE_CHARGE;
	
	public static final float PHASE_DURATION = 5f;
	public static final float PHASE_PREP = 0.2f;
	public static final float PHASE_RET = 0.2f;
	public static final float SIEGE_PREP = 20f;
	
	private boolean inPosition = false;
	private boolean retreating = false;
	
	private float phaseTime = 0;
	
	private transient BattleGUI gui;
	
	private List<Skirmish> skirmishs = new ArrayList<Skirmish>();
	
	private boolean finished = false;
	
	private Holding place;
	
	private Quaternion attackQuat;
	private Quaternion defQuat;
	
	private War war;
	private int attackerTroops;
	private int defenderTroops;
	
	private int[] regimentTroopsAttacker = new int[4];
	private int[] regimentTroopsDefender = new int[4];
	
	private int attackerTotalTroops;
	private int defenderTotalTroops;
	
	private float warProgress;
	
	private Army winner;
	private Army looser;
	
	private boolean siege;
	
	private float duration;
	
	public Battle(Army attacker, Army defender, Holding place, War war) {
		this.attacker = attacker;
		this.defender = defender;
		this.place = place;
		this.war = war;
		
		attacker.setBattle(this);
		defender.setBattle(this);
		
		attackerTroops = attacker.getTotalTroops();
		defenderTroops = defender.getTotalTroops();
		
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Regiment rA = attacker.getRegiment(i);
			Regiment rD = defender.getRegiment(i);
			
			if (rA == null) {
				regimentTroopsAttacker[i] = 0;
			} else {
				regimentTroopsAttacker[i] = (int) rA.troops;
			}
			
			if (rD == null) {
				regimentTroopsDefender[i] = 0;
			} else {
				regimentTroopsDefender[i] = (int) rD.troops;
			}
		}
		
		attackerTotalTroops = attacker.getOwner().getTotalTroops();
		defenderTotalTroops = defender.getOwner().getTotalTroops();
		
		if (attacker.getEntity() != null &&  attacker.getEntity().getParent() != null) {
			attackQuat = attacker.getEntity().getParent().getRelativeRot();
			attacker.setRotation(Quaternion.fromAxisAngle(new Vector3(0,0,1), -45));
			attacker.positionAsAttacker();
		}
		
		if (defender.getEntity() != null && defender.getEntity().getParent() != null) {
			defQuat = defender.getEntity().getParent().getRelativeRot();
			defender.setRotation(Quaternion.fromAxisAngle(new Vector3(0,0,1),  135));
			defender.positionAsDefender();
		}
		
		World.getInstance().getUpdater().addItem(this);
		
		attacker.setReinforcementEnabled(false);
		defender.setReinforcementEnabled(false);
		
		war.battles.add(this);
	}
	
	public void makeSiege() {
		siege = true;
		phase = BattlePhase.SIEGE_PREPARE;
	}
	
	public boolean isSiege() {
		return siege;
	}
	
	public Holding getPlace() {
		return place;
	}

	@Override
	public void onUpdate(float time) {
		
		if (finished) return;
		
		phaseTime += time * World.getInstance().getPlaySpeed();
		duration += time * World.getInstance().getPlaySpeed();
		
		if (gui != null) {
			gui.displayPhase();
		}
		
		if (!inPosition) {
			if (phaseTime >= PHASE_PREP) {
				inPosition = true;
				phaseTime = 0;
			}
		} else {
			if (retreating) {
				if (phaseTime >= PHASE_RET) {
					phaseTime = 0;
					retreating = false;
					inPosition = false;
					
					moveUp(attacker);
					moveUp(defender);
					
					if (attacker.moral <= 0 || attacker.getTotalTroops() <= 0) {
						if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
							win(defender, attacker);
						}
					} else if (defender.moral <= 0 || defender.getTotalTroops() <= 0){
						if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
							win(attacker, defender);
						}
					} else {
					
						nextPhase();
						
						if (phase.isRangedPhase()) {
							inPosition = true;
						}
						
						if ((skirmishs.size() == 0 && phase != BattlePhase.SIEGE_PREPARE) || (phaseTime >= SIEGE_PREP && phase == BattlePhase.SIEGE_PREPARE)) {
							retreating = true;
							inPosition = true;
						}
					
					}
				}
			} else {
				if ((phaseTime >= PHASE_DURATION && phase != BattlePhase.SIEGE_PREPARE) || (phaseTime >= SIEGE_PREP && phase == BattlePhase.SIEGE_PREPARE)){
					inPosition = false;
					retreating = true;
					phaseTime = 0;
					
					if (gui != null){
						gui.displayRegimentRetreat(skirmishs);
					}
				} else {
					for (int i = 0; i < skirmishs.size(); ++i) {
						Skirmish skirmish = skirmishs.get(i);
						skirmish.calcDmg(time * World.getInstance().getPlaySpeed() / 8);
					}
				}
			}
		}
		
	}
	
	private void moveUp(Army army) {
		for (int i = 0; i < 2; ++i) {
			Regiment r = army.getRegiment(i);
			if (r == null || r.troops <= 0) {
				Regiment r2 = army.getRegiment(i+2);
				if (r2 != null && r2.troops > 0) {
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						army.switchRegiments(i, i+2);
						short param = (short) (i | ((i+2) << 8));
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction( army.id, 
																							  LevyAction.CHANGE_FORMATION, 
																							  param));
					} 
				}
			}
		}
		
		if (gui != null) {
			gui.updateFormation();
		}
	}
	
	private void nextPhase() {
		phase = phase.getNext();
		skirmishs.clear();
		
		// Make the regiment battle pairings
		if (phase == BattlePhase.BATTLE_BATTLE_1) {
			makeMeleeSkirmishs(attacker, defender);
		} else if (phase == BattlePhase.BATTLE_BATTLE_2) {
			makeMeleeSkirmishs(defender, attacker);
		} else if (phase == BattlePhase.BATTLE_VOLLEY_1) {
			makeRangedSkirmishs(attacker, defender);
		} else if (phase == BattlePhase.BATTLE_VOLLEY_2) {
			makeRangedSkirmishs(defender, attacker);
		}else if (phase == BattlePhase.BATTLE_FLANK_1) {
			makeFlankSkirmishs(attacker, defender);
		} else if (phase == BattlePhase.BATTLE_FLANK_2) {
			makeFlankSkirmishs(defender, attacker);
		} else if (phase == BattlePhase.SIEGE_ASSAULT) {
			makeMeleeSkirmishs(attacker, defender);
		} else if (phase == BattlePhase.SIEGE_PREPARE) {
			
		} else if (phase == BattlePhase.SIEGE_VOLLEY_1) {
			makeRangedSkirmishs(attacker, defender);
		} else if (phase == BattlePhase.SIEGE_VOLLEY_2) {
			makeRangedSkirmishs(defender, attacker);
		}
	}
	
	private void makeMeleeSkirmishs(Army attacker, Army defender) {
		List<Regiment> attackSupport = new ArrayList<Regiment>();
		Regiment lastTarget = null;
		
		for (int i = 0; i < 2; ++i) {
			Regiment regiment1 = attacker.getRegiment(i);
			
			int j = i;
			
			if (siege) {
				j += 2;
			}
			
			Regiment regiment2 = defender.getRegiment(j);
			
			Skirmish skirmish = new Skirmish();
			skirmish.battle = this;
			skirmish.attackerArmy = attacker;
			skirmish.defenderArmy = defender;
			
			if (attackSupport != null) {
				skirmish.attackers.addAll(attackSupport);	
			}
			
			if (regiment1 != null && regiment1.troops > 0 && regiment1.unitType.isMeleeAttacker()) {
				if (!siege || regiment1.unitType != UnitType.Cavalry) {
					skirmish.attackers.add(regiment1);
				}
			} 
				
			if (regiment2 != null && regiment2.troops > 0) {
				lastTarget = regiment2;
				
				skirmish.defenders.add(regiment2);
				
				if (j % 2 - 1 >= 0) {
					Regiment def = defender.getRegiment(j-1);
					if (def != null && def.troops > 0 && def.unitType.isGuarding()) {
						skirmish.defenders.add(defender.getRegiment(j-1));
					}
				}
				
				if (j % 2 < 1) {
					Regiment def = defender.getRegiment(j+1);
					if (def != null && def.troops > 0 && def.unitType.isGuarding()) {
						skirmish.defenders.add(defender.getRegiment(j+1));
					}
				}
				
				if (j - 2 >= 0) {
					Regiment def = defender.getRegiment(j-2);
					if (def != null && def.troops > 0 && def.unitType.isGuarding()) {
						skirmish.defenders.add(defender.getRegiment(j-2));
					}
				}
				
				if (j+2 < Army.MAX_REGIMENTS) {
					Regiment def = defender.getRegiment(j+2);
					if (def != null && def.troops > 0 && (def.unitType.isGuarding() || def.unitType == UnitType.Pikemen)) {
						skirmish.defenders.add(defender.getRegiment(j+2));
					}
				}
				
			} else if (regiment1 != null && regiment1.troops > 0 && regiment1.unitType != UnitType.Archers) {
				if (lastTarget == null) {
					attackSupport.add(regiment1);
				} else {
					skirmish.defenders.add(lastTarget);
				}
			}
			
			if (skirmish.attackers.size() > 0 && skirmish.defenders.size() > 0){
				skirmishs.add(skirmish);
				attackSupport.clear();
			}
		}
		
		if (gui != null) {
			gui.displaySkirmishs(skirmishs);
		}
	}

	private void makeRangedSkirmishs(Army attacker, Army defender) {
		for (int i = 0; i < 4; ++i) {
			Regiment regiment1 = attacker.getRegiment(i);
			
			if (regiment1 != null && regiment1.unitType == UnitType.Archers && regiment1.troops > 0 ) {
				
				for (int j = 0; j < 2; ++j) {
					
					Skirmish skirmish = new Skirmish();
					skirmish.battle = this;
					skirmish.counterDamage = false;
					skirmish.attackerArmy = attacker;
					skirmish.defenderArmy = defender;
					
					skirmish.attackers.add(regiment1);
					
					Regiment regiment2 = defender.getRegiment(j);
					Regiment regiment3 = defender.getRegiment(j + 2);
					
					if (regiment2 != null  && regiment2.troops > 0 && (skirmish.defenders.size() == 0 || regiment2.unitType == UnitType.Guardians)) {
						skirmish.defenders.add(regiment2);
					}
					
					if (regiment3 != null && regiment3.troops > 0 && (regiment2 == null || regiment2.troops <= 0 || regiment2.unitType != UnitType.Guardians)) {
						skirmish.defenders.add(regiment3);
					}
					
					if (j % 2 - 1>= 0) {
						Regiment def = defender.getRegiment(j-1);
						if (def != null && def.troops > 0 && def.unitType.isGuarding()) {
							skirmish.defenders.add(def);
						}
					}
					
					if (j % 2 < 1) {
						Regiment def = defender.getRegiment(j+1);
						if (def != null && def.troops > 0 && def.unitType.isGuarding()) {
							skirmish.defenders.add(def);
						}
					}
					
					if (skirmish.defenders.size() > 0) {
						skirmishs.add(skirmish);
					}
					
				}
				
			}
		}
		
		if (gui != null) {
			gui.highlightAttackers(skirmishs);
		}
	}
	
	private void makeFlankSkirmishs(Army attacker, Army defender) {
		
		if (siege) return;
		
		for (int i = 0; i < 4; ++i) {
			
			Regiment regiment1 = attacker.getRegiment(i);
			
			if (regiment1 != null && regiment1.unitType == UnitType.Cavalry && regiment1.troops > 0 ) {
					
				int j;
				
				if (i < 2) {
					j = i + 2;
				} else {
					j = i - 2;
				}
				
				Skirmish skirmish = new Skirmish();
				skirmish.battle = this;
				skirmish.counterDamage = true;
				skirmish.attackerArmy = attacker;
				skirmish.defenderArmy = defender;
				
				skirmish.attackers.add(regiment1);
				
				Regiment regiment2 = defender.getRegiment(j);
				
				
				if (regiment2 != null  && regiment2.troops > 0 && (skirmish.defenders.size() == 0 || regiment2.unitType == UnitType.Guardians)) {
					skirmish.defenders.add(regiment2);
				}
				
				if (j < 2) {
					Regiment regiment3 = defender.getRegiment(j + 2);
					if (regiment3 != null && regiment3.troops > 0 && regiment2 != null && regiment2.troops > 0 && (regiment3.unitType == UnitType.Guardians  || regiment3.unitType == UnitType.Pikemen)) {
						skirmish.defenders.add(regiment3);
					}
				}
				
				if (j % 2 - 1 >= 0) {
					Regiment def = defender.getRegiment(j-1);
					if (def != null && def.troops > 0 && def.unitType == UnitType.Guardians) {
						skirmish.defenders.add(def);
					}
				}
				
				if (j % 2 < 1) {
					Regiment def = defender.getRegiment(j+1);
					if (def != null && def.troops > 0 && def.unitType == UnitType.Guardians) {
						skirmish.defenders.add(def);
					}
				}
				
				if (skirmish.defenders.size() > 0) {
					skirmishs.add(skirmish);
				}
				
			}
			
		}
		
		if (gui != null) {
			gui.displayFlanking(skirmishs);
		}
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public Army getDefender() {
		return defender;
	}

	public List<Skirmish> getSkirmishes() {
		return skirmishs;
	}
	
	public void setView(BattleGUI gui) {
		this.gui = gui;
	}
	
	public BattlePhase getPhase() {
		return phase;
	}
	
	public void win(Army winner, Army looser) {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new BattleResult(winner.id));
		}
		
		end();
		
		this.winner = winner;
		this.looser = looser;
		
		int attackerLosses = attackerTroops - attacker.getTotalTroops();
		int defenderLosses = defenderTroops - defender.getTotalTroops();
		
		float attackerWarDamage = (float)attackerLosses / (attackerTotalTroops+1);
		float defenderWarDamage = (float)defenderLosses / (defenderTotalTroops+1);
		
		warProgress = (defenderWarDamage -  attackerWarDamage) / 2;
		
		if (siege && winner == attacker) {
			warProgress += 1 / looser.getOwner().getBaronies().size();
		}
		
		if (war.attackers.contains(attacker.getOwner())) {
			war.changeProgress(warProgress, winner.getOwner());
			war.battleProgress += warProgress;
		} else {
			war.changeProgress(-warProgress, winner.getOwner());
			war.battleProgress -= warProgress;
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (winner.getOwner().getController() == World.getInstance().getPlayerController() || looser.getOwner().getController() == World.getInstance().getPlayerController()) {
				if (gui != null) {
					gui.displayResult(winner);
				}
				
				BattleMail mail = new BattleMail("Battle of " + winner.getCurrentHolding().getLinkedName() + " " + World.getInstance().getDate(), this);
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
		
		if (siege && winner == attacker) {
			if (!attacker.getOwner().isMarauder()) {
				((Barony)place).setOccupied(winner.getOwner());
				war.addOccupied((Barony)place);
			} else {
				attacker.applyPillage();
			}
		}
		
		if (!siege || looser == attacker) {
			if(looser.getTotalTroops() > 0) {
				looser.randomRetreat();
			} else {
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					looser.kill();
				} else {
					looser.getCurrentHolding().removePositionedArmy(looser);
				}
			}
		}
		
		if (!siege || winner == attacker) {
			winner.arrive();
		}
		

		
	}
	
	public Army getWinner() {
		return winner;
	}
	
	public Army getLooser() {
		return looser;
	}
	
	public float getWarProgress() {
		return warProgress;
	}
	
	public void end() {
		finished = true;
		
		attacker.setBattle(null);
		defender.setBattle(null);
		
		if (attacker.getEntity() != null && attacker.getEntity().getParent() != null && attackQuat != null) {
			attacker.getEntity().getParent().setRelativeRot(attackQuat);
		}
		
		if (defender.getEntity() != null && defender.getEntity().getParent() != null && defQuat != null) {
			defender.getEntity().getParent().setRelativeRot(defQuat);
		}
		
		if (!defender.isRaised()) {
			defender.setBesieged(false);
		}
		
		attacker.setReinforcementEnabled(true);
		defender.setReinforcementEnabled(true);
		
		war.battles.remove(this);
	}
	
	public int getLosses(Army army, int regiment) {
		int[] troopCount = regimentTroopsAttacker;
		
		if (army == defender) {
			troopCount = regimentTroopsDefender;
		}
		
		Regiment r = army.getRegiment(regiment);
	
		if (r == null) return 0;
		
		return troopCount[regiment] - (int) r.troops;
	
	}
	
	public int getLosses(Army army) {
		if (army == attacker) {
			return attackerTroops - attacker.getTotalTroops(); 
		} else {
			return defenderTroops - defender.getTotalTroops();
		}
	}
	
	public Army getOther(Army army) {
		if (attacker == army) {
			return defender;
		} else {
			return attacker;
		}
	}
	
	public float getDuration() {
		return duration;
	}
}
