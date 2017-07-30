package com.tyrfing.games.id17.houses;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrlib2.main.Media;

public class XMLHouseSetFactory {
	
	private XmlPullParser parser;
	private String source;
	
	public XMLHouseSetFactory(String source) {
		this.source = source;
	}
	
	public void create() {
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	
	        factory.setValidating(false);
	        parser = factory.newPullParser();
	
	        InputStream raw = Media.CONTEXT.openAsset(source);
	        parser.setInput(raw, null);
	        
	        int eventType = parser.getEventType();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("House")) {
	        			new XMLHouseFactory(parser, new AIController(new BehaviorModel())).create();
	        		} 
	        	} 
	        	eventType = parser.next();
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load HouseSet " + source + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load HouseSet " + source + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load HouseSet " + source + " due to IOException!");
		}
	}
}
