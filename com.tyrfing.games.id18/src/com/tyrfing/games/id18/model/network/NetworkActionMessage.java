package com.tyrfing.games.id18.model.network;

import java.io.Serializable;

public class NetworkActionMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2728242387209798281L;

	public static final int MESSAGE_END_TURN_ACTION = 2;
	public static final int MESSAGE_MOVE_ACTION = 3;
	public static final int MESSAGE_APPLY_AFFECTOR_ACTION = 3;
	
	public static interface IConstantMessages {
		public static final NetworkActionMessage END_TURN_ACTION = new NetworkActionMessage(MESSAGE_END_TURN_ACTION);
	}
	
	public NetworkActionMessage(int message, Serializable... params) {
		super(message, params);
	}

}
