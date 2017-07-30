package com.tyrfing.games.id17;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.main.Media;

public class WikiReader {
	
	private XmlPullParser parser;
	
	public WikiReader() {
		
	}
	
	public void read(String file) {
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	
	        factory.setValidating(false);
	        parser = factory.newPullParser();
	
	        InputStream raw = Media.CONTEXT.openAsset(file);
	        parser.setInput(raw, null);
	        
	        int eventType = parser.getEventType();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_TAG) {
	        		String elementName = parser.getName();
	        		if (elementName.equals("Entry")) {
	        			
	        			final String header = parser.getAttributeValue(0);
	        			final String content = parser.getAttributeValue(1);
	        			
	        			LinkManager.getInstance().registerLink(new ILink() {
							@Override
							public void onCall() {
								Mail back = World.getInstance().getMainGUI().mailboxGUI.getCurrentMail();
								House h = World.getInstance().getPlayerController().getHouse();
								Mail mail = new HeaderedMail(header, content, h, h);
								World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
								if (back != null) {
									mail.setBackMail(back);
								}
							}
	        			}, header);
	        		} 
	        	} 
	        	eventType = parser.next();
	        }
	        
        
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load Wiki " + file + " due to XmlPullParserException!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load Wiki " + file + " due to FileNotFountException!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load Wiki " + file + " due to IOException!");
		}
	}
}
