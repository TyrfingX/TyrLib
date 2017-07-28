package com.tyrfing.games.tyrlib3.edit.action;

import java.util.List;

public class AddAction<T> extends AAction {

	protected List<T> collection;
	protected T object;
	
	public AddAction(List<T> collection, T object) {
		this.collection = collection;
		this.object = object;
	}

	@Override
	public void undo() {
		collection.remove(object);
	}

	@Override
	public void execute() {
		collection.add(object);
	}
}
