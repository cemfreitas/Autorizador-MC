package cemfreitas.autorizadorMC.MVC;

import cemfreitas.autorizadorMC.AutorizadorConstants.ClientConnectionStatus;
import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;
import cemfreitas.autorizadorMC.AutorizadorMasterCard;
import cemfreitas.autorizadorMC.manager.AutorizadorException;
import cemfreitas.autorizadorMC.utils.AppFunctions;

/* AutorizadorController class
 * It represents the controller layer of the MVC model.
 * It permits to manipulates all functionality of the MVC model.
 * It controls the "conversation" between the model and view layers. 
 * It implements an interface which dictates its behavior. *        
 */
public class AutorizadorController implements Controller {
	private Model autorizadorModel;
	private AutorizadorView autorizadorView;
	private boolean enableTransaction;
	private final static int numMaxTransactionTable = 500; //Num. max of rows on transaction table 

	public AutorizadorController(Model model) {
		// Get the references of the MVC model
		this.autorizadorModel = model;
		model.initialize();
		// and instantiates the view
		autorizadorView = new AutorizadorView(this, model);
		// Turn update on by default
		enableTransaction = true;
	}

	// Turn the transaction table updates on
	@Override
	public void turnUpdateOn() {
		enableTransaction = true;

	}

	// Turn the transaction table updates off
	@Override
	public void turnUpdateOff() {
		enableTransaction = false;
	}

	// Update the HSM connection status
	@Override
	public void setConnectStatusHSM(ClientConnectionStatus status) {		
		if (autorizadorModel.getStatusConnHSM() != status
				&& autorizadorModel.getStatusConnHSM() != ClientConnectionStatus.CLIENT_DISABLED) {// Check whether status has changed or Hsm not disable
			autorizadorModel.setStatusConnHSM(status);
			autorizadorView.updateHSMConnection();
		}
	}

	// Update the Ecoscard connection status
	@Override
	public void setConnectStatusEcoscard(ClientConnectionStatus status) {
		if (autorizadorModel.getStatusConnEcoscard() != status
				&& autorizadorModel.getStatusConnEcoscard() != ClientConnectionStatus.CLIENT_DISABLED) {// Check whether status has changed or Ecoscard not disable
			autorizadorModel.setStatusConnEcoscard(status);
			autorizadorView.updateEcoscardConnection();
		}
	}

	// Insert a transaction into the table view
	@Override
	public void inertTransaction(long threadId, TransactionData transactionData) {
		int tableSize = autorizadorModel.getTransactionList().size();
		// when it reaches the max stipulated size then reset it
		if (tableSize == numMaxTransactionTable) {
			autorizadorModel.initialize();
			autorizadorView.clearTransactionTable();
		}
		// If checkbox 'enable transaction' is on ...
		if (enableTransaction) {
			autorizadorModel.insertTransaction(threadId, transactionData);
			autorizadorView.insertTransaction();
		}
	}

	// Update the transaction status based on its thread id
	@Override
	public void updateTransactionStatus(long threadId, TransactionStatus status) {
		int index;

		index = autorizadorModel.updateTransactionStatus(threadId, status);
		autorizadorView.updateStausTransaction(index, status);

	}

	// Update all statistic information
	@Override
	public void updateStatistics(TransactionStatistic statistics) {
		autorizadorModel.setNumTotalTrans(statistics.getTransactionCounter());
		autorizadorModel.setNumTransCompleted(statistics.getTransactionSucessCounter());
		autorizadorModel.setNumTransError(statistics.getTransactionErrorCounter());
		autorizadorModel.setNumTransReversal(statistics.getTransactionReversalCounter());
		autorizadorModel.setNumTransInProcess(statistics.getTransactionInProcessCounter());
		autorizadorModel.setCurrentDate(statistics.getCurrentDate());

		updateStatisticView();
	}

	@Override
	public void setApplicationVersion(String version) {
		autorizadorView.setApplicationVersion(version);
	}

	private void updateStatisticView() {
		autorizadorView.setNumTotalTrans(String.valueOf(autorizadorModel.getNumTotalTrans()));
		autorizadorView.setNumTransCompleted(String.valueOf(autorizadorModel.getNumTransCompleted()));
		autorizadorView.setNumTransError(String.valueOf(autorizadorModel.getNumTransError()));
		autorizadorView.setNumTransRevers(String.valueOf(autorizadorModel.getNumTransReversal()));
		autorizadorView.setNumTransInProcess(String.valueOf(autorizadorModel.getNumTransInProcess()));
		autorizadorView.setCurrentDate(autorizadorModel.getCurrentDate());
	}

	//Called when application is closed.
	@Override
	public void shutDownApplication() {
		boolean response;
		if (autorizadorModel.getNumTransInProcess() > 0) {
			response = autorizadorView.showShutdownWarningMessage();
		} else {
			response = autorizadorView.showShutdownNormalMessage();
		}
		if (!response) {
			return;
		}
		try {
			AutorizadorMasterCard.shutDownApplication();
			System.exit(0);
		} catch (AutorizadorException e) {
			AppFunctions.showErroMessage("Erro ao sair da aplicacao :", e);
			System.exit(1);
		}
	}
}
