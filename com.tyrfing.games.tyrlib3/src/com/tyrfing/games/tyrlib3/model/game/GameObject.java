package com.tyrfing.games.tyrlib3.model.game;

import java.util.UUID;

import com.tyrfing.games.tyrlib3.model.IUUID;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public abstract class GameObject implements IUUID, ISaveable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5360261207692144426L;

	private UUID uuid;
	
	public GameObject() {
		uuid = UUID.randomUUID();
	}
	
	@Override
	public UUID getUUID() {
		return uuid;
	}
}
