package com.tyrfing.games.tyrlib3.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class Network {

	public static final int DEFAULT_TIMEOUT = 10000;
	
	private Server server;
	private List<Connection> connections = new Vector<Connection>();
	protected List<INetworkListener> listener;
	private boolean measure;
	private long sentBytes;
	private long receivedBytes;
	private boolean log;
	
	public Network() {
		this.listener = new ArrayList<INetworkListener>();
	}
	
	public void setLog(boolean log) {
		this.log = log;
	}
	
	public void addListener(INetworkListener listener) {
		this.listener.add(listener);
	}
	
	public void removeListener(INetworkListener listener) {
		this.listener.remove(listener);
	}
	
	public boolean isHost() {
		return server != null;
	}
	
	public boolean isClient() {
		return server == null && connections.size() > 0;
	}
	
	public void setMeasureBandwithUse(boolean state) {
		measure = state;
	}
	
	public long getSentBytes() {
		return sentBytes;
	}
	
	public long getReceivedBytes() {
		return receivedBytes;
	}
	
	public long pollSentBytes() {
		long sentBytesTmp = sentBytes;
		sentBytes = 0;
		return sentBytesTmp;
	}
	
	public long pollReceivedBytes() {
		long receivedBytesTmp = receivedBytes;
		receivedBytes = 0;
		return receivedBytesTmp;
	}
	
	public void host(int port){
		server = new Server(this, port);
	}
	
	public void connectTo(String serverName, int port) throws UnknownHostException, IOException {
		connectTo(serverName, port, DEFAULT_TIMEOUT);
	}
	
	public void connectTo(String serverName, int port, int timeout) throws UnknownHostException, IOException {
		Socket client = new Socket();
		client.connect(new InetSocketAddress(serverName, port), timeout);
		if (log) {
			System.out.println(Calendar.getInstance().getTime() + " Successfully connected to " + client.getRemoteSocketAddress());
		}
		addConnection(client);
	}
	
	public void addConnection(Socket socket) {
		Connection c = new Connection(socket, this);
		addConnection(c);
		for (int i = 0; i < listener.size(); ++i) {
			listener.get(i).onNewConnection(c);
		}
	}
	
	public void addConnection(Connection c) {
		connections.add(c);
		c.start();
	}
	
	public int getClientCount() {
		return connections.size();
	}
	
	public void broadcast(Serializable s) {
		
		if (log) {
			System.out.println(Calendar.getInstance().getTime() + " Sent Data: " + s.toString());
		}
		
		int countConnections = connections.size();
		
		if (countConnections > 0) {
			for (int i = 0; i < countConnections; ++i) {
				if (connections.get(i).openToBroadcasts) {
					if (measure) {
						sentBytes += Network.sizeOf(s);
					}
					connections.get(i).send(s);
				}
			}
		}
	}

	public void send(Serializable s, int connection) {
		send(s, connections.get(connection));
	}
	
	public void send(Serializable s, Connection connection) {
		
		if (log) {
			System.out.println(Calendar.getInstance().getTime() + " Sent Data: " + s.toString());
		}
		
		if (measure) {
			sentBytes += Network.sizeOf(s);
		}
		
		connection.send(s);
	}
	
	public void flush() {
		for (int i = 0; i < connections.size(); ++i) {
			connections.get(i).flush();
		}
	}
	
	public void receiveData(Connection c, Object o) {
		
		if (measure) {
			receivedBytes += Network.sizeOf((Serializable)o);
		}
		
		if (log) {
			System.out.println(Calendar.getInstance().getTime() +  " Received Data: " + o.toString());
		}
		
		for (int i = 0; i <listener.size(); ++i) {
			listener.get(i).onReceivedData(c, o);
		}
	}
	
	public void connectionLost(Connection c) {
		
		if (log) {
			System.out.println(Calendar.getInstance().getTime() + " Connection " + c.ID + " lost");
		}
		
		for (int i = 0; i <listener.size(); ++i) {
			listener.get(i).onConnectionLost(c);
		}
		
		connections.remove(c);
	}
	
    private static long sizeOf(Serializable obj) {
        try {
            CheckSerializedSize counter = new CheckSerializedSize();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(counter);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            return counter.getNBytes();
        } catch (Exception e) {
            // Serialization failed
            return -1;
        }
    }
    
    public void close() {
    	for (int i = 0; i < connections.size(); ++i) {
    		connections.get(i).close();
    	}
    	connections.clear();
    	
    	if (this.isHost()) {
    		server.close();
    		server = null;
    	} 
    }
    
    public Server getServer() {
    	return server;
    }

	
}
