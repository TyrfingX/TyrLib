package com.tyrfing.games.id17.holdings.projects;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.networking.BuildRoadMessage;
import com.tyrfing.games.id17.networking.ProjectCompleted;
import com.tyrfing.games.id17.world.RoadNode;
import com.tyrfing.games.id17.world.Tile;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

public class RoadProject implements IProject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5929688822329964707L;

	private float progress;
	private float prod;
	private boolean aborted;
	
	private Holding from;
	private Holding to;
	
	private List<RoadNode> unrealizedPath;
	private int segment;
	
	public RoadProject(Holding from, Holding to) {
		this.prod = getRequiredProd(from.holdingData.barony);
		this.from = from;
		this.to = to;
		unrealizedPath = World.getInstance().getMap().getRoadMap().hasDirectRealizeablePath(from.getHoldingID(), to.getHoldingID());
	
		if (unrealizedPath == null) {
			throw new RuntimeException("Cannot make a road from " + from + " to " + to);
		}
		
		from.holdingData.barony.getOwner().changeGold(-getCosts(from.holdingData.barony));
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast( getPacket()	);
		}  
	}
	
	public static float getRequiredProd(Barony from) {
		return (float) (Math.pow(2, from.getTotalCountRoads()) * 10000);
	}
	
	public static int getExpectedDays(Holding from) {
		int days = (int)(getRequiredProd(from.holdingData.barony)/from.getHoldingData().prod);
		return days;
	}
	
	public static int getCosts(Barony from) {
		return (int) (Math.pow(2, from.getCountRoads()) * 25);
	}
	
	@Override
	public void onUpdate(float time) {
		progress += time * from.getHoldingData().prod;
		
		if (progress / prod >= (float) segment / unrealizedPath.size()) {
			if (segment < unrealizedPath.size()-1) {
				if (segment != 0) {
					RoadNode node = unrealizedPath.get(segment);
					Tile t = World.getInstance().getMap().getTile(node.x, node.y);
					if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
						t.chunk.changeTileType(t, 8);
					}
					node.realize();
				}
				segment++;
			}
		}
		
		if (progress >= prod) {
			
			progress = prod;
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				from.finishActiveProject();
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ProjectCompleted(from.getHoldingID()));
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

	@Override
	public void abort() {
		aborted = true;
	}

	@Override
	public void finish() {
		
		from.getOwner().addStatModifier(new VaryingStatModifier("Construction", House.WEALTH, from.getOwner(), -1, BuildingProject.WEALTHY_CONSTRUCTION, BuildingProject.WEALTHY_CONSTRUCTION_DECAY, 0));
		
		from.addRoad(to);
		
		progress = prod;
		
		if (World.getInstance().getPlayerController().getHouse() == from.holdingData.barony.getOwner() && EmpireFrameListener.state != GameState.SELECT) {
			
			String text = "We have completed our construction\nand built a road connecting\n" + from.getLinkedName() + " with " + to.getLinkedName() + ".";
			HeaderedMail mail = new HeaderedMail(	"Production finished", 
													text, 
													World.getInstance().getPlayerController().getHouse(), 
													World.getInstance().getPlayerController().getHouse());
			mail.setIconName("Construction");
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
	}

	@Override
	public String getIconAtlasName() {
		return "MAIL_ICONS";
	}

	@Override
	public String getIconRegionName() {
		return "Economy";
	}

	@Override
	public void setProgress(float progress) {
		progress *= prod;
		this.progress = progress;
	}
	
	public BuildRoadMessage getPacket() {
		return new BuildRoadMessage(from.getHoldingID(), 
									to.getHoldingID());
	}

	public static boolean canBuild(Holding h, Holding n) {
		return 		h != n
				&&	h.holdingData.barony.getOwner().isVisible(n.holdingData.barony.getIndex())
				&&	h.holdingData.barony.getOwner().getGold() >= getCosts(h.holdingData.barony)
				&& 	h.getProject() == null
				&& 	World.getInstance().getMap().isHoldingNeighbour(h, n)
				&& 	World.getInstance().getMap().getRoadMap().hasDirectRealizeablePath(h.getHoldingID(), n.getHoldingID()) != null
				&& 	!World.getInstance().getMap().getRoadMap().hasDirectPath(h.getHoldingID(), n.getHoldingID())
				&&	!h.isRoadBuilt(n)
				&& (	n.holdingData.barony == h.holdingData.barony 
						|| 	n.holdingData.barony.getOwner().haveSameOverlordWith(h.getOwner())
						||	n.holdingData.barony.getOwner().getSupremeOverlord().hasTradeAgreement(h.getOwner().getSupremeOverlord()));
	}

}
