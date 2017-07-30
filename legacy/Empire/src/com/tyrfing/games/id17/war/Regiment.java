package com.tyrfing.games.id17.war;

import java.io.Serializable;

public class Regiment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3264831963008999067L;
	
	public float troops;
	public float maxTroops;
	public UnitType unitType;
	public int formationPos;
	public boolean reinforcementEnabled = true;
	
	public Regiment(UnitType type, int troops, int maxTroops, int formationPos){
		this.troops = troops;
		this.maxTroops = maxTroops;
		this.unitType = type;
		this.formationPos = formationPos;
	}
	
}
