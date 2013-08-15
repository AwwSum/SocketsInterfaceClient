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

	public static InetAddress serverInetAddress;
	public static int backlog;
	public static int serverPort;
	public static Socket serverSock;
	
	//create client with the default backlog of 10.
	public Client(String ipAddr, int port){
		Client.serverPort = port;
		Client.backlog = 10;
		try{
			Client.serverInetAddress = InetAddress.getByName(ipAddr);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		}
	}
	
	//create client with specified backlog.
	public Client(int port, String ipAddr, int backlog){
		Client.serverPort = port;
		Client.backlog = backlog;
		try{
			Client.serverInetAddress = InetAddress.getByName(ipAddr);
		} catch(UnknownHostException e){
			System.out.println("Invalid IP address passed to client.");
			System.exit(-1);
		}
	}
	
	int getPort(){
		return serverPort;
	}
	
	Socket connectToServer(){
		try {
			serverSock = new Socket(serverInetAddress, serverPort);
			System.out.println("Client: socket created");
			
			BufferedReader inStream = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
			
			//outStream.write("This message is from the output stream of the client socket. \n");
			outStream.flush(); //this is necessary for some reason...
			//System.out.println("Received string: " + inStream.readLine() + " from server. ");
			
		} catch (UnknownHostException e) {
			System.out.println("Client: unknown host exception connecting to server.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Client: IO exception connecting to server.");
			e.printStackTrace();
		}
		return serverSock;
	}
}