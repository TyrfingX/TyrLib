package com.tyrfing.games.id17.names;

import java.util.ArrayList;
import java.util.List;

public class NameGen {
	
	private final String[] syllabies;
	private final String[] allFirstSyllabies;
	private final int min;
	private final int max;
	private List<String> generated = new ArrayList<String>();
	
	public NameGen(String[] prefixes, String[] syllabies, int min, int max) {
		
		this.min = min;
		this.max = max;
		
		this.syllabies = syllabies;
		allFirstSyllabies = new String[prefixes.length+syllabies.length];
		
		for (int i = 0; i < prefixes.length; ++i) {
			allFirstSyllabies[i] = prefixes[i];
		}
		
		for (int i = 0; i < syllabies.length; ++i) {
			allFirstSyllabies[i+prefixes.length] = syllabies[i];
		}
	}
	
	public String generateNext() {
		
		String name = allFirstSyllabies[(int)(Math.random()*allFirstSyllabies.length)];;
		
		int countSyllabies = (int)(Math.random()*(max-min))+min;
		for (int i = 1; i < countSyllabies; ++i) {
			name += syllabies[(int)(Math.random()*syllabies.length)];
		}
		
		char c = name.charAt(0);
		name = name.substring(1);
		name = String.valueOf(c).toUpperCase() + name;
		
		if (!generated.contains(name)) {
			generated.add(name);
			return name;
		} else {
			return generateNext();
		}
	
	}
	
	public void forgetName(String name) {
		generated.remove(name);
	}
}
