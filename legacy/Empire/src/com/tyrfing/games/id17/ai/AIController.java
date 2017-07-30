package com.tyrfing.games.id17.ai;

import java.util.List;
import java.util.Vector;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.util.Color;

public class AIController extends HouseController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -675062475833499255L;
	
	private List<Message> messages = new Vector<Message>();
	protected BehaviorModel model;
	protected Color color;
	
	public AIController(BehaviorModel model) {
		this.model = model;
		color = Color.lerp(Color.getRandomColor(0.2f), DEFAULT_STRATEGIC_COLOR, 0.25f);
	}
	
	public BehaviorModel getModel() {
		return model;
	}
	
	@Override
	public void control(House house) {
		super.control(house);
		model.house = house;
		model.armyModel.onControlHouse(house);
	}

	public void update() {
		
		float timestamp = World.getInstance().getWorldTime();
		for (int i = 0; i < model.memories.size(); ++i) {
			if (timestamp - model.memories.get(i).timestamp >= World.DAYS_PER_YEAR * World.SECONDS_PER_DAY) {
				model.memories.remove(i);
				--i;
			}
		}
		
		while(!messages.isEmpty()) {
			model.processMessage(messages.get(0));
			messages.remove(0);
		}
		
		model.armyModel.update();
		
		if (!house.isOnCooldown()) {
			model.takeAction();
		}
		
	}

	@Override
	public void informMessage(Message message) {
		messages.add(message);
	}

	@Override
	public void informNewHolding(Holding holding) {
		if (holding instanceof Barony) {
			model.armyModel.informNewBarony((Barony)holding);
		}
	}

	@Override
	public void informLostHolding(Holding holding) {
		if (holding instanceof Barony) {
			model.armyModel.informLostBarony((Barony)holding);
		}
	}
	
	@Override
	public void informWarStart(War war) {
		model.armyModel.informWarStart(war);
	}

	@Override
	public void informWarEnd(War war) {
		model.armyModel.informWarEnd(war);
	}
	
	@Override
	public void informAddAlly(House house) {
		model.armyModel.informAddAlly(house);
	}
	
	@Override
	public void informRemoveAlly(House house) {
		model.armyModel.informRemoveAlly(house);
	}

	@Override
	public void destroy() {
		model.armyModel.destroy();
		if (AIThread.getInstance() != null) {
			AIThread.getInstance().removeAI(this);
		}
	}
	
	@Override
	public Color getStrategicColor() {
		return color;
	}
	
}
