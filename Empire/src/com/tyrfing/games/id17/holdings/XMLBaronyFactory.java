package com.tyrfing.games.id17.holdings;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.XMLArmyFactory;
import com.tyrfing.games.id17.world.MapFile;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.math.Vector3;

public class XMLBaronyFactory implements IHoldingFactory {

	private XmlPullParser parser;
	private MapFile mapFile;
	
	private Barony barony;
	
	public XMLBaronyFactory(MapFile mapFile, XmlPullParser parser) {
		this.mapFile = mapFile;
		this.parser = parser;
	}
	
	@Override
	public Holding create() {
		try {	        
			
			parseBarony();
			
	        int eventType = parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("Holding")) {
	        			parseHolding();
	        		} else if (elementName.equals("Levy")) {
	        			XMLArmyFactory armyFactory = new XMLArmyFactory(parser);
	        			Army levy = armyFactory.create();
	        			barony.setLevy(levy);
	        		} else if (elementName.equals("Garrison")) {
	        			XMLArmyFactory armyFactory = new XMLArmyFactory(parser);
	        			Army garrison = armyFactory.create();
	        			barony.setGarrison(garrison);
	        		}
	        	} else if(eventType == XmlPullParser.END_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("Barony")) {
	        			break;
	        		}
	        	}
	        	eventType = parser.next();
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load barony " + mapFile.baronyData + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load barony " + mapFile.baronyData + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load barony " + mapFile.baronyData + " due to IOException!");
		}
		
		return barony;
	}
	
	private void parseBarony() {
		
		HoldingData holdingData = new HoldingData();
		Vector3 pos = new Vector3();
		
		holdingData.typeName = "Castle";
		float[] castle = Barony.stats;
		holdingData.income = castle[HoldingTypes.INCOME];
		PopulationType[] types = PopulationType.values();
		System.arraycopy(castle, HoldingTypes.POPULATION, holdingData.population, 0, types.length);
		
		for (int i = 0; i < holdingData.population.length; ++i) {
			if (Float.isNaN(holdingData.population[i])) {
				throw new RuntimeException("parseBarony::Pop is NaN");
			}
		}
		
		holdingData.inhabitants = (int) castle[HoldingTypes.INHABITANTS];
		holdingData.growth = castle[HoldingTypes.GROWTH];
		
		BaronyWindow baronyWindow = new BaronyWindow();
		
		int countSubHoldings = 0;
		
		int countAttributes = parser.getAttributeCount();
		for (int i = 0; i < countAttributes; ++i) {
			String name  = parser.getAttributeName(i);
			if (name.equals("name")) {
				holdingData.name = parser.getAttributeValue(i);
			} else if (name.equals("x")) {
				baronyWindow.x = Short.valueOf(parser.getAttributeValue(i));
				pos.x = (baronyWindow.x-mapFile.tileMap.getWidth()/2)* WorldChunk.BLOCK_SIZE;
			} else if (name.equals("y")) {
				baronyWindow.y = Short.valueOf(parser.getAttributeValue(i));
				pos.y = (baronyWindow.y-mapFile.tileMap.getHeight()/2) * WorldChunk.BLOCK_SIZE;
			} else if (name.equals("w")) {
				baronyWindow.w = Short.valueOf(parser.getAttributeValue(i));
				baronyWindow.w += 4;
			} else if (name.equals("h")) {
				baronyWindow.h = Short.valueOf(parser.getAttributeValue(i));
				baronyWindow.h += 4;
			} else if (name.equals("color")) {
				baronyWindow.color = Long.valueOf(parser.getAttributeValue(i));
			} else if (name.equals("countSubHoldings")) {
				countSubHoldings = Integer.valueOf(parser.getAttributeValue(i));
			} 
		}
		
		barony = new Barony(countSubHoldings, holdingData, Barony.stats);
		holdingData.barony = barony;
		barony.getNode().setRelativePos(pos);
		barony.build(mapFile, baronyWindow);
		
		holdingData.worldEntity = barony.getWorldChunk().getCastleEntity();
		barony.initOffsets();
	}
	
	private void parseHolding() {
		
		String name = "";
		String type = ""; 
		int objectNo = -1;
		HoldingData holdingData = new HoldingData();
		
		int countAttributes = parser.getAttributeCount();
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("name")) {
				name = parser.getAttributeValue(i);
			} else if (parser.getAttributeName(i).equals("objectNo")) {
				objectNo = Integer.valueOf(parser.getAttributeValue(i));
			} else if (parser.getAttributeName(i).equals("type")) {
				type = parser.getAttributeValue(i);
			} 
		}
		
		holdingData.typeName = type;
		holdingData.name = name;
		float[] stats = World.getInstance().getHoldingTypes().getStats(type);
		holdingData.income = stats[HoldingTypes.INCOME];
		holdingData.inhabitants = (int) stats[HoldingTypes.INHABITANTS];
		
		PopulationType[] types = PopulationType.values();
		System.arraycopy(stats, HoldingTypes.POPULATION, holdingData.population, 0, types.length);
		
		holdingData.growth = stats[HoldingTypes.GROWTH];
		Entity entity = barony.getWorldChunk().getMainObject(objectNo);
		holdingData.objectNo = objectNo;
		holdingData.worldEntity = entity;
		holdingData.barony = barony;
		
		Holding holding = World.getInstance().getHoldingTypes().createHolding(holdingData);
		holding.initOffsets();
		
		barony.addHolding(holding);
		holding.holdingData.index = (short) World.getInstance().getHoldings().size();
		World.getInstance().addHolding(holding);
		holding.holdingData.worldEntity.setUserData(holding.getHoldingID());
		
		barony.getWorldChunk().getMainObjectTile(objectNo).holdingID = holding.getHoldingID();
	}

}
