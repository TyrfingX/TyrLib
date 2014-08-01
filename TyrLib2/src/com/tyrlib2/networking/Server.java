package com.tyrlib2.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
	
	private ServerSocket serverSocket;
	private boolean acceptConnections = true;
	private Network network;
	
	public Server(Network network, int port){
		
		this.network = network;
		
	   try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("Error when creating ServerSocket! Failed to open port " + port + "!");
		}
	      
	    this.start();
	}
	
	public int getPort() {
		return serverSocket.getLocalPort();
	}
	
	public String getServerName() {
		return serverSocket.getInetAddress().getHostAddress();
	}
	
	public void close() {
		try {
			serverSocket.close();
			acceptConnections = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setAcceptConnections(boolean state) {
		this.acceptConnections = state;
	}
	
	@Override
	public void run() {
		while(acceptConnections) {
			 try {
				Socket socket = serverSocket.accept();
				System.out.println("Succesfully accepted a connection: " + socket.getRemoteSocketAddress());
				network.addConnection(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
