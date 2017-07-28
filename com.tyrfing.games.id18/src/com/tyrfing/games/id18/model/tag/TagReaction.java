package com.tyrfing.games.id18.model.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id18.model.unit.StatModifier;

public class TagReaction {
	private List<Tag> reactingTags;
	private List<Tag> removedModifiersWithTag;
	private List<StatModifier> addedModifiers;

	public TagReaction(List<Tag> reactingTags, List<Tag> removedModifiersWithTag, List<StatModifier> addedModifiers) {
		this.reactingTags = reactingTags;
		this.removedModifiersWithTag = removedModifiersWithTag;
		this.addedModifiers = addedModifiers;
	}
	
	public List<Tag> getReactingTags() {
		return reactingTags;
	}
	
	public List<Tag> getRemovedModifiersWithTag() {
		return removedModifiersWithTag;
	}
	
	public List<StatModifier> getAddedModifiers() {
		return addedModifiers;
	}
	
	public boolean isApplicable(IModifiable modifiable, AModifier modifier) {
		if (Collections.disjoint(getReactingTags(), modifier.getTags())) {
			return false;
		}
		
		if (Tag.getTags(modifiable).containsAll(getReactingTags())) {
			return true;
		}
		
		return false;
	}
	
	public static List<TagReaction> createDefaultTagReactionSystem() {
		List<TagReaction> tagReactions = new ArrayList<TagReaction>();
		
		tagReactions.add(new TagReaction(Arrays.asList(Tag.FIRE, Tag.OIL), Arrays.asList(Tag.OIL), Arrays.asList(StatModifier.BURNING)));
		
		return tagReactions;
	}
}
