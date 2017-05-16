package cemfreitas.autorizador.manager;

import java.io.IOException;

import org.slf4j.Logger;

import cemfreitas.autorizador.utils.AppFunctions;
import cemfreitas.autorizador.utils.AutorizadorParams;
import cemfreitas.autorizador.utils.Logging;

/* TransactionHSM class.
 * Extends abstract TransactionBase and provides
 * further implementation for HSM services. 
 */
public class TransactionHSM extends TransactionBase {
	// Get client connection information
	private static final String hsmIP = AutorizadorParams.getValue("IPServidorHSM");
	private static final int hsmPort = AutorizadorParams.getValueAsInt("PortaServidorHSM");
	private static final String clientName = "HSM";
	//
	private static final long timeOut = AutorizadorParams.getValueAsInt("TimeoutHSM");
	private Logger traceLog = Logging.getTrace();
	private Mediator mediator;

	// Constructor. Send to super class information for HSM connection.
	public TransactionHSM(Mediator mediator) {
		super(hsmIP, hsmPort, clientName, timeOut);
		this.mediator = mediator;
	}

	// Not used for HSM message
	@Override
	public void pack() throws AutorizadorException {
		return;
	}

	// Not used for HSM message
	@Override
	public void unpack() throws AutorizadorException {
		return;
	}

	// Inherited from superclass. Used by Service Executor to timeout control.
	// Executes a HSM transaction sending and receiving a message.
	@Override
	public Long call() throws AutorizadorException {
		byte[] hsmTransaction = mediator.getDatabaseHsmReturn().get(0).getBytes();

		hsmTransaction = AppFunctions.addHeader(hsmTransaction);

		byte[] zeros = new byte[383 - hsmTransaction.length];

		hsmTransaction = AppFunctions.concat(hsmTransaction, zeros);

		mediator.setTransactionToHSM(hsmTransaction);

		setTransactionToClient(hsmTransaction);
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Transacao enviada ao HSM -----");
			traceLog.trace(AppFunctions.hexdump(hsmTransaction));
		}
		long beginExecution = System.currentTimeMillis();
		try {
			send();
			receive();
			mediator.setHsmDisconected(false);
		} catch (IOException e) {
			mediator.setHsmDisconected(true);
			throw new AutorizadorException(e);
		}
		long endExecution = System.currentTimeMillis();
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Transacao recebida do HSM -----");
			traceLog.trace(AppFunctions.hexdump(getTransactionFromClient()));
		}
		mediator.setTransactionFromHSM(getTransactionFromClient());// Send HSM
																	// response
																	// to
																	// mediator.
		return (endExecution - beginExecution);
	}

}
