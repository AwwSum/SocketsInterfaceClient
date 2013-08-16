package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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
	private InputStream pendingData; //data waiting to be written. Is piped in from Client.
	
	private String destHostAddress;
	private int destPortNum;
	private String srcHostAddress;
	private int srcPortNum;
	
	public ClientSenderThread(InputStream pendingData, String destHostAddress, int destPortNum, Client client){
		this.serverSock = client.serverSock;
		try {
			this.serverInStream = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			this.serverOutStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Problem initializing the readers/writers for the server socket streams.");
			e.printStackTrace();
		}
		
		this.destHostAddress = destHostAddress;
		this.destPortNum = destPortNum;
		this.srcHostAddress = IPChecker.getIp();
		this.srcPortNum = serverSock.getLocalPort();
		this.pendingData = pendingData;
	}
	
	public void run(){
		connectToPeer(); //this must always happen; write() will fail if this hasn't been done yet.
		//wait for input to write to the connected client
		while(true){
			try {
				if(pendingData.available() > 0){
					write(convertStreamToString(pendingData));
				}
				else{
					sleep(10);
				}
			} catch (IOException e) {
				System.out.println("Client Sender Thread: I/O error checking number of available bytes.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				//Do nothing. This is expected behavior.
			}
		}
	}
	
	private void connectToPeer(){
		try {
			//send connection message to server
			//outSocket = new Socket(InetAddress.getByName(hostAddress), portNum); //Old, direct way.
			System.out.println("Client sender thread using socket connected to: " + serverSock.getInetAddress());
			serverInStream = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			serverOutStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
			
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
			System.out.println("Client Sender Thread: Problem encoding bytes into UTF-8 format.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client Sender Thread: IO exception writing data to server.");
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Given an InputStream, convert the contents to a string.
	 * 
	 * Credit goes to Pavel Repin, Patrick (stack overflow user) and Jacob (stack overflow user)
	 * 	for this utility function. 
	 * URL: http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	 */
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
}//end class
