package cemfreitas.autorizador.manager;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cemfreitas.autorizador.AutorizadorConstants;
import cemfreitas.autorizador.Client;
import cemfreitas.autorizador.utils.AppFunctions;

/* Abstract class TransactionBase.
 * Provides basic implementations for some transactions services. * 
 */
public abstract class TransactionBase implements Transaction, Callable<Long> {
	private String clientIP, clientName;
	private int clientPort;
	private byte[] transactionToClient, transactionFromClient;
	private Client client;
	private long timeOut;

	// Basic constructor with no params.
	// Used by TransactionMasterCard
	public TransactionBase() {

	}

	// Constructor with client connection params.
	// Used by classes that need to connect to some client services.
	public TransactionBase(String clientIP, int clientPort, String clientName, long timeOut) {
		this.clientIP = clientIP;
		this.clientPort = clientPort;
		this.clientName = clientName;
		//Check whether timeout < min allowed.
		if (timeOut >= AutorizadorConstants.TIMEOUT_MIN_ALLOWED_CLIENT) {
			this.timeOut = timeOut;
		} else {
			this.timeOut = AutorizadorConstants.TIMEOUT_DEFAULT_CLIENT;
		}

	}

	public abstract void pack() throws AutorizadorException; // To be
																// implemented

	public abstract void unpack() throws AutorizadorException; // To be
																// implemented

	// Send and receive a transaction to a client.
	// Use ExecutorService for timeout control.
	@Override
	public long doTransaction() throws AutorizadorException {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Long> future = executor.submit(this);
		long timeRunning = 0;

		try {
			timeRunning = (long) future.get(timeOut, TimeUnit.MILLISECONDS);

		} catch (TimeoutException e) {// If timed out ...
			throw new AutorizadorException(
					"Time Out !!! A execucao do(a) " + clientName + " demorou mais de " + timeOut + " milisegundos");
		} catch (InterruptedException e) {// If cancelled by the transaction
											// manager Executor Service.
			throw new AutorizadorException("A execucao do(a) " + clientName + " foi interrompido(a)");
		} catch (ExecutionException e) {
			throw new AutorizadorException("A execucao do(a) " + clientName + " terminou com erro :" + e.getMessage());
		} finally {// Try interrupt if timed out.
			future.cancel(true);
		}

		return timeRunning;
	}

	// Send to client
	@Override
	public void send() throws IOException, AutorizadorException {
		try {
			client = new Client(clientIP, clientPort, clientName);
			client.clientConnect();
			client.setRequestTransaction(transactionToClient);
			client.sendTransaction();
		} catch (IOException e) {
			throw e;
		}
	}

	// Receive from client
	@Override
	public void receive() throws IOException, AutorizadorException {
		client.receiveTransaction();
		transactionFromClient = client.getResponseTransaction();
		client.closeConnection();
	}

	// For trace purposes
	@Override
	public String getTransactionToTrace(Object transaction) {
		byte[] transactionToLog = (byte[]) transaction;
		return AppFunctions.hexdump(transactionToLog);
	}

	public byte[] getTransactionToClient() {
		return transactionToClient;
	}

	public void setTransactionToClient(byte[] transactionToClient) {
		this.transactionToClient = transactionToClient;
	}

	public byte[] getTransactionFromClient() {
		return transactionFromClient;
	}

	public void setTransactionFromClient(byte[] transactionFromClient) {
		this.transactionFromClient = transactionFromClient;
	}
}
