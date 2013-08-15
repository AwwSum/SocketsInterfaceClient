package umass.socketsInterface.client;

import java.io.InputStream;

public interface ClientSideInterface {

	public InputStream getInputStream(); //read functionality
	public int write(String payload);
	public int write(byte[] payload);
	
}
