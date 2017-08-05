package cemfreitas.autorizadorMC.manager;

import java.util.ArrayList;
import java.util.List;

import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;

import cemfreitas.autorizadorMC.utils.AppFunctions;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;
import cemfreitas.autorizadorMC.utils.Logging;

/* TransactionHsmDB class.
 * Extends abstract class TransactionBaseDB and provides further
 * implementation for HSM database transactions. 
 */
public class TransactionHsmDB extends TransactionBaseDB {
	private static final String storedProcedureName = AutorizadorParams.getValue("SP_HSM");
	private Logger traceLog = Logging.getTrace();
	private Mediator mediator;

	public TransactionHsmDB(Mediator mediator) {
		super(storedProcedureName);// Send SP to be executed to superclass
		this.mediator = mediator;// Get the mediator reference
	}

	// Execute the HSM SP call.
	@Override
	public long doTransaction() throws AutorizadorException {
		int[] isoFields = new int[] { 2, 32, 35, 41, 45, 52 };// ISO fields used
																// in the SP
																// params.

		ISOMsg transactionFromMC = mediator.getIsoTransactionFromMC(); // Get
																		// from
																		// mediator
																		// MC
																		// ISO
																		// transaction

		List<String> parametersList = new ArrayList<String>();

		// Get params data from MC ISO transaction.
		Object isoValue;
		for (int i = 0; i < isoFields.length; i++) {
			if (transactionFromMC.hasField(isoFields[i])) {
				isoValue = transactionFromMC.getValue(isoFields[i]);
				if (isoValue instanceof byte[]) {// Some fields are hexa ...
					parametersList.add(AppFunctions.hexString((byte[]) isoValue));
				} else { // others are strings.
					parametersList.add((String) isoValue);
				}
			} else {
				parametersList.add("");
			}
		}
		setParametersList(parametersList);
		if (traceLog.isTraceEnabled()) {
			traceLog.trace(" ----- Chamada da SP HSM -----");
			traceLog.trace(getTransactionToTrace(parametersList));
		}
		long beginExecution = System.currentTimeMillis();
		send();
		receive();
		long endExecution = System.currentTimeMillis();
		if (traceLog.isTraceEnabled()) {
			traceLog.trace("Resultado da {} :{}", storedProcedureName, getDatabaseReturn().get(0));
		}
		mediator.setDatabaseHsmReturn(getDatabaseReturn());// Send to mediator a
															// List of SP
															// response.
		return (endExecution - beginExecution);// Return the total execution
												// time.
	}

}
