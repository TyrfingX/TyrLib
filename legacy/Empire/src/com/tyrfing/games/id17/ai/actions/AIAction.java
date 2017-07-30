package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.houses.House;

public abstract class AIAction extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5235287036084566770L;

	public AIAction(String name) {
		super(name);
	}
	
	public abstract boolean isEnabled(House executor, int[] options);
	public abstract void execute(House executor, int[] options);

	@Override
	public void selectedByUser(House sender, House receiver) { }
	
}
