package com.tyrlib2.graphics.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;


public class TextureAtlas {
	
	private Texture texture;
	private Map<String, TextureRegion> regions;
	
	public TextureAtlas(Texture texture) {
		this.texture = texture;
		regions = new HashMap<String, TextureRegion>();
	}
	
	public void addRegion(String name, TextureRegion region) {
		regions.put(name, region);
	}
	
	public TextureRegion getRegion(String name) {
		return regions.get(name);
	}

	public Texture getTexture() {
		return texture;
	}
	
	public static TextureAtlas fromXMLFile(String fileName) {
		return fromXMLFile(fileName, 1);
	}
	
	public static TextureAtlas fromXMLFile(String fileName, float  atlasScale) {
		
		TextureAtlas atlas = null;
		
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setValidating(false);
	        XmlPullParser parser = factory.newPullParser();
	
	        InputStream raw = Media.CONTEXT.openAsset(fileName);
	        parser.setInput(raw, null);
			
			int eventType = parser.getEventType();

			Texture texture = null;
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String elementName = parser.getName();
					if (elementName.equals("Atlas")) {
						
						String atlasName = null;
						String textureName = null;
						
						int countAttributes = parser.getAttributeCount();
						for (int i = 0; i < countAttributes; ++i) {
							String attributeName = parser.getAttributeName(i);
							if (attributeName.equals("texture")) {
								textureName = parser.getAttributeValue(i);
							} else if (attributeName.equals("name")) {
								atlasName = parser.getAttributeValue(i);
							}
						}
						
						texture = TextureManager.getInstance().getTexture(textureName);
						atlas = new TextureAtlas(texture);
						
						SceneManager.getInstance().addTextureAtlas(atlasName, atlas);
					} else if (elementName.equals("Region")) {
						int countAttributes = parser.getAttributeCount();
						
						String name = null;
						float x = 0;
						float y = 0;
						float w = 0;
						float h = 0;
						
						for (int i = 0; i < countAttributes; ++i) {
							String attributeName = parser.getAttributeName(i);
							if (attributeName.equals("name")) {
								name = parser.getAttributeValue(i);
							} else if (attributeName.equals("x")) {
								x = Float.valueOf(parser.getAttributeValue(i)) * atlasScale;
							} else if (attributeName.equals("y")) {
								y = Float.valueOf(parser.getAttributeValue(i)) * atlasScale;
							} else if (attributeName.equals("w")) {
								w = Float.valueOf(parser.getAttributeValue(i)) * atlasScale;
							} else if (attributeName.equals("h")) {
								h = Float.valueOf(parser.getAttributeValue(i)) * atlasScale;
							}
						}
						
						TextureRegion region = new TextureRegion(texture.getSize().x, texture.getSize().y, x,y,w,h);
						atlas.addRegion(name, region);
					} 
				} 
				eventType = parser.next();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

	
		return atlas;
	}
}
