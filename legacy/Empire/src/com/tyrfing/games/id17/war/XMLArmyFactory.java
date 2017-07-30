package com.tyrfing.games.id17.war;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XMLArmyFactory {
	private XmlPullParser parser;
	private Army army;
	
	public XMLArmyFactory(XmlPullParser parser) {
		this.parser = parser;
	}
	
	public Army create() throws XmlPullParserException, IOException {
	        
		army = new Army();
		
        int eventType = parser.getEventType();
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	if(eventType == XmlPullParser.START_TAG) {
        		String elementName = parser.getName();
        		if (elementName.equals("Regiment")) {
        			parseRegiment();
        		} 
        	} else if (eventType == XmlPullParser.END_TAG) {
        		String elementName = parser.getName();
        		if (elementName.equals("Garrison") || elementName.equals("Levy")) {
        			break;
        		} 
        	}
        	eventType = parser.next();
        }
		
		return army;
	}
	
	private void parseRegiment() {
		
		UnitType unitType = null;
		int formationPos = -1;
		int troops = -1;
		int maxTroops = -1;
		
		int countAttributes = parser.getAttributeCount();
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("type")) {
				String type = parser.getAttributeValue(i);
				unitType = UnitType.valueOf(type);
			} else if (parser.getAttributeName(i).equals("amount")) {
				troops = Integer.valueOf(parser.getAttributeValue(i));
			} else if (parser.getAttributeName(i).equals("position")) {
				formationPos = Integer.valueOf(parser.getAttributeValue(i));
			} else if (parser.getAttributeName(i).equals("maxAmount")) {
				maxTroops = Integer.valueOf(parser.getAttributeValue(i));
			} 
		}
		
		Regiment regiment = new Regiment(unitType, troops, maxTroops, formationPos);
		army.addRegiment(regiment);
	}
}
