package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	/*
	 * Note that these variables lack modifiers on purpose; this is to
	 * 	limit direct accesses to them to only those classes contained
	 * 	within this package.
	 */
	//set directly in constructor
	static int serverPort;
	static InetAddress serverInetAddress;
	static InputStream receivedDataStream; 			//receives data from 'toReceivedData'. Accessible by user.
	static PipedOutputStream toReceivedDataStream;	//written to by ClientListenerThread, pipes to 'receivedData'.
	static PipedInputStream sendDataStream; //written to by ClientSenderThread, holds data pending write across the network.
	static OutputStream toSendDataStream; 	//sends data to sendDataStream. Accessible by user.
	
	//set in connectToServer()
	static Socket serverSock = null;
	
	//set in startListenerThread() and startSenderThread() respectively.
	static ClientListenerThread clientListener = null;
	static ClientSenderThread clientSender = null;
	
	//Written to by sender and receiver thread.
	static int localPort = -1; //is -1 until bound
	static int remotePort = 0;
	static InetAddress localInetAddress = null;
	static InetAddress remoteInetAddress = null;
	
	
	/* 
	 *	Begin constructors section 
	 */
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on an ephemeral port. 
	public Client(String serverAddr, int serverPort){
		try{
			Client.serverInetAddress = InetAddress.getByName(serverAddr);
			Client.serverPort = serverPort;
			Client.toReceivedDataStream = new PipedOutputStream();
			Client.receivedDataStream = new PipedInputStream(toReceivedDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
				
		//initial setup
		Client.serverSock = connectToServer();
		startListenerThread(Client.toReceivedDataStream);
	}
	
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on the port specified by listenerPort. 
	public Client(String serverAddr, int serverPort, int clientListenerPort){
		try{
			Client.serverInetAddress = InetAddress.getByName(serverAddr);
			Client.serverPort = serverPort;
			Client.toReceivedDataStream = new PipedOutputStream();
			Client.receivedDataStream = new PipedInputStream(toReceivedDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
		
		//initial setup
		Client.serverSock = connectToServer(clientListenerPort);
		startListenerThread(Client.toReceivedDataStream); //uses the local server socket IP and port
	}
	
	/*
	 * Create client listener and sender, and attempt to connect to the client listener at destAddr:destPort.
	 * 	Uses ephemeral port and localhost for the ClientListenerThread.
	 */
	public Client(String serverAddr, int serverPort, String destAddr, int destPort){
		try{
			Client.serverInetAddress = InetAddress.getByName(serverAddr);
			Client.serverPort = serverPort;
			Client.toReceivedDataStream = new PipedOutputStream();
			Client.receivedDataStream = new PipedInputStream(toReceivedDataStream);
			Client.toSendDataStream = new PipedOutputStream();
			Client.sendDataStream = new PipedInputStream((PipedOutputStream)toSendDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
		
		//initial setup
		Client.serverSock = connectToServer();
		startSenderThread(sendDataStream, destAddr, destPort);
		startListenerThread(Client.toReceivedDataStream);
	}
	
	
	/*
	 * Begin functional methods section
	 */
	//establishes a connection to the proxy server and returns a copy of the Socket.
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
	
	//establishes a connection to the proxy server and returns a copy of the Socket. Binds to the local port specified by clientListenerPort.
	Socket connectToServer(int clientListenerPort){
		try {
			serverSock = new Socket(serverInetAddress, serverPort, InetAddress.getLocalHost(), clientListenerPort);
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
	void startSenderThread(InputStream pendingData, String destIPAddress, int destPortNum){
		System.out.println("Opening connection to another client.");
		Client.clientSender = new ClientSenderThread(pendingData, destIPAddress, destPortNum);
		Client.clientSender.start();
	}
	
	//start the listening side of the client. Writes received data to the InputStream associated with 'outStream'.
	void startListenerThread(OutputStream outStream){
		System.out.println("Opening client listener socket.");
		Client.clientListener = new ClientListenerThread(outStream);
		Client.clientListener.start();
	}

	
	/*
	 * Begin interface methods section
	 */
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
	
	//Returns the client-side input stream. This stream contains only payload.
	InputStream getInputStream(){
		return Client.receivedDataStream;
	}
	
	//writes arbitrary bytes to the remote end of the client socket.
	void write(byte[] payload){
		try {
			toSendDataStream.write(payload);
			toSendDataStream.flush();
		} catch (IOException e) {
			System.out.println("Client: IO exception writing data to server.");
			e.printStackTrace();
		}
	}
	
	//writes arbitrary Strings to the remote end of the client socket.
	void write(String payload){
		try {
			this.write(payload.getBytes("UTF-8")); //Simply uses the other Write() method of the Client class.
		} catch (UnsupportedEncodingException e) {
			System.out.println("Client: problem encoding bytes into UTF-8 format.");
			e.printStackTrace();
		}
	}
	
}