package com.tyrfing.games.pc.main;

import java.awt.Color;

import javax.swing.JFrame;

import com.TyrLib2.PC.config.Config;
import com.TyrLib2.PC.config.ConfigLoader;
import com.TyrLib2.PC.config.OnStartGame;
import com.tyrfing.games.id17.EmpireFrameListener;



public class Main {
	public static final void main(String[] args) {
		
		if (args.length > 0) {
			int port = (args.length > 1) ? Integer.valueOf(args[1]) : EmpireFrameListener.DEFAULT_PORT;
			EmpireFrameListener.SERVER_PORT = port;
			new Game(null, port);
		} else {
			new ConfigLoader(new OnStartGame() {
				@Override
				public void startGame(Config config) {
		    		JFrame frame = new JFrame();
		    		frame.setSize(240, 240);
		    		frame.getContentPane().setBackground(Color.BLACK);
		    		frame.setUndecorated(true);
		    		frame.setVisible(true);
					new Game(frame, config);
				}
			});
		}
	}
}
