package com.TyrLib2.PC.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.TyrLib2.PC.config.Config.ScreenState;
import com.tyrlib2.math.Vector2;

public class ConfigLoader {
	
	public Config config;
	
	public ConfigLoader(OnStartGame startGame) {
		
		System.setProperty("sun.java2d.noddraw", "true");
		System.setProperty("sun.awt.noerasebackground", "true");
		
		try {
			URL url = getClass().getResource("/");
			String path = url.getPath() + "config.xml";
	        InputStream raw = new FileInputStream(path);
			
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	
	        factory.setValidating(false);
	        XmlPullParser parser = factory.newPullParser();
	       
	        parser.setInput(raw, null);
	        
	        int eventType = parser.getEventType();
	        
	        config = new Config();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("WindowMode")) {
	        			config.screenState = ScreenState.valueOf(parser.getAttributeValue(0));
	        		} else if (elementName.equals("WindowSize")) {
	        			float x = Float.valueOf(parser.getAttributeValue(0));
	        			float y = Float.valueOf(parser.getAttributeValue(1));
	        			config.screenSize = new Vector2(x, y);
	        		}
	        	} 
	        	eventType = parser.next();
	        }
	        
	        startGame.startGame(config);
	        
		} catch (Exception e) {
			e.printStackTrace();
			ConfigGUI config = new ConfigGUI(startGame);
			config.setVisible(true);
		}
	}
}
