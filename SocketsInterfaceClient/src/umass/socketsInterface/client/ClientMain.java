package umass.socketsInterface.client;

import java.net.Socket;

public class ClientMain {
	/*
	 * Connects the client to the server, which acts as the middleman between
	 * 	client daemons.
	 */
	
	public static void main(String[] args){
		//the following 2 arguments are mandatory.
		String serverIPAddress = null;
		int serverPortNum = -1;
		
		//the following 4 arguments are optional, and are default-initialized.
		String destIPAddress = null;
		int destPortNum = -1;
		//int listenerServerPort = 31337;
		//int listenerBacklog = 10;
		
		//TODO: remove the ability to start without args. That is just for debugging.
		if(args.length != 6 && args.length != 4 && args.length != 2){
			System.out.println("Usage: ./client <server address> <server port> [<destination address> <destination port>]");
			System.exit(-1);
		}
		else{ //this should be the normal case.
			switch(args.length){
			case 2: serverIPAddress = args[0];
					serverPortNum = Integer.parseInt(args[1]);
					break;
			case 4: serverIPAddress = args[0];
					serverPortNum = Integer.parseInt(args[1]);
					destIPAddress = args[2];
					destPortNum = Integer.parseInt(args[3]);
					break;
					/*
			case 6: serverIPAddress = args[0];
					serverPortNum = Integer.parseInt(args[1]);
					destIPAddress = args[2];
					destPortNum = Integer.parseInt(args[3]);
					listenerServerPort = Integer.parseInt(args[4]);
					listenerBacklog = Integer.parseInt(args[5]);
					break;
					*/
			default:System.out.println("Usage: ./client <server address> <server port> [<destination address> <destination port>");
					break;
				
			}
		}
		//Connect to server. This client uses the default backlog of 10; there is another client constructor available however.
		Client testClient = new Client(serverIPAddress, serverPortNum);
		testClient.connectToServer();
		
		//if this is a client connecting to another, then spawn the sender thread and use it.
		if(destIPAddress != null && destPortNum != -1){
			System.out.println("Opening connection to another client.");
			ClientSenderThread testClientSender = new ClientSenderThread(destIPAddress, destPortNum);
			testClientSender.start();
		}
		else{ //else, this is a listening client. So spawn the listening thread.
			System.out.println("Opening client listener socket.");
			ClientListenerThread testClientListener = new ClientListenerThread();
			testClientListener.start();
		}
	}	
}		
