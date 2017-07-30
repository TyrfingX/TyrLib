package com.tyrfing.games.id17.events;

import com.tyrfing.games.id17.houses.House;

public class Journey extends Event {

	@Override
	public boolean conditionsMet(House house) {
		return false;
	}

	@Override
	public void activate() {
	}

	@Override
	public void onUpdate(float time) {
		
	}	

	@Override
	public boolean isFinished() {
		return false;
	}

}
