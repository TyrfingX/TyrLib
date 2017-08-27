package com.tyrfing.games.id18.model.tag;

import java.util.HashSet;
import java.util.Set;

import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.model.game.stats.IModifiable;

public enum Tag {
	FIRE, OIL;
	
	public static Set<Tag> getTags(IModifiable<StatModifier> modifiable) {
		Set<Tag> tags = new HashSet<Tag>();
		for (StatModifier modifier : modifiable.getModifiers()) {
			tags.addAll(modifier.getTags());
		}
		return tags;
	}
}
