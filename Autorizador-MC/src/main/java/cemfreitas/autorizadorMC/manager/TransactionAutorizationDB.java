package cemfreitas.autorizadorMC.manager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;

import cemfreitas.autorizadorMC.utils.AppFunctions;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;
import cemfreitas.autorizadorMC.utils.Logging;

/* TransactionAutorizationDB class.
 * Extends the abstracted class TransactionBaseDB and provides implementations 
 * specific to the database authorization
 */
public class TransactionAutorizationDB extends TransactionBaseDB {
	private static final String storedProcedureName = AutorizadorParams.getValue("SP_Autorizacao");
	private Logger traceLog = Logging.getTrace();
	private Mediator mediator;

	public TransactionAutorizationDB(Mediator mediator) {
		super(storedProcedureName);// Send SP to be executed by superclass
		this.mediator = mediator;// Get the mediator reference
	}

	// Set the List of parameters to be executed by the SP
	// The parameters comes from Ecoscard, HSM and the Mastercard ISO
	// transaction previously unpacked.
	private void setParameters() throws AutorizadorException {

		ISOMsg transactionFromMC = mediator.getIsoTransactionFromMC();// Get MC
																		// ISO
																		// transaction.
		List<String> parametersList = new ArrayList<String>();

		parametersList.add(transactionFromMC.hasField(0) ? "1" + (String) transactionFromMC.getValue(0) : "0");
		try {
			parametersList.add(AppFunctions.dumpString(transactionFromMC.pack()));
		} catch (ISOException e) {
			throw new AutorizadorException("Erro ao empacotar campo da ISO :" + e.getMessage());
		}

		Object isoValue;
		for (int i = 2; i < 129; i++) {
			if (transactionFromMC.hasField(i)) {
				isoValue = transactionFromMC.getValue(i);
				if (isoValue instanceof byte[]) {
					parametersList.add("1" + AppFunctions.hexString((byte[]) isoValue));
				} else {
					parametersList.add("1" + (String) isoValue);
				}
			} else {
				parametersList.add("0");
			}
		}

		String de22 = parametersList.get(22);
		String de39 = "0";
		String de55 = "0";
		// If transaction was sent to Ecoscard, set response field de39 with the
		// proper value according to Ecoscard's manual.
		if (mediator.isChipCardTransaction()) {
			if (mediator.getTransactionFromEcoscard() != null) {
				ISOMsg transactionFromEco = mediator.getIsoTransactionFromEcoscard();
				de39 = transactionFromEco.hasField(39) ? "1" + (String) transactionFromEco.getValue(39) : "0";
				de55 = transactionFromEco.hasField(55) ? "1" + (String) transactionFromEco.getValue(55) : "0";
				parametersList.set(55, de55);
				if (!de39.equals("100")) {
					if (de39.equals("199")) {
						de39 = "114";
					}
					if (de39.equals("0")) {//Ecoscard responded without de39
						de39 = "183";
					}
					mediator.setTransactionFromEcoscard(null);
				}
			} else {
				de39 = "183";
			}
		} else {
			if (de22.charAt(0) == '1') {
				if ((de22.charAt(1) != '0') || (de22.charAt(2) != '2' && de22.charAt(2) != '5')) {
					de39 = "112";
				}
			}
		}

		try {
			parametersList.set(100, "1" + InetAddress.getLocalHost() + ":" + AutorizadorParams.getValue("PortaEscuta"));
		} catch (UnknownHostException e) {
			parametersList.set(100, "");
		}

		// If transaction was sent to HSM, set response field de39 with the
		// proper if HSM failed.
		if (mediator.isHSMTransaction()) {
			if (mediator.getTransactionFromHSM() != null) {
				parametersList.set(126, "1" + mediator.getDatabaseHsmReturn().get(0));
			} else {
				de39 = "183";
			}
		}
		parametersList.set(39, de39);
		setParametersList(parametersList);
	}

	// After parameters set, execute SP on database.
	@Override
	public long doTransaction() throws AutorizadorException {
		setParameters();
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Chamada da SP Autorizacao -----");
			traceLog.trace(getTransactionToTrace(getParametersList()));
		}
		long beginExecution = System.currentTimeMillis();
		send();
		receive();
		long endExecution = System.currentTimeMillis();
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" Resultado da SP Autorizacao : {}", listDbExecutionResult());
		}
		mediator.setDatabaseAutReturn(getDatabaseReturn());
		if (!mediator.getDatabaseAutReturn().get(0).equals("0")) {
			throw new AutorizadorException("SP autorizacao retornou status diferente de zero. Transacao cancelada.");
		}
		return (endExecution - beginExecution);
	}

	// For trace purposes.
	private String listDbExecutionResult() {
		StringBuffer result = new StringBuffer();
		int deNumber = 0;		
		
		for (String de : getDatabaseReturn()) {
			result.append("DE");
			result.append(deNumber++);
			result.append(" :");
			result.append(de);
			result.append(", ");
		}

		return result.substring(0, result.length() - 2);
	}

}
