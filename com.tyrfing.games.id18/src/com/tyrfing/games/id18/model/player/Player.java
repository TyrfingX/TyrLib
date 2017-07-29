package com.tyrfing.games.id18.model.player;

import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public class Player implements ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7317275799975039042L;
	private String provinceName;
	
	public String getProvinceName() {
		return provinceName;
	}
	
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
}
