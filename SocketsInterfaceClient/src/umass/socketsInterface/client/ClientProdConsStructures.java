package umass.socketsInterface.client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class stores all data structures that 
 * 	will be shared among the threads of the client.
 * 
 */

public class ClientProdConsStructures {	
	//for singleton
	private ClientProdConsStructures instance = null;
	
	//locks
	private static Lock constructorLock = new ReentrantLock();
	
	public ClientProdConsStructures getInstance(){
		constructorLock.lock();
		if(instance == null){
			this.instance = new ClientProdConsStructures();
		}
		constructorLock.unlock();
		return this.instance;
	}
	
	//actual constructor
	private ClientProdConsStructures(){
		
	}
	
}
