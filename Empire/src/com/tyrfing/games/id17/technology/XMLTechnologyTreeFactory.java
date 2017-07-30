package com.tyrfing.games.id17.technology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrfing.games.id17.effects.AddBuildingEffect;
import com.tyrfing.games.id17.effects.AddBuildingMultEffect;
import com.tyrfing.games.id17.effects.AddGoodMultEffect;
import com.tyrfing.games.id17.effects.AddHouseMultEffect;
import com.tyrfing.games.id17.effects.AddProductionEffect;
import com.tyrfing.games.id17.effects.AddUnitMultEffect;
import com.tyrfing.games.id17.effects.EnableBuildingEffect;
import com.tyrfing.games.id17.effects.EnableUnitEffect;
import com.tyrfing.games.id17.effects.HoldingStatEffect;
import com.tyrfing.games.id17.effects.IEffect;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.main.Media;

public class XMLTechnologyTreeFactory {

	private XmlPullParser parser;
	private String source;
	private TechnologyTree tree;
	private int currentTech;
	
	public XMLTechnologyTreeFactory(String source) {
		this.source = source;
	}
	
	public TechnologyTree create() {
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
	        		if (elementName.equals("TechnologyTree")) {
	        			createTree();
	        		} else if (elementName.equals("Technology")) {
	        			parseTech();
	        		}
	        	} 
	        	eventType = parser.next();
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load TechnologyTree " + source + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load TechnologyTree " + source + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load TechnologyTree " + source + " due to IOException!");
		}
		
		return tree;
	}
	
	private void createTree() {
		int countAttributes = parser.getAttributeCount();
		String name = "";
		int countTechs = 0;
			
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("name")) {
				name = parser.getAttributeValue(i);
			} else if (parser.getAttributeName(i).equals("count")) {
				countTechs = Integer.valueOf(parser.getAttributeValue(i));
			}
		}
		
		tree = new TechnologyTree(name, new Technology[countTechs]);
	}
	
	private void parseTech() throws XmlPullParserException, IOException {
		
		String name = parser.getAttributeValue(0);
		int scienceMax = Integer.valueOf(parser.getAttributeValue(1));
		int funds = Integer.valueOf(parser.getAttributeValue(2));
		float iconPosX = Float.valueOf(parser.getAttributeValue(3));
		float iconPosY = Float.valueOf(parser.getAttributeValue(4));
		ScaledVector2 iconPos = new ScaledVector2(iconPosX, iconPosY, 2);
		Technology[] pre = null;
		int preIndex = 0;
		String researchEffsDesc = "";
		String discoverEffsDesc = "";
		IEffect[] researchEffs = null;
		IEffect[] discoverEffs = null;
		
		// Parse research&discovery effs
		
        int eventType = parser.getEventType();
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	if(eventType == XmlPullParser.START_TAG) {
        		String elementName = parser.getName();
        		if (elementName.equals("ResearchEffects")) {
        			researchEffsDesc = parser.getAttributeValue(0);
        			researchEffs = parseEffs("ResearchEffects");
        		} else if (elementName.equals("DiscoverEffects")) {
        			discoverEffsDesc = parser.getAttributeValue(0);
        			discoverEffs = parseEffs("DiscoverEffects");
        		} else if (elementName.equals("Requires")) {
        			int countRequirements = Integer.valueOf(parser.getAttributeValue(0));
        			pre = new Technology[countRequirements];
        		} else if (elementName.equals("RequiredTechnology")) {
        			int id = Integer.valueOf(parser.getAttributeValue(0));
        			pre[preIndex++] = tree.techs[id];
        		}
        	} else if(eventType == XmlPullParser.END_TAG) {
        		String elementName = parser.getName();
        		if (elementName.equals("Technology")) {
        			break;
        		}
        	}
        	
        	eventType = parser.next();
        }
		
		if (researchEffs == null) {
			researchEffs = new IEffect[0];
		}
		
		if (discoverEffs == null) {
			discoverEffs = new IEffect[0];
		}
		
		tree.techs[currentTech++] = new Technology(	name, scienceMax, funds, pre, researchEffsDesc, 
													discoverEffsDesc, researchEffs, discoverEffs, iconPos);
	}
	
	public IEffect[] parseEffs(String endTag) throws XmlPullParserException, IOException {
		int count = Integer.valueOf(parser.getAttributeValue(1));
		IEffect[] effs = new IEffect[count];
		int index = 0;
		
		int eventType = parser.getEventType();
		
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	if(eventType == XmlPullParser.START_TAG) {
         		String elementName = parser.getName();
        		if (elementName.equals("Effect")) {
        			effs[index++] = parseEffect();
        		}
        	} else if(eventType == XmlPullParser.END_TAG) {
        		String elementName = parser.getName();
        		if (elementName.equals(endTag)) {
        			break;
        		}
        	}
        	
        	eventType = parser.next();
        }
		
		return effs;
	}
	
	private IEffect parseEffect() {
		String attribName = parser.getAttributeName(0);
		if (attribName.equals("enableBuilding")) {
			String[] buildingNames = { parser.getAttributeValue(0) };
			String[] holdingTypes = { parser.getAttributeValue(1) };
			return new EnableBuildingEffect(buildingNames, holdingTypes);
		} else if (attribName.equals("holdingStat")) {
			String stat = parser.getAttributeValue(0);
			float value = Float.valueOf(parser.getAttributeValue(1));
			return new HoldingStatEffect(stat, value);
		} else if (attribName.equals("addBuilding")) {
			String buildingName = parser.getAttributeValue(0);
			String holdingType = parser.getAttributeValue(1);
			return new AddBuildingEffect(buildingName, holdingType);
		} else if (attribName.equals("enableUnit")) {
			String unitName = parser.getAttributeValue(0);
			return new EnableUnitEffect(unitName);
		} else if (attribName.equals("addUnitMult")) {
			String unitName = parser.getAttributeValue(0);
			float mult = Float.valueOf(parser.getAttributeValue(1));
			return new AddUnitMultEffect(unitName, mult);
		} else if (attribName.equals("addHouseMult")) {
			int stat = Integer.valueOf(parser.getAttributeValue(0));
			float mult = Float.valueOf(parser.getAttributeValue(1));
			return new AddHouseMultEffect(stat, mult);
		} else if (attribName.equals("addBuildingMult")) {
			String buildingName = parser.getAttributeValue(0);
			int stat = Integer.valueOf(parser.getAttributeValue(1));
			float mult = Float.valueOf(parser.getAttributeValue(2));
			return new AddBuildingMultEffect(buildingName, stat, mult);
		} else if (attribName.equals("addGoodMult")) {
			int good = Integer.valueOf(parser.getAttributeValue(0));
			int stat = Integer.valueOf(parser.getAttributeValue(1));
			float mult = Float.valueOf(parser.getAttributeValue(2));
			return new AddGoodMultEffect(good, stat, mult);
		} else if (attribName.equals("addProduction")) {
			String[] in = new String[0];
			String[] out = new String[0];
			
			int countAttributes = parser.getAttributeCount();
			String holdingType = "";
				
			for (int i = 0; i < countAttributes; ++i) {
				if (parser.getAttributeName(i).equals("holdingType")) {
					holdingType = parser.getAttributeValue(i);
				} else if (parser.getAttributeName(i).equals("in")) {
					in = new String[1];
					in[0] = parser.getAttributeValue(i);
				} else if (parser.getAttributeName(i).equals("addProduction")) {
					out = new String[1];
					out[0] = parser.getAttributeValue(i);
				}
			}
			
			return new AddProductionEffect(in, out, holdingType);
		} else {
			throw new RuntimeException("XMLTechnologyTreeFactory::parseEffect() error: Unknown effect type " + attribName);
		}
	}
	
	
}
