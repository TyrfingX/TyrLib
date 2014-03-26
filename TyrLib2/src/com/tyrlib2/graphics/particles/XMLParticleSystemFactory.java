package com.tyrlib2.graphics.particles;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.main.Media;
import com.tyrlib2.util.Color;

public class XMLParticleSystemFactory implements IParticleSystemFactory {

	private ParticleSystem prototype;
	private Map<String, BasicParticleFactory> particleFactories;
	private XmlPullParser parser;
	
	public XMLParticleSystemFactory(XmlPullParser parser) {
		this.parser = parser;
		parse();
	}
	
	public XMLParticleSystemFactory(String fileName) {
		
		 try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setValidating(false);
	        parser = factory.newPullParser();

	        InputStream raw = Media.CONTEXT.openAsset(fileName);
	        parser.setInput(raw, null);
			 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		 parse();
	}
	
	private void parse() {
		try {
			 int eventType = parser.getEventType();
			 
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	        	 if(eventType == XmlPullParser.START_TAG) {
	        		 String elementName = parser.getName();
	        		 if (elementName.equals("ParticleSystem")) {
	        			 parseParticleSystem();
	        		 } else if (elementName.equals("Factories")) {
	        			 parseFactories();
	        		 } else if (elementName.equals("Emitters")) {
	        			 parseEmitters();
	        		 } else if (elementName.equals("Affectors")) {
	        			 parseAffectors();
	        		 }
	             } else if (eventType == XmlPullParser.END_TAG) {
	            	 String elementName = parser.getName();
	            	 if (elementName.equals("ParticleSystem")) {
	            		 // This is the end of the particle System description, abort
	            		 break;
	            	 }
	             }
	             eventType = parser.next();
	         }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ParticleSystem create() {
		return prototype.copy();
	}
	
	private void parseParticleSystem() {
		
		int maxParticles = 0;
		boolean simple = false;

		int countAttributes = parser.getAttributeCount();
		for (int i = 0; i < countAttributes; ++i) {
			if (parser.getAttributeName(i).equals("maxParticles")) {
				maxParticles = Integer.valueOf(parser.getAttributeValue(i));
			} else if (parser.getAttributeName(i).equals("simple")) {
				simple = Boolean.valueOf(parser.getAttributeValue(i));
			}
		}
		
		if (!simple) {
			prototype = new ComplexParticleSystem();
		} else {
			prototype = new SimpleParticleSystem();
		}
		prototype.setMaxParticles(maxParticles);
	}
	
	private void parseFactories() throws XmlPullParserException, IOException {
		particleFactories = new HashMap<String, BasicParticleFactory>();
		
		
		int index = 0;
		BasicParticleFactory factory = null;
		String name = "" + index;;
		
		int eventType = parser.next();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Factory")) {
					int countAttributes = parser.getAttributeCount();
					
					float lifeTime = 0;

					float size = 1;
					
					for (int i = 0; i < countAttributes; ++i) {
						String attributeName = parser.getAttributeName(i);
						
						if (attributeName.equals("name")) {
							name = parser.getAttributeValue(i);
						} else if (attributeName.equals("lifeTime")) {
							lifeTime = Float.valueOf(parser.getAttributeValue(i));
						} else if (attributeName.equals("size")) {
							size = Float.valueOf(parser.getAttributeValue(i));
						}	
					}
					
					factory = new BasicParticleFactory(lifeTime, size);
				} else if (elementName.equals("Material")) {
					String texture = null;
					Color color = new Color(1,1,1,1);
					
					int countAttributes = parser.getAttributeCount();
					
					for (int i = 0; i < countAttributes; ++i) {
						String attributeName = parser.getAttributeName(i);
						
						if (attributeName.equals("r")) {
							color.r = Float.valueOf(parser.getAttributeValue(i));
						} else if (attributeName.equals("g")) {
							color.g = Float.valueOf(parser.getAttributeValue(i));
						} else if (attributeName.equals("b")) {
							color.b = Float.valueOf(parser.getAttributeValue(i));
						} else if (attributeName.equals("a")) {
							color.a = Float.valueOf(parser.getAttributeValue(i));
						} else if (attributeName.equals("texture")) {
							texture = parser.getAttributeValue(i);
						} 
					}
					
					factory.setMaterial(new ParticleMaterial(texture, color));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Factories")) {
					// We are finished with this part
					break;
				} else if (elementName.equals("Factory")) {
					particleFactories.put(name, factory);
					index++;
					name = "" + index;
				}
			}
			
			eventType = parser.next();
		}
		
	}
	
	private void parseEmitters() throws XmlPullParserException, IOException {
		int eventType = parser.next();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Emitter")) {
					XMLEmitterFactory emitterFactory = new XMLEmitterFactory(parser, particleFactories);
					Emitter emitter = emitterFactory.create();
					prototype.addEmitter(emitter);
				} 
			} else if (eventType == XmlPullParser.END_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Emitters")) {
					break;
				} 
			}
			
			eventType = parser.next();
		}
	}
	
	private void parseAffectors() throws XmlPullParserException, IOException {
		int eventType = parser.next();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Affector")) {
					XMLAffectorFactory affectorFactory = new XMLAffectorFactory(parser);
					Affector affector = affectorFactory.create();
					prototype.addAffector(affector);
				} 
			} else if (eventType == XmlPullParser.END_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("Affectors")) {
					break;
				} 
			}
			
			eventType = parser.next();
		}
	}

}
