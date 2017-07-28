package com.tyrfing.games.id18.model.tag;

import java.util.HashSet;
import java.util.Set;

public enum Tag {
	FIRE, OIL;
	
	public static Set<Tag> getTags(IModifiable modifiable) {
		Set<Tag> tags = new HashSet<Tag>();
		for (AModifier modifier : modifiable.getModifiers()) {
			tags.addAll(modifier.getTags());
		}
		return tags;
	}
}
