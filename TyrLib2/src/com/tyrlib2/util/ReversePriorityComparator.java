package com.tyrlib2.util;

import java.util.Comparator;

public class ReversePriorityComparator implements Comparator<IPrioritizable> {

	@Override
	public int compare(IPrioritizable object1, IPrioritizable object2) {
		if (object1.getPriority() < object2.getPriority()) return 1;
		if (object1.getPriority() > object2.getPriority()) return -1;
		return 0;
	}
	
	

}
