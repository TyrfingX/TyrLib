package com.tyrlib2.graphics.particles;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

public class XMLParticleSystemFactory implements IParticleSystemFactory {

	public XMLParticleSystemFactory(String fileName, Context context) {
		try {
			XmlPullParser parser = context.getResources().getAssets().openXmlResourceParser(fileName);
			 int eventType = parser.getEventType();
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	        	 if(eventType == XmlPullParser.START_TAG) {
	        		 String elementName = parser.getName();
	        		 if (elementName.equals("Graph")) {
	        		
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
		return null;
	}

}
