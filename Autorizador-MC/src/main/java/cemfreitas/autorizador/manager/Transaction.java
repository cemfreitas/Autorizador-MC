package cemfreitas.autorizador.manager;

import java.io.IOException;

/* Transaction interface.
 * Implemented by all class which have transaction behavior.  
 * Provides service methods for transaction processing.
 */
public interface Transaction {
	void send() throws IOException, AutorizadorException;

	void receive() throws IOException, AutorizadorException;

	long doTransaction() throws AutorizadorException;

	void pack() throws AutorizadorException;

	void unpack() throws AutorizadorException;

	String getTransactionToTrace(Object transaction);
	
}
