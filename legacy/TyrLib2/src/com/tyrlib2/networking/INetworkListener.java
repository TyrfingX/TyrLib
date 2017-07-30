package com.tyrlib2.networking;

public interface INetworkListener {
	public void onNewConnection(Connection c);
	public void onConnectionLost(Connection c);
	public void onReceivedData(Connection c, Object o);
}
