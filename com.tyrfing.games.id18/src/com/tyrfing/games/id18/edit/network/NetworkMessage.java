package com.tyrfing.games.id18.edit.network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3722478395332021523L;

	public static final int MESSAGE_REQUEST_ACTION = 0;
	
	public static final NetworkMessage REQUEST_ACTION = new NetworkMessage(MESSAGE_REQUEST_ACTION);
	
	private int message;
	
	public NetworkMessage(int message) {
		this.message = message;
	}
	
	public int getMessage() {
		return message;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NetworkMessage) {
			return message == ((NetworkMessage) obj).message;
		}
		return super.equals(obj);
	}
}
