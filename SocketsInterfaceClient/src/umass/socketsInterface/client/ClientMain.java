package umass.socketsInterface.client;

import java.io.InputStream;
import java.net.Socket;

public class ClientMain {
	/*
	 * Connects the client to the server, which acts as the middleman between
	 * 	client daemons.
	 */
	
	public static void main(String[] args){
		//the following 2 arguments are mandatory.
		String serverIPAddress = null;
		String destIPAddress = null;
		int serverPortNum = -1;
		int destPortNum = -1;
		Client testClient;
		InputStream rcvData;

		switch(args.length){
		case 2: serverIPAddress = args[0];
				serverPortNum = Integer.parseInt(args[1]);
				testClient = new Client(serverIPAddress, serverPortNum); //if this is a listening client. So spawn the listening thread.
				rcvData = testClient.getInputStream();
				break;
		case 4: serverIPAddress = args[0];
				serverPortNum = Integer.parseInt(args[1]);
				destIPAddress = args[2];
				destPortNum = Integer.parseInt(args[3]);
				testClient = new Client(serverIPAddress, serverPortNum, destIPAddress, destPortNum); //if this is a client connecting to another
				testClient.
				break;
		default:System.out.println("Usage: ./client <server address> <server port> [<destination address> <destination port>]");
				System.exit(0);
				break;
		}
		
		
		
	}//end main
}
