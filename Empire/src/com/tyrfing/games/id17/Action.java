package com.tyrfing.games.id17;

import java.io.Serializable;

import com.tyrfing.games.id17.houses.House;


public abstract class Action implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1200488853098160445L;
	
	protected String name;
	protected String disabledText;
	
	public Action(String name) {
		this.name = name;
	}
	
	public String getDisabledText() {
		return disabledText;
	}
	
	public void setDisabledText(House sender, House receiver) {
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void selectedByUser(House sender, House receiver);
	public boolean isEnabled(House sender, House receiver) { return true; }
}
