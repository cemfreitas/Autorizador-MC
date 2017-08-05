package cemfreitas.autorizadorMC.manager;

import java.io.IOException;

import org.jpos.iso.IFA_BITMAP;
import org.jpos.iso.IFA_LLLCHAR;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;

import cemfreitas.autorizadorMC.AutorizadorConstants;
import cemfreitas.autorizadorMC.utils.AppFunctions;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;
import cemfreitas.autorizadorMC.utils.Logging;

/* TransactionEcoscard class.
 * Extends abstract TransactionBase and provides
 * further implementation for Ecoscard services. 
 */
public class TransactionEcoscard extends TransactionBase {
	// Get client connection information
	private static final String ecoscardIP = AutorizadorParams.getValue("EcoscardIP");
	private static final int ecoscardPort = AutorizadorParams.getValueAsInt("EcoscardPorta");
	private static final String clientName = "Ecoscard";
	//
	private static final String packageName = "config/ISO8583_MC.xml";
	private static final long timeOut = AutorizadorParams.getValueAsInt("TimeOutEcoscard");
	private Logger traceLog = Logging.getTrace();
	private GenericPackager packagerEco = null;
	private Mediator mediator;

	// Constructor. Send to super class information for Ecoscard connection.
	public TransactionEcoscard(Mediator mediator) {
		super(ecoscardIP, ecoscardPort, clientName, timeOut);
		this.mediator = mediator;
	}

	// Pack the transaction to be sent to Ecoscard. Most fields are fixed,
	// according to Ecoscard manual.
	@Override
	public void pack() throws AutorizadorException {
		ISOMsg transactionEcoscard = new ISOMsg();
		ISOMsg transactionFromMC = mediator.getIsoTransactionFromMC();
		byte[] transaction;

		if (packagerEco == null) {
			getPackager();
		}
		transactionEcoscard.setPackager(packagerEco);

		try {
			transactionEcoscard.setMTI(AutorizadorConstants.TRANSAC_PURCHASE_TYPE); // It
																					// requires
																					// to
																					// be
																					// a
																					// 0200

			if (transactionFromMC.hasField(2) && !transactionFromMC.hasField(35)) {
				transactionEcoscard.set(2, (String) transactionFromMC.getValue(2));
			}

			transactionEcoscard.set(3, "006400"); // Fixed

			if (transactionFromMC.hasField(7)) {
				transactionEcoscard.set(7, (String) transactionFromMC.getValue(7));
			} else {
				transactionEcoscard.set(7, "0405172920"); // Fixed if no field
															// value from MC
			}

			if (transactionFromMC.hasField(11)) {
				transactionEcoscard.set(11, (String) transactionFromMC.getValue(11));
			} else {
				transactionEcoscard.set(11, "000001"); // Fixed if no field
														// value from MC
			}

			if (transactionFromMC.hasField(35)) {
				transactionEcoscard.set(35, (String) transactionFromMC.getValue(35));
			}
			
			transactionEcoscard.set(41, "19999999");// Fixed

			if (transactionFromMC.hasField(55)) {
				String De55 = AppFunctions.hexString((byte[]) transactionFromMC.getValue(55)); // De55
																								// is
																								// a
																								// hexa
																								// value
				transactionEcoscard.set(55, De55);
			}

			transaction = transactionEcoscard.pack();
			transaction = AppFunctions.addHeader(transaction);

			mediator.setIsoTransactionToEcoscard(transactionEcoscard);// Send
																		// ISO
																		// packed
																		// transaction
																		// to
																		// mediator.

			mediator.setTransactionToEcoscard(transaction);// Send binary
															// transaction to
															// mediator.

		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao empacotar transacao ISO para Ecoscard: " + e.getMessage());
		}
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Transacao Ecoscard empacotada com sucesso -----");
		}
	}

	// Unpack transaction received from Ecoscard.
	@Override
	public void unpack() throws AutorizadorException {
		byte[] transaction;
		try {
			ISOMsg isoTransaction = new ISOMsg();// Create a new ISO message.
			if (packagerEco == null) {
				getPackager();
			}
			isoTransaction.setPackager(packagerEco);// Set the proper ISO
													// packager.

			transaction = mediator.getTransactionFromEcoscard();// Get from
																// mediator
																// transaction
																// to be
																// unpacked.

			if (!AppFunctions.checkHeader(transaction)) {// Check whether its
															// header contains a
															// correct info
															// size.
				throw new AutorizadorException(// If not throw an exception.
						"Erro ao desempacotar transacao da Ecoscard: Transacao com tamanho invalido");
			}

			transaction = AppFunctions.copyBytes(transaction, 2, transaction.length - 1);// Remove
																							// the
																							// header
																							// from
																							// the
																							// transaction
																							// to
																							// be
																							// unpacked.

			isoTransaction.unpack(transaction);

			mediator.setIsoTransactionFromEcoscard(isoTransaction); // Send to
																	// mediator
																	// the
																	// Ecoscard
																	// ISO
																	// message.
			if (isoTransaction != null) {
				if (isoTransaction.hasField(39)) {
					if (!isoTransaction.getValue(39).equals("00")) {						
						throw new AutorizadorException(
								"Erro na validacao da API Ecoscard !!! A Ecoscard respondeu bit39="
										+ isoTransaction.getValue(39));
					}
				} else {
					throw new AutorizadorException("A Ecoscard respondeu sem o bit39.");
				}
			}

			if (traceLog.isTraceEnabled()) {
				traceLog.trace(" ----- Transacao Ecoscard desempacotada com sucesso -----");
			}

		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao desempacotar transacao da Ecoscard: " + e.getMessage());
		}
	}

	// Get the packager and set it properly to unpack the Ecoscard transaction.
	private void getPackager() throws AutorizadorException {
		try {
			packagerEco = new GenericPackager(packageName);
			packagerEco.setFieldPackager(1, new IFA_BITMAP(16, "BIT MAP"));
			packagerEco.setFieldPackager(55, new IFA_LLLCHAR(999, "RESERVED ISO"));
		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao configurar o JPOS");
		}
	}

	// Inherited from superclass. Used by Service Executor to timeout control.
	// Executes an Ecoscard transaction sending and receiving a message.
	@Override
	public Long call() throws AutorizadorException {
		setTransactionToClient(mediator.getTransactionToEcoscard());// Get
																	// transaction
																	// to be
																	// sent from
																	// mediator
																	// and send
																	// it to
																	// superclass.
		long beginExecution = System.currentTimeMillis();
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Transacao enviada para Ecoscard  -----");
			traceLog.trace(AppFunctions.hexdump(mediator.getTransactionToEcoscard()));
		}
		try {
			send();
			receive();
			mediator.setEcoDisconected(false);
		} catch (IOException e) {
			mediator.setEcoDisconected(true);
			throw new AutorizadorException(e);
		}

		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Transacao recebida da Ecoscard  -----");
			traceLog.trace(AppFunctions.hexdump(getTransactionFromClient()));
		}
		long endExecution = System.currentTimeMillis();
		mediator.setTransactionFromEcoscard(getTransactionFromClient()); // Get
																			// transactio
																			// received
																			// from
																			// Ecoscard
																			// and
																			// send
																			// to
																			// mediator.

		return (endExecution - beginExecution); // Return the total execution
												// time.
	}

}
