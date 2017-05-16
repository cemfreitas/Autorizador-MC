package cemfreitas.autorizador.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cemfreitas.autorizador.AutorizadorConstants;
import cemfreitas.autorizador.Client;
import cemfreitas.autorizador.utils.AutorizadorParams;
import cemfreitas.autorizadorMVC.Controller;
import cemfreitas.autorizadorMVC.TransactionData;
import cemfreitas.autorizadorMVC.TransactionStatistic;

/* TransactionMonitor class.
 * Provides synchronized static methods to update main screen informations.
 */
public class TransactionMonitor {
	private static int transactionCounter, transactionCompletedCounter, transactionErrorCounter,
			transactionReversalCounter, transactionInProcessCounter;
	private static String today;
	private static SimpleDateFormat sdf;
	private static Controller viewController;
	private static TransactionStatistic transactionStatistic;

	public static void init(Controller controller) {
		viewController = controller;
		sdf = new SimpleDateFormat("dd/MM/yyyy");
		today = sdf.format(new Date());
		checkClientConnectioStatus();
		transactionStatistic = new TransactionStatistic();
	}

	private static void resetCounters() {
		transactionCounter = 0;
		transactionCompletedCounter = 0;
		transactionErrorCounter = 0;
		transactionReversalCounter = 0;
		transactionInProcessCounter = 0;
	}

	synchronized static private void incTransactionCounter() {
		transactionCounter++;
		transactionStatistic.setTransactionCounter(transactionCounter);
	}

	synchronized static private void incTransactionErrorCounter() {
		transactionErrorCounter++;
		transactionStatistic.setTransactionErrorCounter(transactionErrorCounter);
	}

	synchronized static private void incTransactionCompletedCounter() {
		transactionCompletedCounter++;
		transactionStatistic.setTransactionSucessCounter(transactionCompletedCounter);

	}

	synchronized static private void incTransactionReversalCounter() {
		transactionReversalCounter++;
		transactionStatistic.setTransactionReversalCounter(transactionReversalCounter);
	}

	synchronized static private void incTransactionInProcessCounter() {
		transactionInProcessCounter++;
		transactionStatistic.setTransactionInProcessCounter(transactionInProcessCounter);
	}

	synchronized private static void decTransactionInProcessCounter() {
		if (transactionInProcessCounter > 0) {
			transactionInProcessCounter--;
		}
		transactionStatistic.setTransactionInProcessCounter(transactionInProcessCounter);

	}

	synchronized public static void updateTransaction(long threadId, int status) {
		if (status == AutorizadorConstants.TRANSAC_COMP_STATUS) {
			incTransactionCompletedCounter();
		}
		if (status == AutorizadorConstants.TRANSAC_NOTCOMP_STATUS) {
			incTransactionErrorCounter();
		}

		decTransactionInProcessCounter();
		viewController.updateTransactionStatus(threadId, status);
		viewController.updateStatistics(transactionStatistic);
	}

	synchronized public static void addTransaction(long threadId, TransactionData transactionData) {
		checkDate();
		if (transactionData.getCodigo().equals(AutorizadorConstants.TRANSAC_REVERSAL_TYPE)) {// Verify
																								// whether
																								// is
																								// a
																								// reversal
			incTransactionCounter();
			incTransactionReversalCounter();
		} else {
			incTransactionCounter();
			incTransactionInProcessCounter();
		}
		transactionStatistic.setCurrentDate(today);
		viewController.inertTransaction(threadId, transactionData);
		viewController.updateStatistics(transactionStatistic);
	}

	synchronized private static void checkDate() {
		String now = sdf.format(new Date());

		if (!now.equals(today)) {
			resetCounters();
		}

	}

	synchronized static public void updateHsmConnectionStatusView(boolean status) {
		viewController.setConnectStatusHSM(status);
	}

	synchronized static public void updateEcoscardConnectionStatusView(boolean status) {
		viewController.setConnectStatusEcoscard(status);
	}

	private static void checkClientConnectioStatus() {
		String hsmIP = AutorizadorParams.getValue("IPServidorHSM");
		int hsmPort = AutorizadorParams.getValueAsInt("PortaServidorHSM");

		Client hsm = new Client(hsmIP, hsmPort, "HSM");
		try {
			hsm.clientConnect();
			updateHsmConnectionStatusView(true);
			hsm.closeConnection();			
		} catch (IOException e) {			
			updateHsmConnectionStatusView(false);
		}

		String ecoscardIP = AutorizadorParams.getValue("EcoscardIP");
		int ecoscardPort = AutorizadorParams.getValueAsInt("EcoscardPorta");

		Client ecoscard = new Client(ecoscardIP, ecoscardPort, "Ecoscard");
		try {
			ecoscard.clientConnect();
			updateEcoscardConnectionStatusView(true);
			ecoscard.closeConnection();
		} catch (IOException e) {
			updateEcoscardConnectionStatusView(false);
		}
	}

}
