package com.tyrfing.games.id18.model.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AModifier implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5836893121592936614L;
	private String name;
	private List<Tag> tags;
	
	public AModifier(String name) {
		this.name = name;
		tags = new ArrayList<Tag>();
	}
	
	public AModifier(String name, Tag...tags) {
		this(name);
		Collections.addAll(this.tags, tags);
	}
	
	public String getName() {
		return name;
	}
	
	public List<Tag> getTags() {
		return tags;
	}
}
