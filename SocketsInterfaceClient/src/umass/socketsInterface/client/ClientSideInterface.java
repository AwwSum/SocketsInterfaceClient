package umass.socketsInterface.client;

import java.io.InputStream;
import java.net.InetAddress;

public interface ClientSideInterface {

	//read functionality
	public InputStream getInputStream();
	
	//write functionality
	public int write(String payload);
	public int write(byte[] payload);
	
	//getters
	public int getPort();
	public int getLocalPort();
	public InetAddress getInetAddress();
	public InetAddress getLocalAddress();
}
