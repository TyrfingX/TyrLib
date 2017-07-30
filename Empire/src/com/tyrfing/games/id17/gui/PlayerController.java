package com.tyrfing.games.id17.gui;

import com.tyrfing.games.id17.ChatListener;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.SeasonMaterial;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class PlayerController extends HouseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2975173924244491171L;
	
	@Override
	public void informMessage(Message message) {
		if (EmpireFrameListener.state != GameState.SELECT) {
			Mail mail = null;
			
			if (message.action != null) {
				if (message.response > -1) {
					mail = message.action.getResponseMail(message);
				} else if (message.response == -2) {
					mail = message.action.getOptionMail(message.sender, message.receiver); 
				} else {
					mail = message.action.getSendMail(message); 
				}
			}
			
			if (mail != null)  {
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
	}

	@Override
	public void informNewHolding(Holding holding) {
		
	}

	@Override
	public void informLostHolding(Holding holding) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void control(House house) {
		if (EmpireFrameListener.state == EmpireFrameListener.GameState.SELECT) {
			Window houseButton = WindowManager.getInstance().getWindow("HOUSE_BUTTON");
			if (houseButton != null) {
				((ImageBox)houseButton).setAtlasRegion(house.getSigilName());
			}
			
			
			unmarkControlledHouse();
			
			if (this.getHouse() != null) {
				for (int i = 0; i < this.getHouse().getCountWars(); ++i) {
					this.getHouse().getWar(i).destroyGUI();
				}
			}
		
			super.control(house);
			
			markControlledHouse();
			
			for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
				if ( highlightBarony(World.getInstance().getBarony(i)) ) {
					World.getInstance().getBarony(i).getWorldChunk().setOwnerValue(SeasonMaterial.FULL);
				} else {
					World.getInstance().getBarony(i).getWorldChunk().setOwnerValue(SeasonMaterial.NONE);
				}
			}
			
			for (int i = 0; i < house.getCountWars(); ++i) {
				house.getWar(i).createGUI();
			}
			
			for (int i = 0; i < subFactions.size(); ++i) {
				for (int j = 0; j < house.getCountWars(); ++j) {
					subFactions.get(i).getWar(j).createGUI();
				}
			}
			
			updateBorderColors();
		} else {
			super.control(house);
		}
	}
	
	public void updateBorderColors() {
		for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
			World.getInstance().getHouses().get(i).updateBorders();
		}
	}
	
	public boolean highlightBarony(Barony barony) {
		return barony.getOwner().haveSameOverlordWith(house);
	}
	
	@Override
	public Color getStrategicColor() {
		return ChatListener.chatColors[playerID];
	}

}
