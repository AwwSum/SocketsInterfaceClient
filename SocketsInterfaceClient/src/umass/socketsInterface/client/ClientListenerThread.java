package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/*
 * This thread will listen for connections from other
 *	hosts using the NAT traversal API
 * 	and respond to them.
 * 
 */

public class ClientListenerThread extends Thread {
	
	//private Socket serverSock;
	//private ServerSocket inSocket;

	/*
	 * Create the listener, which will accept() incoming connections and respond to them.
	 * 
	 */
	public ClientListenerThread(){
		//this.serverSock = Client.serverSock;
		//this.serverPort = serverPort;
		//this.backlog = backlog;
		/*
		try {
			//We no longer need to bind to an address, because the TCP connection to the server is 2-way.
			//InetAddress bindAddr = InetAddress.getByName("localhost");
			//this.inSocket = new Socket(serverPort, backlog, bindAddr);
			//this.inSocket.setReuseAddress(true);
			
		} catch (IOException e) {
			System.out.println("Error creating a client listener thread.");
			e.printStackTrace();
		}
		*/
	}
	
	public void run(){
		
		try {
			//accept a connection and set up the streams
			//newPeer = inSocket.accept();
			BufferedReader inStream = new BufferedReader(new InputStreamReader(Client.serverSock.getInputStream()));
			BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(Client.serverSock.getOutputStream()));
			
			while(true){
				//receive from client
				String rawString = inStream.readLine();
				String[] parsedString = rawString.split(" ");
				System.out.println("Client Listener: read from client: " + rawString);
				
				switch(parsedString[0]){
				case "CONNECT":
					System.out.println("Client Listener: Received a connect request.");
					outStream.write("CONNECT_ACCEPT" + " " + parsedString[1] + " " + parsedString[2] + " " + parsedString[3] + " " + parsedString[4]);
					outStream.newLine();
					outStream.flush();
					break;
				case "DATA":
					String[] dataSplit = rawString.split("BEGIN_DATA");
					String payload = dataSplit[1];
					System.out.println("Client Listener: Received a DATA message with payload: " + payload);
					/*	Write the data itself (sans control messages)to internal output stream,
					 * 		which pipes to an internal input stream, which is accessible via
					 * 		a mechanism similar to socket.getInputStream to allow for reads of data.
					 */
					break;
				default:System.out.println("Client Listener: Received unrecognized command: " + rawString);
						break;
				}
			}	
		} catch (IOException e) {
			System.out.println("Client Listener: Error accepting a connection from another client.");
			e.printStackTrace();
		}
	}
	
}