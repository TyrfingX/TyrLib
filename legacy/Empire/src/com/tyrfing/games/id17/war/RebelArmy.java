package com.tyrfing.games.id17.war;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.PopulationType;
import com.tyrfing.games.id17.world.World;

public class RebelArmy extends Army{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5452732954254676860L;
	public final Holding origin;
	private float strengths;
	
	public boolean destroyed = false;
	
	public RebelArmy(Holding origin, float strengths) {
		this.origin = origin;
		this.strengths = strengths;
	}
	
	@Override
	public void destroy() {
		
		destroyed = true;
		
		if (origin != null) {
			int troops = this.getTotalTroops();
			
			PopulationType popTypes[] = PopulationType.VALUES;
			
			for (int i = 0; i < popTypes.length; ++i) {
				origin.holdingData.changePop(i, troops * strengths);
			}
			origin.holdingData.inhabitants += troops;
		}
		
		World.getInstance().removeHouse(this.getOwner());
		
		for (short i = 0; i < World.getInstance().getHouses().size(); ++i) {
			World.getInstance().getHouses().get(i).id = i;
		}

		this.getOwner().armies.remove(new Short(this.id));
		World.getInstance().armies.remove(this);
		
		for (int i = 0; i < World.getInstance().armies.size(); ++i) {
			Army army = World.getInstance().armies.get(i);
			for (int j = 0; j < army.getOwner().armies.size(); ++j) {
				if (army.getOwner().armies.get(j) == army.id) {
					army.getOwner().armies.set(j, (short) i);
					break;
				}
			}
			
			army.id = (short) i;
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			AIThread.getInstance().removeAI((AIController)this.getOwner().getController());
		}
		World.getInstance().getUpdater().removeItem(this.getOwner());
		
		super.destroy();
	}
	
	@Override
	public void kill() {
		
		super.kill();
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (this.getOwner().getCountWars() > 0) {
				War war = this.getOwner().getWar(0);
				war.win(war.getOther(this.getOwner()));
			}
		}
		
	}
}
