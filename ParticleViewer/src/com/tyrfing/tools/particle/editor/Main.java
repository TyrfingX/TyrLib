package com.tyrfing.tools.particle.editor;


import java.awt.Color;

import javax.swing.JFrame;

import com.TyrLib2.PC.config.Config;
import com.TyrLib2.PC.config.ConfigLoader;
import com.TyrLib2.PC.config.OnStartGame;



public class Main {
	public static final void main(String[] args) {
		new ConfigLoader(new OnStartGame() {
			@Override
			public void startGame(Config config) {
	    		JFrame frame = new JFrame();
	    		frame.setSize(240, 240);
	    		frame.setBackground(Color.BLACK);
	    		frame.setVisible(true);
				new Viewer(frame, config);
			}
		});
	}
}
