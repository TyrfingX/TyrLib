package com.tyrfing.games.id17.ai.tasks;

import java.io.Serializable;

import com.tyrfing.games.id17.holdings.Holding;

public class Task implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7685987700627861493L;
	
	public float value;
	public Holding holding;
	public boolean beingServed;
	public boolean exploration;
	
	public Task(float value, Holding holding) {
		this.value = value;
		this.holding = holding;
	}
}
