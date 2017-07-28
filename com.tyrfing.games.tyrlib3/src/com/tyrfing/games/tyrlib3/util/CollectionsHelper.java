package com.tyrfing.games.tyrlib3.util;

import java.util.Collection;

public class CollectionsHelper {
	public static <T> void addIfNotNull(Collection<T> collection, T object) {
		if (object != null) {
			collection.add(object);
		}
	}
}
