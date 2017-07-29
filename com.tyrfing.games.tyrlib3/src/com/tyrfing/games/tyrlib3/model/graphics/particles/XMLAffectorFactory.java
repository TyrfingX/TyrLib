package com.tyrfing.games.tyrlib3.model.graphics.particles;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class XMLAffectorFactory implements IAffectorFactory {

	
	private Affector prototype;
	
	/**
	 * @param parser	The XML Pull parser form an object using Affectors (ie ParticleSystems)
	 * 					The Affector will be constructed from the parser and all data
	 * 					until the first </Affector> Tag will be used to construct it.
	 */
	public XMLAffectorFactory(XmlPullParser parser) {
		try {
			int eventType = parser.getEventType();
		
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String elementName = parser.getName();
					if (elementName.equals("Affector")) {
						
						String type = parser.getAttributeValue(0);
						
						if (type.equals("ColorChanger")) {
							prototype = new ColorChanger();
						} else if (type.equals("ForceAffector")) {
							prototype = new ForceAffector();
						} else if (type.equals("SizeChanger")) {
							prototype = new SizeChanger();
						} else if (type.equals("RotationChanger")) {
							prototype = new RotationChanger();
						}
						
					} else if (elementName.equals("HomogenField")) {
						
						float x = 0;
						float y = 0;
						float z = 0;
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);
							if (attribute.equals("x")) {
								x = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("y")) {
								y = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("z")) {
								z = Float.valueOf(parser.getAttributeValue(i));
							}
						}
						
						((ForceAffector)prototype).setForce(new Vector3F(x,y,z));
						
					} else if (elementName.equals("RadialField")) {
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);
							if (attribute.equals("radialDependency")) {
								float radialDependency = Float.valueOf(parser.getAttributeValue(i));
								((ForceAffector)prototype).setRadialDependency(radialDependency);
							} else if (attribute.equals("power")) {
								float power = Float.valueOf(parser.getAttributeValue(i));
								((ForceAffector)prototype).setPower(power);
							}
						}
					} else if (elementName.equals("ColorChange")) {
						
						Color color = new Color(0,0,0,0);
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);
							
							
							if (attribute.equals("r")) {
								color.r = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("g")) {
								color.g = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("b")) {
								color.b = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("a")) {
								color.a = Float.valueOf(parser.getAttributeValue(i));
							} 
						}
						
						((ColorChanger)prototype).setColorChange(color);
				} else if (elementName.equals("SizeChange")) {
					
					float sizeChange = 0;
					
					int countAttributes = parser.getAttributeCount();
					for (int i = 0; i < countAttributes; ++i) {
						String attribute = parser.getAttributeName(i);
						if (attribute.equals("value")) {
							sizeChange = Float.valueOf(parser.getAttributeValue(i));
						} 
					}
					
					((SizeChanger)prototype).setSizeChange(sizeChange);
				} else if (elementName.equals("RotationChange")) {
					
					float rotationChange = 0;
					
					int countAttributes = parser.getAttributeCount();
					for (int i = 0; i < countAttributes; ++i) {
						String attribute = parser.getAttributeName(i);
						if (attribute.equals("value")) {
							rotationChange = Float.valueOf(parser.getAttributeValue(i));
						} 
					}
					
					((RotationChanger)prototype).setSizeChange(rotationChange);	
				} else if (elementName.equals("MaxLifeTime")) {
					float maxLifeTime = Float.valueOf(parser.getAttributeValue(0));
					prototype.setLifeTimeMax(maxLifeTime);
				} else if (elementName.equals("MinLifeTime")) {
					float minLifeTime = Float.valueOf(parser.getAttributeValue(0));
					prototype.setLifeTimeMin(minLifeTime);
				}
					
				} else if (eventType == XmlPullParser.END_TAG) {
					String elementName = parser.getName();
					if (elementName.equals("Affector")) {
						break;
					} 
				}
				
				eventType = parser.next();
			}
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Affector create() {
		return prototype.copy();
	}

}
