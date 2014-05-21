package com.tyrlib2.networking;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Network {

	private Server server;
	private Vector<Connection> connections = new Vector<Connection>();
	protected List<INetworkListener> listener;
	
	public Network() {
		this.listener = new ArrayList<INetworkListener>();
	}
	
	public void addListener(INetworkListener listener) {
		this.listener.add(listener);
	}
	
	public boolean isHost() {
		return server != null;
	}
	
	public boolean isClient() {
		return server == null;
	}
	
	public void host(int port){
		server = new Server(this, port);
	}
	
	public void connectTo(String serverName, int port) {
		try {
			Socket client = new Socket(serverName, port);
			System.out.println("Successfully connected to " + client.getRemoteSocketAddress());
			addConnection(client);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		for (int i = 0; i < connections.size(); ++i) {
			connections.get(i).send(s);
		}
	}

	public void send(Serializable s, int connection) {
		connections.get(connection).send(s);
	}
	
	public void flush() {
		for (int i = 0; i < connections.size(); ++i) {
			connections.get(i).flush();
		}
	}
	
	public void receiveData(Connection c, Object o) {
		for (int i = 0; i <listener.size(); ++i) {
			listener.get(i).onReceivedData(c, o);
		}
	}
	
	/*
	protected byte[] toByteArray(Serializable s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(s);
		  return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		  try {
		    if (out != null) {
		      out.close();
		    }
		  } catch (IOException ex) {
		  }
		  try {
		    bos.close();
		  } catch (IOException ex) {
		  }
		}
		
		return null;
	}
	
	protected Object toObject(byte[] byteArray) {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  Object o = in.readObject(); 
		  return o;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
		  try {
		    bis.close();
		  } catch (IOException ex) {
		  }
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		  }
		}
		
		return null;
	}
	
	*/
	
}
