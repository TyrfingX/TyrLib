package com.tyrfing.games.id17.houses;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;

public class XMLHouseFactory {
	private XmlPullParser parser;
	private String source;
	
	private House house;
	private HouseController controller;
	
	private boolean added = false;
	
	public static short ID = 0;
	
	public XMLHouseFactory(XmlPullParser parser, HouseController controller) {
		this.parser = parser;
		this.controller = controller;
	}
	
	public House create() {
		try {
	        
			parseHouse();
			
	        int eventType = parser.next();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("Holding")) {
	        			parseHoldingInformation();
	        		} else if (elementName.equals("House")) {
	        			if (!added) {
	        				added = true;
	        				World.getInstance().addHouse(house);
	        			}
	        			House subHouse = new XMLHouseFactory(parser, new AIController(new BehaviorModel())).create();
	        			house.addSubHouse(subHouse);
	        		}
	        	} else if (eventType == XmlPullParser.END_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("House")) {
	        			break;
	        		}
	        	}
	        	eventType = parser.next();
	        }
	        
	        if (!added) {
	        	World.getInstance().addHouse(house);
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load House " + source + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load House " + source + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load House " + source + " due to IOException!");
		}
		
		return house;
	}
	
	public void parseHouse() {
		int countAttributes = parser.getAttributeCount();
		
		String name = "";
		
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("name")) {
				name = parser.getAttributeValue(i);
			}
		}
		
		house = new House(name, controller, ID++);
	}
	
	public void parseHoldingInformation() {
		int countAttributes = parser.getAttributeCount();
		
		Holding holding = null;
		
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("name")) {
				String holdingFullname = parser.getAttributeValue(i);
				holding = World.getInstance().getHoldingByFullName(holdingFullname);
			}
		}
		
		house.addHolding(holding);
		
	}
}
