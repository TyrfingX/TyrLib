package com.tyrfing.games.tyrlib3.model.game.stats;

import java.util.Map;

public interface IStatHolder<S extends Number> {
	public Map<Stat, S> getStats();
}
