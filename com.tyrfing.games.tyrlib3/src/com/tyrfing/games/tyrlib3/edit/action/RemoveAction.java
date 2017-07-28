package com.tyrfing.games.tyrlib3.edit.action;

import java.util.List;

public class RemoveAction<T> extends AAction {

	protected List<T> collection;
	protected T object;
	
	public RemoveAction(List<T> collection, T object) {
		this.collection = collection;
		this.object = object;
	}

	@Override
	public void undo() {
		collection.add(object);
	}

	@Override
	public void execute() {
		collection.remove(object);
	}
	
	@Override
	public boolean canExecute() {
		return collection.contains(object);
	}
}
