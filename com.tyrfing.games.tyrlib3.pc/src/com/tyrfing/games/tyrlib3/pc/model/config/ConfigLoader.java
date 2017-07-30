package com.tyrfing.games.tyrlib3.pc.model.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.pc.model.config.Config.ScreenState;
import com.tyrfing.games.tyrlib3.pc.view.config.ConfigGUI;

public class ConfigLoader {
	
	public Config config;
	
	public ConfigLoader(OnStartGame startGame) {
		
		System.setProperty("sun.java2d.noddraw", "true");
		System.setProperty("sun.awt.noerasebackground", "true");
		
		try {
			URL url = getClass().getResource("/");
	        InputStream raw = new FileInputStream(url.getPath() + "config.xml");
			
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
	        			config.screenSize = new Vector2F(x, y);
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
