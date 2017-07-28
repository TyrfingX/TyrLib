package com.tyrfing.games.tyrlib3.graphics.renderer;

import java.util.Set;
import java.util.TreeSet;

public class PreprocessorOptions {
	private Set<String> options = new TreeSet<String>();
	
	public void define(String option) {
		options.add(option);
	}
	
	public void undefine(String option) {
		options.remove(option);
	}
	
	public boolean isDefined(String option) {
		return options.contains(option);
	}
	
	public PreprocessorOptions copy() {
		PreprocessorOptions copy = new PreprocessorOptions();
		copy.options = new TreeSet<String>(this.options);
		return copy;
	}
}
