package cemfreitas.autorizadorMC.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cemfreitas.autorizadorMC.AutorizadorConstants;
import cemfreitas.autorizadorMC.AutorizadorConstants.ClientConnectionStatus;
import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;
import cemfreitas.autorizadorMC.Client;
import cemfreitas.autorizadorMC.MVC.Controller;
import cemfreitas.autorizadorMC.MVC.TransactionData;
import cemfreitas.autorizadorMC.MVC.TransactionStatistic;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;

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
	public static boolean isHsmEnabled, isEcoscardEnabled;

	public static void init(Controller controller) {
		viewController = controller;
		sdf = new SimpleDateFormat("dd/MM/yyyy");
		today = sdf.format(new Date());
		checkHsmStatus();
		checkEcoscardStatus();
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

	synchronized public static void updateTransaction(long threadId, TransactionStatus status) {
		if (status == TransactionStatus.TRANSAC_COMPLETED) {
			incTransactionCompletedCounter();
		}
		if (status == TransactionStatus.TRANSAC_NOT_COMPLETED) {
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

	synchronized static public void updateHsmConnectionStatusView(ClientConnectionStatus status) {
		viewController.setConnectStatusHSM(status);
	}

	synchronized static public void updateEcoscardConnectionStatusView(ClientConnectionStatus status) {
		viewController.setConnectStatusEcoscard(status);
	}

	private static void checkHsmStatus() {
		String hsmIP = AutorizadorParams.getValue("IPServidorHSM");
		String hsmSP = AutorizadorParams.getValue("SP_HSM");
		int hsmPort = AutorizadorParams.getValueAsInt("PortaServidorHSM");

		if (hsmIP.equals("") || hsmSP.equals("") || hsmPort == 0) {
			isHsmEnabled = false;
			updateHsmConnectionStatusView(ClientConnectionStatus.CLIENT_DISABLED);
			return;
		}
		isHsmEnabled = true;
		Client hsm = new Client(hsmIP, hsmPort, "HSM");
		try {
			hsm.clientConnect();
			updateHsmConnectionStatusView(ClientConnectionStatus.CLIENT_CONNECTED);
			hsm.closeConnection();			
		} catch (IOException e) {
			updateHsmConnectionStatusView(ClientConnectionStatus.CLIENT_DISCONNECTED);
		}
	}

	private static void checkEcoscardStatus() {
		String ecoscardIP = AutorizadorParams.getValue("EcoscardIP");
		int ecoscardPort = AutorizadorParams.getValueAsInt("EcoscardPorta");		
		
		if (ecoscardIP.equals("") || ecoscardPort == 0) {			
			isEcoscardEnabled = false;
			updateEcoscardConnectionStatusView(ClientConnectionStatus.CLIENT_DISABLED);
			return;
		}
		isEcoscardEnabled = true;
		Client ecoscard = new Client(ecoscardIP, ecoscardPort, "Ecoscard");
		try {
			ecoscard.clientConnect();
			updateEcoscardConnectionStatusView(ClientConnectionStatus.CLIENT_CONNECTED);
			ecoscard.closeConnection();			
		} catch (IOException e) {
			updateEcoscardConnectionStatusView(ClientConnectionStatus.CLIENT_DISCONNECTED);
		}
	}

}
