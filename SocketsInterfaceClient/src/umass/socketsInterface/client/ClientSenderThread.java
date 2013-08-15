package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Responsible for outgoing traffic client-side.
 * 
 */

public class ClientSenderThread extends Thread {
	private Socket serverSock;
	private BufferedReader serverInStream;
	private BufferedWriter serverOutStream;
	
	private String destHostAddress;
	private int destPortNum;
	private String srcHostAddress;
	private int srcPortNum;
	
	public ClientSenderThread(String destHostAddress, int destPortNum){
		this.serverSock = Client.serverSock;
		try {
			this.serverInStream = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			this.serverOutStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Problem initializing the readers/writers for the server socket streams.");
			e.printStackTrace();
		}
		
		this.destHostAddress = destHostAddress;
		this.destPortNum = destPortNum;
		this.srcHostAddress = serverSock.getLocalAddress().getHostAddress();
		this.srcPortNum = serverSock.getLocalPort();
	}
	
	public void run(){
		connectToPeer(); //this must always happen; write() will fail if this hasn't been done yet.
		String helloWorld = new String("Hello, World!");
		
		
		//test byte[] version
		byte[] helloWorldBytes = helloWorld.getBytes();
		write(helloWorldBytes);
		
	}
	
	/*
	 * Writes data to the client that this clientSenderThread is currently connected to.
	 * 
	 * byte[] version.
	 */
	public void write(byte[] rawData){
		try {
			String strData = new String(rawData, "UTF-8");
			serverOutStream.write("DATA" + " " + destHostAddress + " " + destPortNum + " " + srcHostAddress + " " + srcPortNum + " " + "BEGIN_DATA" + strData);
			serverOutStream.newLine();
			serverOutStream.flush();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problem encoding bytes into UTF-8 format.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client: IO exception writing data to server.");
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Writes data to the client that this clientSenderThread is currently connected to.
	 * 
	 * String version.
	 */
	public void write(String strData){
		try {
			serverOutStream.write("DATA" + " " + destHostAddress + " " + destPortNum + " " + srcHostAddress + " " + srcPortNum + " " + "BEGIN_DATA" + strData);
			serverOutStream.newLine();
			serverOutStream.flush();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problem encoding bytes into UTF-8 format.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client: IO exception writing data to server.");
			e.printStackTrace();
		}
		
	}
	
	private void connectToPeer(){
		//Socket outSocket;
		try {
			
			//send connection message to server
			//outSocket = new Socket(InetAddress.getByName(hostAddress), portNum); //Old, direct way.
			System.out.println("Client sender thread using socket connected to: " + serverSock.getInetAddress());
			BufferedReader serverInStream = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			BufferedWriter serverOutStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
			
			serverOutStream.write("CONNECT" + " " + destHostAddress + " " + destPortNum + " " + srcHostAddress + " " + srcPortNum);
			serverOutStream.newLine();
			serverOutStream.flush();
			
			String response = serverInStream.readLine();
			if(response.contains("CONNECT_ACCEPT" + " " + destHostAddress + " " + destPortNum + " " + srcHostAddress + " " + srcPortNum)){
				System.out.println("Successfully connected to client with IP: " + destHostAddress + " and port: " + destPortNum);
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Client: unknown host exception connecting to client.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client: IO exception connecting to server.");
			e.printStackTrace();
		}
	}
	
}//end class
