package cemfreitas.autorizador.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;

import cemfreitas.autorizador.AutorizadorConstants;
import cemfreitas.autorizador.utils.AutorizadorParams;
import cemfreitas.autorizador.utils.Logging;
import cemfreitas.autorizadorMVC.TransactionData;

/* TransactionMediator class
 * Implements the mediator pattern and does the communication among the classes used to process the transaction.   
 * In order to process a transaction, several sub process are executed. The order of the these stpes are:  
 * Receive transaction - unpack - pack trans to send to Ecoscard - receive from Ecoscard - unpack - send to HSM - 
 * receive from HSM - send to authorization stored procedure - receive response - pack transaction - send to Master card.
 * 
 * Implements the observer pattern which notifies the Transaction Manager (TM) when a transaction is unpacked 
 * and when it is completed, successfully or not.
 * 
 * Implements Manager interface which contains the methods used by TM.
 * 
 * Implements Callable interface which contains the method call used by Service Executor API for time out control.  
 *         
 */
public class TransactionMediator extends Observable implements Mediator, Manager, Callable<Object> {
	private static final long timeOutCunfigured = AutorizadorParams.getValueAsInt("TimeoutTransacao");//Get transaction time out value.
	private Logger traceLog = Logging.getTrace();

	//Used by Transaction implementations.
	private Transaction transactionMasterCard, transHsmDB, transAutorizationDB, transactionEcoscard, transactionHSM;

	//Used by streams connections from TM 
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket connection;

	//Used by transactions bytes representation 
	private byte[] transactionFromMC, transactionFromEcoscard, transactionToMC, transactionToEcoscard,
			transactionFromHSM, transactionToHSM;

	//Used by transactions ISO8583 representation
	private ISOMsg isoTransactionFromMC, isoTransactionFromEcoscard, isoTransactionToMC, isoTransactionToEcoscard;

	//Flags to transaction types and its execution status.
	private boolean isHSMTransaction, isChipCardTransaction, isTimeOut, isAborted, isHsmDisconected, isEcoDisconected;

	//Hold the stored procedure returns
	private List<String> databaseHsmReturn, databaseAutReturn;

	//Used by TM to get the current transaction phase.
	private int transactionPhase;

	//Hold a transaction to be displayed on the screen.	
	private TransactionData transactionData;

	//Hold an exception when occurs. 
	private Exception autException, ecoException, hsmException;

	// Hold the execution time
	private long ecoExecutionTime, hsmDbExecutionTime, hsmExecutionTime, autDbExecutionTime, autExecutionTime;

	// Constructor.
	//Receives CM message and connection from TM
	public TransactionMediator(byte[] transactionFromMC, Socket connection) throws IOException {
		this.transactionFromMC = transactionFromMC;
		this.connection = connection;
		this.inputStream = connection.getInputStream();
		this.outputStream = connection.getOutputStream();
	}

	//Add the TM as Observable
	@Override
	public void addObservable(Observer o) {
		this.addObserver(o);
	}

	// Manager interface method implementation. Uses an Executor Service for time out control.
	@Override
	public void perform() {
		ExecutorService executor = Executors.newCachedThreadPool();

		Future<Object> future = executor.submit(this);// submit reference with call method.

		long timeOutReal;

		//Check whether timeout < min allowed.
		if (timeOutCunfigured < AutorizadorConstants.TIMEOUT_MIN_ALLOWED_TRANSAC) {
			timeOutReal = AutorizadorConstants.TIMEOUT_DEFAULT_TRANSAC;
		} else {
			timeOutReal = timeOutCunfigured;
		}

		try {

			future.get(timeOutReal, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			isTimeOut = true; // If time out, set flag on
			autException = new AutorizadorException("********** Time Out !!! A transacao demorou mais de " + timeOutReal
					+ " milisegundos para executar **********");
		} catch (InterruptedException e) {
			isTimeOut = true;
			autException = new AutorizadorException("********** A execucao da transacao foi interrompida **********");
		} catch (ExecutionException e) {
			isTimeOut = true;
			autException = new AutorizadorException(
					"********** A execucao da transacao terminou com erro :" + e.getMessage() + " ********** ");
		} finally {
			future.cancel(true); // Attempts to cancel the thread if it is still running.
		}
	}

	// Callable method used by ExecutorService. Executes the transaction flow.
	@Override
	public Object call() {
		TransactionFactoryBase transactionFactory = new TransactionFactory();
		long beginTransacExec, endTransacExec;

		try {// Outer try/catch block
			beginTransacExec = System.currentTimeMillis(); // Get the time of
															// the beginning
															// transaction
															// execution.

			//Starts to process a MC transaction.
			transactionMasterCard = transactionFactory.createMasterCardTransaction(this);
			transactionMasterCard.unpack();
			changeTransactionPhase(AutorizadorConstants.TRANSAC_MC_UNPACK_PHASE);//Notify TM with a new unpacked transaction.
			/*
			 * Flag settled by TransactionMasterCard to know whether or not
			 * should be sent to Ecoscard.
			 */
			if (isChipCardTransaction) {
				/*
				 * Inner try/catch block - Catch errors from Ecoscard then
				 * continue the execution flow.
				 */
				try {
					transactionEcoscard = transactionFactory.createEcoscardTransaction(this);
					transactionEcoscard.pack();

					ecoExecutionTime = transactionEcoscard.doTransaction();// Send
																			// and
																			// receive
																			// to
																			// Ecoscard

					transactionEcoscard.unpack();
					// -------------

				} catch (AutorizadorException e) {
					ecoException = e;
					if (traceLog.isTraceEnabled()) {
						traceLog.trace("Origem do erro:", e);// If trace
																// enabled,
																// log
																// the stack
																// error
																// on
																// trace file.
					}
					isAborted = true;// Set flag. Transaction aborted.
				} // End of inner try/catch block
			}

			/*
			 * Flag settled by TransactionMasterCard to know whether or not
			 * should be send to HSM.
			 */
			if (isHSMTransaction) {
				/*
				 * Inner try/catch block - Catch errors from HSM then continue
				 * the execution flow.
				 */
				try {
					// Send to SP HSM, receive response and send to HSM.
					transHsmDB = transactionFactory.createHsmDbTransaction(this);

					hsmDbExecutionTime = transHsmDB.doTransaction();// Execute
																	// SP
																	// HSM.

					transactionHSM = transactionFactory.createHsmTransaction(this);

					hsmExecutionTime = transactionHSM.doTransaction();// Send
																		// and
																		// receive
																		// to
																		// HSM					
				} catch (AutorizadorException e) {
					hsmException = e;
					if (traceLog.isTraceEnabled()) {
						traceLog.trace("Origem do erro:", e);// If trace
																// enabled,
																// log
																// the stack
																// error
																// on
																// trace file.
					}
					isAborted = true;// Set flag. Transaction aborted.
				} // End of inner try/catch block

			}
			// Preparing to execute SP Authorization
			transAutorizationDB = transactionFactory.createAutDbTransaction(this);

			autDbExecutionTime = transAutorizationDB.doTransaction(); // Execute
																		// SP
																		// Authorization

			// Preparing to send the transaction response to Master Card.			
			transactionMasterCard.pack();// Pack response			

			transactionMasterCard.send();// Send it

			endTransacExec = System.currentTimeMillis();//Mark the end of execution and ...
			autExecutionTime = endTransacExec - beginTransacExec;//Calculates the execution time.

		} catch (AutorizadorException | IOException e) {
			autException = e;

			if (traceLog.isTraceEnabled()) {
				if (!e.getMessage().equals("")) {
					traceLog.trace("Origem do erro:", e);// If trace enabled, log
															// the stack error on
															// trace file.
				}
			}
			isAborted = true;// Set flag. Transaction aborted.

		} finally {
			try {// Release streams and disconnect.
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
				if (connection != null)
					connection.close();
			} catch (IOException e) {
				isAborted = true;
				autException = new AutorizadorException("Erro ao fechar conexao com a Mastercard.");
			}

			/*
			 * if (traceLog.isTraceEnabled()) {// Turn the trace log off if
			 * enabled, so that be executed only once. Logging.turnTraceOff(); }
			 */

			//Message could not be unpacked. Aborting ...
			if (isoTransactionFromMC == null) {
				return null;
			}

			if (isAborted || isTimeOut) {// If occurred some error
				changeTransactionPhase(AutorizadorConstants.TRANSAC_AUT_ERROR_PHASE);//Notify TM with transaction finished with error.
			} else {
				if (!transactionData.getCodigo().equals(AutorizadorConstants.TRANSAC_REVERSAL_TYPE)) {//Reversal message should not be counted.
					changeTransactionPhase(AutorizadorConstants.TRANSAC_COMPLETED_PHASE);//Notify TM with transaction finished successfully.
				}
			}

		} // End of outer try/catch block
		return null;
	}

	//Changes phase and notify TM
	private void changeTransactionPhase(int phase) {
		transactionPhase = phase;
		setChanged();
		notifyObservers(Thread.currentThread().getId());

	}

	//Gets and Sets methods.

	@Override
	public byte[] getTransactionFromMC() {
		return transactionFromMC;
	}

	@Override
	public void setTransactionFromMC(byte[] transactionFromMC) {
		this.transactionFromMC = transactionFromMC;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public ISOMsg getIsoTransactionFromMC() {
		return isoTransactionFromMC;
	}

	@Override
	public void setIsoTransactionFromMC(ISOMsg isoTransactionFromMC) {
		this.isoTransactionFromMC = isoTransactionFromMC;
	}

	@Override
	public boolean isHSMTransaction() {
		return isHSMTransaction;
	}

	@Override
	public void setHSMTransaction(boolean isHSMTransaction) {
		this.isHSMTransaction = isHSMTransaction;
	}

	@Override
	public boolean isChipCardTransaction() {
		return isChipCardTransaction;
	}

	@Override
	public void setChipCardTransaction(boolean isChipCardTransaction) {
		this.isChipCardTransaction = isChipCardTransaction;
	}

	@Override
	public byte[] getTransactionFromEcoscard() {
		return transactionFromEcoscard;
	}

	@Override
	public void setTransactionFromEcoscard(byte[] transactionFromEcoscard) {
		this.transactionFromEcoscard = transactionFromEcoscard;
	}

	@Override
	public ISOMsg getIsoTransactionFromEcoscard() {
		return isoTransactionFromEcoscard;
	}

	@Override
	public void setIsoTransactionFromEcoscard(ISOMsg isoTransactionFromEcoscard) {
		this.isoTransactionFromEcoscard = isoTransactionFromEcoscard;
	}

	@Override
	public ISOMsg getIsoTransactionToEcoscard() {
		return isoTransactionToEcoscard;
	}

	@Override
	public void setIsoTransactionToEcoscard(ISOMsg isoTransactionToEcoscard) {
		this.isoTransactionToEcoscard = isoTransactionToEcoscard;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public byte[] getTransactionToMC() {
		return transactionToMC;
	}

	@Override
	public void setTransactionToMC(byte[] transactionToMC) {
		this.transactionToMC = transactionToMC;
	}

	@Override
	public byte[] getTransactionToEcoscard() {
		return transactionToEcoscard;
	}

	@Override
	public void setTransactionToEcoscard(byte[] transactionToEcoscard) {
		this.transactionToEcoscard = transactionToEcoscard;
	}

	@Override
	public ISOMsg getIsoTransactionToMC() {
		return isoTransactionToMC;
	}

	@Override
	public void setIsoTransactionToMC(ISOMsg isoTransactionToMC) {
		this.isoTransactionToMC = isoTransactionToMC;
	}

	@Override
	public byte[] getTransactionFromHSM() {
		return transactionFromHSM;
	}

	@Override
	public void setTransactionFromHSM(byte[] transactionFromHSM) {
		this.transactionFromHSM = transactionFromHSM;
	}

	@Override
	public byte[] getTransactionToHSM() {
		return transactionToHSM;
	}

	@Override
	public void setTransactionToHSM(byte[] transactionToHSM) {
		this.transactionToHSM = transactionToHSM;
	}

	@Override
	public void setDatabaseHsmReturn(List<String> databaseHsmReturn) {
		this.databaseHsmReturn = databaseHsmReturn;
	}

	@Override
	public List<String> getDatabaseHsmReturn() {
		return databaseHsmReturn;
	}

	@Override
	public void setDatabaseAutReturn(List<String> databaseAutReturn) {
		this.databaseAutReturn = databaseAutReturn;
	}

	@Override
	public List<String> getDatabaseAutReturn() {
		return databaseAutReturn;
	}

	@Override
	public int getTransactionPhase() {
		return transactionPhase;
	}

	@Override
	public TransactionData getTransactionData() {
		return transactionData;
	}

	@Override
	public void setTransactionData(TransactionData transactionData) {
		this.transactionData = transactionData;
	}

	@Override
	public boolean isHsmDisconected() {
		return isHsmDisconected;
	}

	@Override
	public boolean isEcoDisconected() {
		return isEcoDisconected;
	}

	@Override
	public void setHsmDisconected(boolean isHsmDisconected) {
		this.isHsmDisconected = isHsmDisconected;

	}

	@Override
	public void setEcoDisconected(boolean isEcoDisconected) {
		this.isEcoDisconected = isEcoDisconected;

	}

	@Override
	public long getEcoExecutionTime() {
		return ecoExecutionTime;
	}

	@Override
	public long getHsmDbExecutionTime() {
		return hsmDbExecutionTime;
	}

	@Override
	public long getHsmExecutionTime() {
		return hsmExecutionTime;
	}

	@Override
	public long getAutDbExecutionTime() {
		return autDbExecutionTime;
	}

	@Override
	public long getAutExecutionTime() {
		return autExecutionTime;
	}

	@Override
	public Exception getAutException() {
		return autException;
	}

	@Override
	public Exception getEcoException() {
		return ecoException;
	}

	@Override
	public Exception getHsmException() {
		return hsmException;
	}

	@Override
	public boolean isTimeOut() {
		return isTimeOut;
	}
}
