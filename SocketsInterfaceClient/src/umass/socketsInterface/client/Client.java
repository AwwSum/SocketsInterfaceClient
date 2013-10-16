package umass.socketsInterface.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements UMassSocketsInterfaceClient{

	/*
	 * Note that these variables lack modifiers on purpose; this is to
	 * 	limit direct accesses to them to only those classes contained
	 * 	within this package.
	 */
	//set directly in constructor
	int serverPort;
	InetAddress serverInetAddress;
	InputStream receivedDataStream = null; 	//receives data from 'toReceivedData'. Accessible by user.
	PipedOutputStream toReceivedDataStream;	//written to by ClientListenerThread, pipes to 'receivedData'.
	PipedInputStream sendDataStream; //written to by ClientSenderThread, holds data pending write across the network.
	OutputStream toSendDataStream = null; 	//sends data to sendDataStream. Accessible by user.
	
	//set in connectToServer()
	Socket serverSock = null;
	
	//set in startListenerThread() and startSenderThread() respectively.
	ClientListenerThread clientListener = null;
	ClientSenderThread clientSender = null;
	
	//Written to by sender and receiver thread.
	int localPort = -1; //is -1 until bound
	int remotePort = 0;
	InetAddress localInetAddress = null;
	InetAddress remoteInetAddress = null;
	
	
	/* 
	 *	Begin constructors section 
	 */
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on an ephemeral port. 
	public Client(String serverAddr, int serverPort){
		try{
			this.serverInetAddress = InetAddress.getByName(serverAddr);
			this.serverPort = serverPort;
			this.toReceivedDataStream = new PipedOutputStream();
			this.receivedDataStream = new PipedInputStream(toReceivedDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
		
		//initial setup
		this.serverSock = connectToServer();
		startListenerThread(this.toReceivedDataStream, this);
	}
	
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on the port specified by listenerPort. 
	public Client(String serverAddr, int serverPort, int clientListenerPort){
		try{
			this.serverInetAddress = InetAddress.getByName(serverAddr);
			this.serverPort = serverPort;
			this.toReceivedDataStream = new PipedOutputStream();
			this.receivedDataStream = new PipedInputStream(toReceivedDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
		
		//initial setup
		this.serverSock = connectToServer(clientListenerPort);
		startListenerThread(this.toReceivedDataStream, this); //uses the local server socket IP and port
	}
	
	/*
	 * Create sender, and attempt to connect to the client listener at destAddr:destPort.
	 */
	public Client(String serverAddr, int serverPort, String destAddr, int destPort){
		try{
			this.serverInetAddress = InetAddress.getByName(serverAddr);
			this.serverPort = serverPort;
			this.toReceivedDataStream = new PipedOutputStream();
			this.receivedDataStream = new PipedInputStream(toReceivedDataStream);
			this.toSendDataStream = new PipedOutputStream();
			this.sendDataStream = new PipedInputStream((PipedOutputStream)toSendDataStream);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Unable to set up internal streams.");
			System.exit(-1);
		}
		
		//initial setup
		this.serverSock = connectToServer();
		startSenderThread(sendDataStream, destAddr, destPort, this);
		//startListenerThread(Client.toReceivedDataStream);
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
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Client: IO exception connecting to server.");
			e.printStackTrace();
			System.exit(-1);
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
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Client: IO exception connecting to server.");
			e.printStackTrace();
			System.exit(-1);
		}
		return serverSock;
	}
	
	//start the sending side of the client.
	void startSenderThread(InputStream pendingData, String destIPAddress, int destPortNum, Client client){
		System.out.println("Opening connection to another client.");
		this.clientSender = new ClientSenderThread(pendingData, destIPAddress, destPortNum, this);
		this.clientSender.start();
	}
	
	//start the listening side of the client. Writes received data to the InputStream associated with 'outStream'.
	void startListenerThread(OutputStream outStream, Client referenceToThis){
		System.out.println("Opening client listener socket.");
		this.clientListener = new ClientListenerThread(outStream, this);
		this.clientListener.start();
	}

	
	/*
	 * Begin interface methods section
	 */
	//Returns the port number of the target host this socket is connected to, or 0 if this socket is not yet connected.
	public int getPort(){
		return this.remotePort;
	}
	
	//Returns the local port this socket is bound to, or -1 if the socket is unbound.
	public int getLocalPort(){
		return this.localPort;
	}
	
	//Returns the IP address of the target host this socket is connected to, or null if this socket is not yet connected.
	public InetAddress getInetAddress(){
		return this.remoteInetAddress;
	}
	
	//Returns the local IP address this socket is bound to, or null if the socket is unbound.
	public InetAddress getLocalAddress(){
		return this.localInetAddress;
	}
	
	//Returns the client-side input stream. This stream contains only payload.
	public InputStream getInputStream(){
		return this.receivedDataStream;
	}
	
	//writes arbitrary bytes to the remote end of the client socket.
	public void write(byte[] payload){
		try {
			toSendDataStream.write(payload);
			toSendDataStream.flush();
		} catch (IOException e) {
			System.out.println("Client: IO exception writing data to server.");
			e.printStackTrace();
		}
	}
	
	//writes arbitrary Strings to the remote end of the client socket.
	public void write(String payload){
		try {
			this.write(payload.getBytes("UTF-8")); //Simply uses the other Write() method of the Client class.
		} catch (UnsupportedEncodingException e) {
			System.out.println("Client: problem encoding bytes into UTF-8 format.");
			e.printStackTrace();
		}
	}
	
	//returns the OutputStream that is hooked up to the InputStream of the ClientSenderThread. Returns null if no ClientSenderThread is running.
	public OutputStream getOutputStream(){
		return this.toSendDataStream;
	}
	
}