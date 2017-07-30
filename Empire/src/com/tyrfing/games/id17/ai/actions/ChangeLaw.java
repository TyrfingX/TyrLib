package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.LawSet;

public class ChangeLaw extends AIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3555023934047284274L;

	/**
	 * OPTIONS:
	 * 0: LAW ID
	 * 1: SETTING
	 */
	
	public ChangeLaw() {
		super("Change Law");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		return     options[1] < LawSet.getLaw(options[0]).options.length
				&& executor.getLawSetting(options[0]) != options[1];
	}

	@Override
	public void execute(House executor, int[] options) {
		LawSet.getLaw(options[0]).selectOption(options[1], executor);
	}

}
