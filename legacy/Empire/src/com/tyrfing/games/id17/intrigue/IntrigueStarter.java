package com.tyrfing.games.id17.intrigue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.game.IUpdateable;

public class IntrigueStarter implements IUpdateable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382384949828929769L;
	
	private List<IntrigueProject> projects = new ArrayList<IntrigueProject>();
	
	public IntrigueStarter() {
	}
	
	public void addProject(IntrigueProject p) {
		if (p == null || p.action == null) {
			throw new RuntimeException("Invalid Intrigue Action!");
		}
		projects.add(p);
	}

	@Override
	public void onUpdate(float time) {
		while(!projects.isEmpty()) {
			IntrigueProject p = projects.get(0);
			p.action.startProject(p.sender, p.receiver, p.options);
			projects.remove(0);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
