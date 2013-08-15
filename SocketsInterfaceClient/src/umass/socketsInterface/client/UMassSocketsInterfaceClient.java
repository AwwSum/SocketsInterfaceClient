package umass.socketsInterface.client;

import java.io.InputStream;
import java.net.InetAddress;

public interface UMassSocketsInterfaceClient {

	/* constructors */
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on an ephemeral port. 
	public Client Client(String serverAddr, int serverPort);
	
	//create a client listener connected to serverAddr:serverPort. Binds to localhost on the port specified by listenerPort. 
	public Client Client(String serverAddr, int serverPort, int clientListenerPort);
	
	//Creates client listener and sender, and attempts to connect to the client listener at destAddr:destPort.
	//Uses ephemeral port and localhost for the ClientListenerThread.
	public Client Client(String serverAddr, int serverPort, String destAddr, int destPort);
	
	
	/* read functionality */
	public InputStream getInputStream();
	
	
	/* write functionality */
	public int write(String payload);
	public int write(byte[] payload);
	
	
	/* getters */
	public int getPort();
	public int getLocalPort();
	public InetAddress getInetAddress();
	public InetAddress getLocalAddress();
}
