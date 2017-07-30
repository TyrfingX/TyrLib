package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class RequestDisplayData extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8262644395951904792L;
	
	public static final byte NONE = 0;
	public static final byte HOLDING_DATA = 1;
	public static final byte TECH_DATA = 2;
	public static final byte HOUSE_DATA = 3;
	public static final byte LAW_STATS = 4;
	public static final byte ARMY_STATS = 5;
	
	public final byte type;
	public final short param;
	
	public RequestDisplayData(byte type, short param) {
		this.type = type;
		this.param = param;
	}
	
	@Override
	public void process(Connection c) {
		NetworkController hc = World.getInstance().getPlayer(c);
		hc.displayRequest = type;
		hc.displayParam = param;
		World.getInstance().passedUpdateTime = World.SERVER_UPDATE;
	}
}
