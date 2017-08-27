package com.tyrfing.games.tyrlib3.model.game.stats;

import java.util.List;

public interface IModifiable <T extends AModifier>{
	public List<T> getModifiers();
}
