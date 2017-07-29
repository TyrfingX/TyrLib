package com.tyrfing.games.id18.model.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3722478395332021523L;

	public static final int MESSAGE_REQUEST_ACTION = 0;
	public static final int MESSAGE_REQUEST_PLAYER = 1;
	
	public static interface IConstantMessages {
		public static final NetworkMessage REQUEST_ACTION = new NetworkMessage(MESSAGE_REQUEST_ACTION);
		public static final NetworkMessage REQUEST_PLAYER = new NetworkMessage(MESSAGE_REQUEST_PLAYER);
	}
	
	private int message;
	private List<Serializable> params;
	
	public NetworkMessage(int message, Serializable... params) {
		this.message = message;
		this.params = new ArrayList<Serializable>(Arrays.asList(params));
	}
	
	public List<Serializable> getParams() {
		return params;
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
