package com.tyrfing.games.id17.holdings.projects;

import java.util.Arrays;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.networking.BuildMessage;
import com.tyrfing.games.id17.networking.ProjectCompleted;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

public class BuildingProject implements IProject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8880534922513049568L;
	private House orderer;
	private Building.TYPE type;
	private Holding holding;
	private float progress;
	private float prod;
	private float cost;
	
	private float progressLast;
	private float progressSpeedInterpolated;
	private float timeSinceLastUpdate;
	private boolean aborted;
	
	public static final int WEALTHY_CONSTRUCTION = 8;
	public static final float WEALTHY_CONSTRUCTION_DECAY = -1.5f / World.DAYS_PER_SEASON;
	
	public BuildingProject(Building.TYPE type, Holding holding, House orderer) {
		this(type, holding, orderer, -1);
	}
	
	public BuildingProject(Building.TYPE type, Holding holding, House orderer, int days) {
		
		this.orderer = orderer;
		this.type = type;
		this.holding = holding;
		
		if (days == -1) {
			days = getExpectedDays();
		}
		
		cost = Building.getPrice(type, holding);
		prod = Building.getProd(type, holding);
		holding.getOwner().changeGold((int) -cost);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new BuildMessage(type.ordinal(), holding.getHoldingID(), (short) days));
		} 
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()  && EmpireFrameListener.state != GameState.SELECT) {
			if (Arrays.binarySearch(Building.GREAT_BUILDINGS, type) >= 0 && orderer != World.getInstance().getPlayerController().getHouse()) {
				Mail mail = new HeaderedMail(
					"Great Building: " +  type +  "\nConstruction started!", 
					"House of " +  orderer.getLinkedName() + " has started the\n" + 
					"construction of the <link=Great Building>Great Building\\l\n<link=" + type +">" + type + "\\l." +
					"\n\nAt the current rate they will finish\nin " + days + "d.",
					orderer, 
					World.getInstance().getPlayerController().getHouse()
				);
				mail.setIconName("Construction");
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
	}

	@Override
	public void onUpdate(float time) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			progress += time * holding.getHoldingData().prod;
		} else {
			timeSinceLastUpdate += time;
			progress += time * progressSpeedInterpolated;
		}
		
		if (progress >= prod) {
			progress = prod;
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				holding.finishActiveProject();
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ProjectCompleted(holding.getHoldingID()));
			}
		}
	}

	@Override
	public boolean isFinished() {
		return progress >= prod || aborted;
	}

	@Override
	public float getProgress() {
		return progress / prod;
	}
	
	public int getExpectedDays() {
		return (int)(Building.getProd(type, holding)/holding.getHoldingData().prod);
	}

	@Override
	public void abort() {
		//orderer.changeGold((int) cost);
		aborted = true;
	}

	@Override
	public String getIconAtlasName() {
		return "BUILDINGS";
	}

	@Override
	public String getIconRegionName() {
		return type.name();
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
	
	public BuildMessage getPacket() {
		return new BuildMessage(type.ordinal(),holding.getHoldingID());
	}

	@Override
	public void finish() {
		
		holding.getOwner().addStatModifier(new VaryingStatModifier("Construction", House.WEALTH, holding.getOwner(), -1, WEALTHY_CONSTRUCTION, WEALTHY_CONSTRUCTION_DECAY, 0));
		
		progress = prod;
		
		Building building = holding.isBuilt(type);
		if (building != null) {
			building.changeLevel(1);
		} else {
			building = Building.create(type, 1);
			holding.addBuilding(building);
		}
		building.applyEffects(holding, 1);
		holding.getStats()[HoldingTypes.WORKERS_ATTRACTIVITY] += prod / 50;
		
		if (World.getInstance().getPlayerController().getHouse() == orderer && EmpireFrameListener.state != GameState.SELECT) {
			
			String text = "We have completed our construction\nand built a " + type + " in\n" + holding.getLinkedName() + ".";
			
			int greatBuildingIndex = Arrays.binarySearch(Building.BASE_BUILDINGS, type);
			if (greatBuildingIndex >= 0 && orderer == World.getInstance().getPlayerController().getHouse() && building.getLevel() == Building.GREAT_BUILDING_MIN_LEVEL) {
				text += "\n\nConstruction of the <link=Great Building>Great Building\\l\n<link=" + Building.GREAT_BUILDINGS[greatBuildingIndex] +">" + Building.GREAT_BUILDINGS[greatBuildingIndex] + "\\l unlocked.";
			}
			
			HeaderedMail mail = new HeaderedMail(	"Production finished", 
													text, 
													orderer, 
													orderer);
			mail.setIconName("Construction");
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode() && EmpireFrameListener.state != GameState.SELECT) {
			if (Arrays.binarySearch(Building.GREAT_BUILDINGS, type) >= 0 && World.getInstance().getPlayerController().getHouse() != orderer) {
				Mail mail = new HeaderedMail(
					"Great Building: " +  type +  "\nConstruction completed!", 
					"House of " +  orderer.getLinkedName() + " has completed the\n" + 
					"construction of the <link=Great Building>Great Building\\l\n<link=" + type +">" + type + "\\l.",
					orderer, 
					World.getInstance().getPlayerController().getHouse()
				);
				mail.setIconName("Construction");
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}

		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (Arrays.binarySearch(Building.GREAT_BUILDINGS, type) >= 0) {
				for (int i = 0; i < World.getInstance().getHoldings().size(); ++i) {
					Holding h = World.getInstance().getHoldings().get(i);
					if (h.getProject() != null && h.getProject() instanceof BuildingProject) {
						BuildingProject bp = (BuildingProject) h.getProject();
						if (bp.type == type) {
							bp.abort();
						}
					}
				}
			}
		}
		
	}
	

}
