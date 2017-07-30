package com.tyrfing.games.pc.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.TyrLib2.PC.main.MainStarter;

public class Starter {
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		MainStarter starter = new MainStarter();
		starter.main("com.tyrfing.games.pc.main.Main", args);
	}
}

