package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	/*
	 * Note that these variables lack modifiers on purpose; this is to
	 * 	limit direct accesses to them to only those classes contained
	 * 	within this package
	 */
	
	//set directly in constructor
	static InetAddress serverInetAddress;
	static int serverPort;
	static int backlog = 50;
	
	//set in connectToServer()
	static Socket serverSock = null;
	
	//written to by sender and receiver thread
	static int localPort = -1; //is -1 until bound
	static int remotePort = 0;
	static InetAddress localInetAddress = null;
	static InetAddress remoteInetAddress = null;
	
	//create client with the default backlog.
	public Client(String serverAddr, int port){
		try{
			Client.serverInetAddress = InetAddress.getByName(serverAddr);
			Client.serverPort = port;
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		}
	}
	
	Socket connectToServer(){
		try {
			serverSock = new Socket(serverInetAddress, serverPort);
			System.out.println("Client: socket created");
			BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
			outStream.flush(); //This is necessary for some reason. Not entirely sure why, but the whole thing doesn't work without it.
			
		} catch (UnknownHostException e) {
			System.out.println("Client: unknown host exception connecting to server.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client: IO exception connecting to server.");
			e.printStackTrace();
		}
		return serverSock;
	}
	
	//start the sending side of the client.
	void startSenderThread(String destIPAddress, int destPortNum){
		System.out.println("Opening connection to another client.");
		ClientSenderThread testClientSender = new ClientSenderThread(destIPAddress, destPortNum);
		testClientSender.start();
	}
	
	//start the listening side of the client.
	void startListenerThread(){
		System.out.println("Opening client listener socket.");
		ClientListenerThread testClientListener = new ClientListenerThread();
		testClientListener.start();
	}

	//Returns the port number of the target host this socket is connected to, or 0 if this socket is not yet connected.
	int getPort(){
		return Client.remotePort;
	}
	//Returns the local port this socket is bound to, or -1 if the socket is unbound.
	int getLocalPort(){
		return Client.localPort;
	}
	
	//Returns the IP address of the target host this socket is connected to, or null if this socket is not yet connected.
	InetAddress getInetAddress(){
		return Client.remoteInetAddress;
	}
	
	//Returns the local IP address this socket is bound to, or null if the socket is unbound.
	InetAddress getLocalAddress(){
		return Client.localInetAddress;
	}
	
}