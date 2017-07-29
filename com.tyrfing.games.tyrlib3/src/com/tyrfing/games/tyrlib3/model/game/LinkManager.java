package com.tyrfing.games.tyrlib3.model.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LinkManager {
	private Map<String, List<ILink>> linkMap = new HashMap<String, List<ILink>>(); 
	private static LinkManager instance;
	
	public static LinkManager getInstance() {
		if (instance == null) {
			instance = new LinkManager();
		}
		
		return instance;
	}
	
	public void registerLink(ILink link, String event) {
		List<ILink> listeners = null;
		if (!linkMap.containsKey(event)) {
			listeners = new ArrayList<ILink>();
			linkMap.put(event, listeners);
		} else {
			listeners = linkMap.get(event);
		}
		listeners.add(link);
	}
	
	public void removeLink(ILink link, String event) {
		if (linkMap.containsKey(event)) {
			List<ILink>listeners = new ArrayList<ILink>();
			listeners.remove(link);
		} 
	}
	
	public void call(String event) {
		if (linkMap.containsKey(event)) {
			List<ILink> listeners = linkMap.get(event);
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onCall();
			}
		}
	}
}
