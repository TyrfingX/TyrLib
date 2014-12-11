package com.tyrlib2.graphics.particles;

import java.io.IOException;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class XMLEmitterFactory implements IEmitterFactory {

	private XmlPullParser parser;
	private Map<String, BasicParticleFactory> factories;
	private Emitter prototype;
	
	public XMLEmitterFactory(XmlPullParser parser, Map<String, BasicParticleFactory> factories) {
		this.parser = parser;
		this.factories = factories;
		parse();
	}
	
	private void parse() {
		try {
			int eventType = parser.getEventType();
		
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String elementName = parser.getName();
					if (elementName.equals("Emitter")) {
						
						BasicParticleFactory factory = null;
						int amount = 0;
						float interval = 0;
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attributeName = parser.getAttributeName(i);
							if (attributeName.equals("factory")) {
								factory = factories.get(parser.getAttributeValue(i));
							} else if (attributeName.equals("amount")) {
								amount = Integer.valueOf(parser.getAttributeValue(i));
							} else if (attributeName.equals("interval")) {
								interval = Float.valueOf(parser.getAttributeValue(i));
							}
						}
	
						prototype = new Emitter(factory);
						prototype.setAmount(amount);
						prototype.setInterval(interval);
						prototype.attachTo(new SceneNode(new Vector3()));
					} else if (elementName.equals("Position")) {
						
						Vector3 position = new Vector3();
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);

							if (attribute.equals("x")) {
								position.x = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("y")) {
								position.y = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("z")) {
								position.z = Float.valueOf(parser.getAttributeValue(i));
							} 
						}
						
						prototype.getParent().setRelativePos(position);
						
					} else if (elementName.equals("Velocity")) {
						
						Vector3 velocity = new Vector3();
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);

							if (attribute.equals("x")) {
								velocity.x = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("y")) {
								velocity.y = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("z")) {
								velocity.z = Float.valueOf(parser.getAttributeValue(i));
							} 
						}
						
						prototype.setVelocity(velocity);
						
					} else if (elementName.equals("RandomVelocity")) {
						
						Vector3 rndVelocity = new Vector3();
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);

							if (attribute.equals("x")) {
								rndVelocity.x = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("y")) {
								rndVelocity.y = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("z")) {
								rndVelocity.z = Float.valueOf(parser.getAttributeValue(i));
							} 
						}
						
						prototype.setRandomVelocity(rndVelocity);
						
					} else if (elementName.equals("RandomPosition")) {
						Vector3 rndPosition = new Vector3();
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attribute = parser.getAttributeName(i);

							if (attribute.equals("x")) {
								rndPosition.x = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("y")) {
								rndPosition.y = Float.valueOf(parser.getAttributeValue(i));
							} else if (attribute.equals("z")) {
								rndPosition.z = Float.valueOf(parser.getAttributeValue(i));
							} 
						}
						
						prototype.setRandomPos(rndPosition);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					String elementName = parser.getName();
					if (elementName.equals("Emitter")) {
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
	public Emitter create() {
		return prototype.copy();
	}

}
