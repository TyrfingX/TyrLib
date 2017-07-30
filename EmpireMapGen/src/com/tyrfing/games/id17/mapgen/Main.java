package com.tyrfing.games.id17.mapgen;

import com.tyrfing.games.id17.mapgen.gui.GUI;

public class Main {
	public static void main(String[] args) {
		try {
			GUI gui = new GUI();
			gui.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
