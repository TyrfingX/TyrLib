package com.tyrlib2.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Connection extends Thread{
	
	private OutputStream outToOther;
	private ObjectOutputStream out;
	private InputStream inToOther;
	private ObjectInputStream in;
	private Socket socket;
	
	private boolean connectionOpen = true;
	private Network network;
	
	public boolean openToBroadcasts = false;
	
	public final int ID;
	
	public Connection(Socket socket, Network network) {
		this.socket = socket;
		this.network = network;
		this.ID = network.getClientCount();
		
        try {
			outToOther = socket.getOutputStream();
			out = new ObjectOutputStream(outToOther);
			inToOther = socket.getInputStream();
			in = new ObjectInputStream(inToOther);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public void send(Serializable s) {
		if (connectionOpen) {
			try {
				out.writeObject(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		while (connectionOpen) {
			try {
				Object o = in.readObject();
				network.receiveData(this, o);
			} catch (IOException e) {
				connectionOpen = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				connectionOpen = false;
			} 
		}
		
		network.connectionLost(this);
		
		
	}
	
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		connectionOpen = false;
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getServerName() {
		return socket.getInetAddress().getHostAddress();
	}
	
	public int getServerPort() {
		return socket.getPort();
	}
	
}
