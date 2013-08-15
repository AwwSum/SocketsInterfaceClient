package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/*
 * This thread will listen for connections from other
 *	hosts using the NAT traversal API
 * 	and respond to them.
 * 
 */

public class ClientListenerThread extends Thread {
	
	private OutputStream userStream; //writes to an InputStream that the user can directly interact with.
	
	/*
	 * Create the listener, which will accept incoming connections and respond to them.
	 * Note that "accept" in this context means "send the CONNECT_ACCEPT response" 
	 * 	to the client that sent the "CONNECT" request.
	 */
	public ClientListenerThread(OutputStream outStream){
		this.userStream = outStream;
	}
	
	public void run(){
		
		try {
			//server communication
			BufferedReader inStream = new BufferedReader(new InputStreamReader(Client.serverSock.getInputStream()));
			BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(Client.serverSock.getOutputStream()));
			
			//user communication
			BufferedWriter outToUserStream = new BufferedWriter(new OutputStreamWriter(this.userStream));
			
			while(true){
				//receive from client
				String rawString = inStream.readLine();
				String[] parsedString = rawString.split(" ");
				String command = parsedString[0];
				System.out.println("Client Listener: read from client: " + rawString);
				
				switch(command){
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
					 * 		which pipes to an internal input stream, which is accessible to the user via
					 * 		a mechanism similar to socket.getInputStream() to allow for reads of data.
					 */
					outToUserStream.write(payload);
					outToUserStream.newLine();
					outToUserStream.flush();
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