package com.tyrfing.games.id17.startmenu;

import java.io.Serializable;

public class PlayerUpdate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4886448601041725055L;
	
	public int[] current;
	public int[] max;
	public int[] id;
	public String[] address;
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < id.length; ++i) {
			b.append(address[i]);
			b.append(", ");
			b.append("ID: ");
			b.append(id[i]);
			b.append(", ");
			b.append(current[i]);
			b.append("/");
			b.append(max[i]);
			b.append("\n");
		}
		b.append("---------------------------");
		return b.toString();
	}
	
}
