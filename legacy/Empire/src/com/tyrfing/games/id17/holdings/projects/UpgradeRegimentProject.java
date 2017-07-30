package com.tyrfing.games.id17.holdings.projects;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.networking.ProjectCompleted;
import com.tyrfing.games.id17.networking.UpgradeRegimentMessage;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;

public class UpgradeRegimentProject implements IProject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2277775441796355883L;

	public static final int MAX_INCREASE_PER_UPGRADE = 100;
	public static final int WEALTHY_CONSTRUCTION = 6;
	public static final float WEALTHY_CONSTRUCTION_DECAY = -3f / World.DAYS_PER_SEASON;
	
	private float progress;
	private float prod;
	private Regiment regiment;
	private float cost;
	private Army army;
	
	private float progressLast;
	private float progressSpeedInterpolated;
	private float timeSinceLastUpdate;

	private boolean aborted;
	
	
	public UpgradeRegimentProject(float prod, Regiment regiment, Army army, float cost) {
		this.prod = prod;
		this.regiment = regiment;
		this.army = army;
		this.cost = cost;
		
		army.getOwner().changeGold((int) -cost);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast( getPacket()	);
		}  
	}
	
	@Override
	public void onUpdate(float time) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			progress += time * army.getHome().getHoldingData().prod * (army.getHome().getStats()[HoldingTypes.UNIT_PROD_FACTOR]+1);
		} else {
			timeSinceLastUpdate += time;
			progress += time * progressSpeedInterpolated;
		}
		
		if (progress >= prod) {
			progress = prod;
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				army.getHome().finishActiveProject();
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ProjectCompleted(army.getHome().getHoldingID()));
			}			
		}
	}

	@Override
	public boolean isFinished() {
		return progress >= prod || aborted;
	}

	@Override
	public float getProgress() {
		return progress / (prod*(army.getHome().getStats()[HoldingTypes.UNIT_PROD_FACTOR]+1));
	}

	@Override
	public void abort() {
		//army.getOwner().changeGold((int) cost);
		aborted = true;
	}

	@Override
	public String getIconAtlasName() {
		return "UNIT_ICONS";
	}

	@Override
	public String getIconRegionName() {
		return regiment.unitType.name();
	}
	
	@Override
	public void setProgress(float progress) {
		progress *= prod;
		this.progress = progress;
		
		if (timeSinceLastUpdate != 0) {
			progressSpeedInterpolated = (progress - progressLast) / timeSinceLastUpdate;
		}
		
		progressLast = progress;
		timeSinceLastUpdate = 0;
	}
	
	public UpgradeRegimentMessage getPacket() {
		return new UpgradeRegimentMessage(	army.getHome().getHoldingID(), 
									regiment.formationPos, 
									regiment.unitType.ordinal(),
									army == army.getHome().getLevy());
	}

	@Override
	public void finish() {
		
		army.getOwner().addStatModifier(new VaryingStatModifier("Military", House.WEALTH, army.getOwner(), -1, WEALTHY_CONSTRUCTION, WEALTHY_CONSTRUCTION_DECAY, 0));
		
		regiment.maxTroops += MAX_INCREASE_PER_UPGRADE;
		
		int goodID = (int) UnitType.UNIT_STATS.get(regiment.unitType).getStat(UnitType.GOOD_DEMAND_1_ID);
		if (goodID != 0) {
			army.getHome().addDemand(
				goodID-1, 
				(int) UnitType.UNIT_STATS.get(regiment.unitType).getStat(UnitType.GOOD_DEMAND_1_AMOUNT)
			);
		}
		
		goodID = (int) UnitType.UNIT_STATS.get(regiment.unitType).getStat(UnitType.GOOD_DEMAND_2_ID);
		if (goodID != 0) {
			army.getHome().addDemand(
				goodID-1, 
				(int) UnitType.UNIT_STATS.get(regiment.unitType).getStat(UnitType.GOOD_DEMAND_2_AMOUNT)
			);
		}
		
		if (!army.isFighting()) {
			regiment.reinforcementEnabled = true;
		}
		
		if (World.getInstance().getPlayerController().getHouse() == army.getOwner() && EmpireFrameListener.state != GameState.SELECT) {
			HeaderedMail mail = new HeaderedMail(	"Regiment upgraded", 
													"We have upgraded our " + Util.getRankedText(regiment.formationPos+1) + " regiment\n" +
													"in " + army.getHome().getLinkedName() + ". It can now support\n" + 
													"up to " + (int)regiment.maxTroops + " <link=" + regiment.unitType + ">" + regiment.unitType + "\\l.", 
													army.getOwner(), 
													army.getOwner());
			mail.setIconName("Construction");
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
		
		progress = prod;
	}

}
