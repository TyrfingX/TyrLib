package com.tyrlib2.graphics.particles;

import org.xmlpull.v1.XmlPullParser;

public class XMLAffectorFactory implements IAffectorFactory {

	/**
	 * @param parser	The XML Pull parser form an object using Affectors (ie ParticleSystems)
	 * 					The Affector will be constructed from the parser and all data
	 * 					until the first </Affector> Tag will be used to construct it.
	 */
	public XMLAffectorFactory(XmlPullParser parser) {
		
	}
	
	@Override
	public Affector create() {
		return null;
	}

}
