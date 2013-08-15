package umass.socketsInterface.client;

import java.io.InputStream;
import java.net.InetAddress;

public interface UMassSocketsInterfaceClient {
	
	/* read functionality */
	public InputStream getInputStream();
	
	
	/* write functionality */
	public void write(String payload);
	public void write(byte[] payload);
	
	
	/* getters */
	public int getPort();
	public int getLocalPort();
	public InetAddress getInetAddress();
	public InetAddress getLocalAddress();
}
