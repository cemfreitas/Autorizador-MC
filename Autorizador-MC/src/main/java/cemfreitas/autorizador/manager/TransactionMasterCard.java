package cemfreitas.autorizador.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;

import cemfreitas.autorizador.AutorizadorConstants;
import cemfreitas.autorizador.utils.AppFunctions;
import cemfreitas.autorizador.utils.Logging;
import cemfreitas.autorizadorMVC.TransactionData;

/* TransactionMasterCard class.
 * Extends abstract class TransactionBase and provides further
 * implementations for the MasterCard transactions.
 */
public class TransactionMasterCard extends TransactionBase {
	private static final String packageName = "config/ISO8583_MC.xml";
	private Logger traceLog = Logging.getTrace();
	private GenericPackager packagerMC;
	private Mediator mediator;
	private byte[] transaction;
	private TransactionData transactionData;// Holds transaction info to show on view.

	public TransactionMasterCard(Mediator mediator) throws AutorizadorException {
		this.mediator = mediator;
		transactionData = new TransactionData();
	}

	// Send the MC transaction response back.
	// It overrides the superclass implementation.
	@Override
	public void send() throws AutorizadorException {
		OutputStream outputStream;
		byte[] transaction;

		outputStream = mediator.getOutputStream(); // Get the outputstream from mediator.
		transaction = mediator.getTransactionToMC(); // Get the transaction from mediator.

		try {
			outputStream.write(transaction);
			if (traceLog.isTraceEnabled()) {
				traceLog.trace(" ----- Resposta enviada ao terminal -----");
				traceLog.trace(AppFunctions.hexdump(transaction));
				Logging.turnTraceOff();// Turn the trace off in order to trace
										// once.
			}

		} catch (IOException e) {
			throw new AutorizadorException("Erro ao enviar transacao para Master Card: " + e.getMessage());
		}

	}

	// Receives the MC transaction from the acquire.
	// It overrides the superclass implementation.
	// Whole transaction process begins here.
	@Override
	public void receive() throws AutorizadorException {
		byte[] msg = null;
		byte pbyte = 0;
		InputStream inputStream = mediator.getInputStream();
		try {
			pbyte = (byte) inputStream.read();
			msg = new byte[inputStream.available()];
			inputStream.read(msg);

			transaction = AppFunctions.concatenate(pbyte, msg);

			mediator.setTransactionFromMC(transaction); // Send MC transaction
														// received to mediator.

			if (traceLog.isTraceEnabled()) {
				traceLog.trace(" ----- Transacao vinda do terminal -----");
				traceLog.trace(getTransactionToTrace(transaction));
			}

			if (msg.length == 0) {
				throw new AutorizadorException("Erro ao receber transacao da MasterCard :zero byte recebido");
			}
		} catch (IOException e) {
			throw new AutorizadorException("Erro ao receber transacao da MasterCard :" + e.getMessage());
		}
	}

	// Pack response transaction to be sent.
	@Override
	public void pack() throws AutorizadorException {
		byte[] transaction;
		ISOMsg isoTransaction = new ISOMsg();// Create a new ISO transaction.

		if (packagerMC == null) {
			getPackager();
		}
		isoTransaction.setPackager(packagerMC);

		List<String> isoFields = mediator.getDatabaseAutReturn();// Get DB
																	// return
																	// with the
																	// transaction
																	// Fields
																	// processed
																	// by the SP
																	// Authorization.

		try {

			// Populates the ISO transactions with the DB return.
			isoTransaction.set(new ISOField(0, isoFields.get(1).substring(1, isoFields.get(1).length())));

			for (int i = 2; i < isoFields.size(); i++) {
				if (isoFields.get(i).charAt(0) == '1') {
					isoTransaction.set(new ISOField(i, isoFields.get(i).substring(1, isoFields.get(i).length())));
				}
			}

			String de52 = isoFields.get(52);
			String de55 = isoFields.get(55);
			String de64 = isoFields.get(64);
			if (isoFields.size() == 129 && de52.charAt(0) == '1') {
				isoTransaction.set(52, AppFunctions.hex2byte(de52.substring(1, de52.length())));
			}

			if (isoFields.size() == 129 && de55.charAt(0) == '1') {
				isoTransaction.set(55, AppFunctions.hex2byte(de55.substring(1, de55.length())));
			}

			if (isoFields.size() == 129 && de64.charAt(0) == '1') {
				isoTransaction.set(64, AppFunctions.hex2byte(de64.substring(1, de64.length())));
			}

			transaction = isoTransaction.pack();// Pack the transaction.
			transaction = AppFunctions.addHeader(transaction);// Add a proper
																// header (it
																// contains the
																// size of
																// message).
			mediator.setTransactionToMC(transaction);// Send to mediator the
														// binary packed
														// transaction
			mediator.setIsoTransactionToMC(isoTransaction);// Send to mediator
															// the ISO packed
															// transaction
			if (traceLog.isTraceEnabled()) {
				traceLog.trace(" ----- Transacao MC empacotada com sucesso -----");
			}

		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao empacotar resposta para MasterCard :" + e.getMessage());
		}
	}

	// Unpack transaction received from MC
	@Override
	public void unpack() throws AutorizadorException {
		try {
			ISOMsg isoTransaction = new ISOMsg();// Create a new ISO
													// transaction.
			byte[] transaction;

			if (packagerMC == null) {
				getPackager();
			}
			isoTransaction.setPackager(packagerMC);

			transaction = mediator.getTransactionFromMC();// Get MC transaction
															// from mediator.

			if (!AppFunctions.checkHeader(transaction)) {// Check whether the
															// head size is
															// Correct
				throw new AutorizadorException(
						"Erro ao desempacotar transacao da MasterCard: Transacao com tamanho invalido");
			}

			transaction = AppFunctions.copyBytes(transaction, 2, transaction.length - 1);// Remove
																							// the
																							// header
																							// before
																							// unpack.

			isoTransaction.unpack(transaction);// Unpack it.

			try {
				transactionData.setCodigo(isoTransaction.getMTI()); // Get cod transaction
			} catch (ISOException e) {

			}

			if (isoTransaction.hasField(12) && isoTransaction.hasField(13)) {// Get
																				// date
																				// and
																				// time
				transactionData.setData(isoTransaction.getValue(13) + " " + isoTransaction.getValue(12));
			}

			if (isoTransaction.hasField(3)) {// Get processing code
				transactionData.setProcesso((String) isoTransaction.getValue(3));
			}

			if (isoTransaction.hasField(42)) {// Get Estbelecimento
				transactionData.setEstabelecimento((String) isoTransaction.getValue(42));
			}
			if (isoTransaction.hasField(63)) {// Get De63
				transactionData.setNSU((String) isoTransaction.getValue(63));
			}
			if (isoTransaction.hasField(35)) {// Get number of the card
				transactionData.setNumCartao((String) isoTransaction.getValue(35));
			} else if (isoTransaction.hasField(2)) {
				transactionData.setNumCartao((String) isoTransaction.getValue(2));
			}
			if (transactionData.getCodigo().equals(AutorizadorConstants.TRANSAC_REVERSAL_TYPE)) {
				transactionData.setStatus(AutorizadorConstants.TRANSAC_REV_STATUS);// Set
																					// status
																					// to
																					// reversal
			} else {
				transactionData.setStatus(AutorizadorConstants.TRANSAC_NEW_STATUS);// Set
																					// status
																					// to
																					// new
																					// transaction
			}

			if (isoTransaction.hasField(4)) { // Get the value of transaction
				transactionData.setValor(AppFunctions.parseAmount((String) isoTransaction.getValue(4)));
			}

			mediator.setTransactionData(transactionData);

			mediator.setIsoTransactionFromMC(isoTransaction);// Send ISO
																// transaction
																// to mediator.
			checkEcoscardTransaction(isoTransaction);// Verify whether is a
														// Ecoscard transaction
														// and set a flag.
			checkHSMTransaction(isoTransaction);// Verify whether is a HSM
												// transaction and set a flag.
			if (traceLog.isTraceEnabled()) {
				traceLog.trace(" ----- Transacao MC desempacotada com sucesso -----");
			}

		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao desempacotar transacao da MasterCard: " + e.getMessage());
		}

	}

	private void getPackager() throws AutorizadorException {
		try {
			packagerMC = new GenericPackager(packageName);

		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao configurar o empacotador JPOS");
		}
	}

	// Verify according some rules whether is a HSM transaction, then set the
	// proper flag.
	private void checkHSMTransaction(ISOMsg isoTransaction) throws ISOException {
		String de52, mti;

		if (!isoTransaction.hasField(52) || !TransactionMonitor.isHsmEnabled) {
			mediator.setHSMTransaction(false);
			return;
		}

		mti = isoTransaction.getMTI();
		de52 = isoTransaction.getString(52);

		if ((!mti.equals(AutorizadorConstants.TRANSAC_AUTHORIZATION_TYPE)
				|| !mti.equals(AutorizadorConstants.TRANSAC_PURCHASE_TYPE)) && (de52.equals(""))) {// HSM rule			
			mediator.setHSMTransaction(false);
		} else {
			mediator.setHSMTransaction(true);
		}

	}

	// Verify according some rules whether is a Ecoscard transaction, then set
	// the
	// proper flag.
	private void checkEcoscardTransaction(ISOMsg isoTransaction) throws ISOException {
		String de22, mti;

		if (!isoTransaction.hasField(22) || !TransactionMonitor.isEcoscardEnabled) {
			mediator.setChipCardTransaction(false);
			return;
		}

		mti = isoTransaction.getMTI();
		de22 = isoTransaction.getString(22);

		if (de22.equals("")) {// Ecoscard rule.
			mediator.setChipCardTransaction(false);
		} else if (de22.startsWith("05") && (mti.equals(AutorizadorConstants.TRANSAC_PURCHASE_TYPE)
				|| mti.equals(AutorizadorConstants.TRANSAC_AUTHORIZATION_TYPE))) {
			mediator.setChipCardTransaction(true);
		} else {
			mediator.setChipCardTransaction(false);
		}
	}

	// Not used in MasterCard implementation
	@Override
	public long doTransaction() throws AutorizadorException {
		return 0;
	}

	// Not used in MasterCard implementation
	@Override
	public Long call() throws Exception {
		return null;
	}

}
