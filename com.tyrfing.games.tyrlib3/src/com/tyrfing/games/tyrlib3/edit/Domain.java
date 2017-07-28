package com.tyrfing.games.tyrlib3.edit;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.resource.Resource;

public class Domain {
	private ActionStack actionStack;
	private List<Resource> resources;
	
	public Domain() {
		resources = new ArrayList<Resource>();
	}
	
	public void setActionStack(ActionStack actionStack) {
		this.actionStack = actionStack;
	}
	
	public ActionStack getActionStack() {
		return actionStack;
	}
	
	public List<Resource> getResources() {
		return resources;
	}
}
