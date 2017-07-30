package com.tyrfing.games.id17.ai;

import java.io.Serializable;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.ai.objectives.Objective;
import com.tyrfing.games.id17.houses.House;

public class Decision implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3269615466866796495L;
	
	public transient Action action;
	public House target;
	public Objective objective;
	public int options[];
	public boolean achievesObjective;
	
	public static final Decision ACHIEVED_DECISION = new Decision(null, null, null, null, true);
	public static final Decision UNACHIEVED_DECISION = new Decision(null, null, null, null, false);
	
	public Decision(Action action, Objective objective, int[] options, House target, boolean achieveObjective) {
		this.action = action;
		this.objective = objective;
		this.options = options;
		this.achievesObjective = achieveObjective;
		this.target = target;
	}
	
	public Decision(Action action, int[] options, boolean achieveObjective) {
		this.action = action;
		this.options = options;
		this.achievesObjective = achieveObjective;
	}
	
	public Decision(Objective objective, int[] options, boolean achieveObjective) {
		this.objective = objective;
		this.options = options;
		this.achievesObjective = achieveObjective;
	}

}
