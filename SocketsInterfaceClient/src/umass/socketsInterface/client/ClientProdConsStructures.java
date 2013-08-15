package umass.socketsInterface.client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class stores all data structures that 
 * 	will be shared among the threads of the client.
 * 
 */

public class ClientProdConsStructures {
	private static Lock constructorLock = new ReentrantLock();
	private boolean hasBeenInstantiated = false;
	private ClientProdConsStructures instance; //for singleton
	
	public ClientProdConsStructures getInstance(){
		constructorLock.lock();
		if(hasBeenInstantiated == false){
			this.instance = new ClientProdConsStructures();
		}
		constructorLock.unlock();
		return this.instance;
	}
	
	//actual constructor
	private ClientProdConsStructures(){
		
	}
	
}
