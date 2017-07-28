package com.tyrfing.games.tyrlib3.resource;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.main.Media;

public class Resource implements ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9061744650048498507L;
	private List<ISaveable> saveables;
	
	private String uri;
	private String name;
	
	public Resource() {
		saveables = new ArrayList<ISaveable>();
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<ISaveable> getSaveables() {
		return saveables;
	}

	public void save() {
		Media.CONTEXT.serializeTo(this, uri, name);
	}

	public void load() {
		Resource resource = (Resource) Media.CONTEXT.deserializeFrom(uri, name);
		saveables.addAll(resource.getSaveables());
	}
}
