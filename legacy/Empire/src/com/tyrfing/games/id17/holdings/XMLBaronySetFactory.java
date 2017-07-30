package com.tyrfing.games.id17.holdings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrfing.games.id17.world.MapFile;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.main.Media;

public class XMLBaronySetFactory {
	
	private XmlPullParser parser;
	private MapFile mapFile;
	
	public XMLBaronySetFactory(MapFile mapFile) {
		this.mapFile = mapFile;
	}
	
	public void create() {
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	
	        factory.setValidating(false);
	        parser = factory.newPullParser();
	
	        InputStream raw = Media.CONTEXT.openAsset(mapFile.baronyData);
	        parser.setInput(raw, null);
	        
	        int eventType = parser.getEventType();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("Barony")) {
	        			parseBarony();
	        		}
	        	} 
	        	eventType = parser.next();
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load BaronySet " + mapFile.baronyData + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load BaronySet " + mapFile.baronyData + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load BaronySet " + mapFile.baronyData + " due to IOException!");
		}
	}
	
	public void parseBarony() {
		Barony holding = (Barony) new XMLBaronyFactory(mapFile, parser).create();
		holding.holdingData.index = (short) World.getInstance().getHoldings().size();
		World.getInstance().addHolding(holding);
		
		holding.getWorldChunk().getCastleTile().holdingID = holding.getHoldingID();
		
		holding.holdingData.worldEntity.setUserData(holding.getHoldingID());
	}
}
