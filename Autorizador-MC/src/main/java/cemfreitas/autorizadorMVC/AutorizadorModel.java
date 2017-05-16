package cemfreitas.autorizadorMVC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* AutorizadorModel class
 * It represents the model layer of the MVC model.
 * It contains all data showed on the view parts.
 * It implements an interface which dictates its behavior. *        
 */
public class AutorizadorModel implements Model {
	private boolean statusConnHSM, statusConnEcoscard, onOffUpdate;
	private String currentDate;
	private int numTotalTrans, numTransCompleted, numTransError, numTransReversal, numTransInProcess;
	private ArrayList<TransactionData> transactionList;
	private Map<Long, Integer> threadIndexMap;

	//Initialize states
	@Override
	public void initialize() {
		onOffUpdate = true;
		transactionList = new ArrayList<TransactionData>();
		threadIndexMap = new HashMap<Long, Integer>();
	}

	//Set the HSM connection status. Also notify the view layer
	@Override
	public void setStatusConnHSM(boolean status) {
		statusConnHSM = status;
	}

	//Set the Ecoscard connection status. Also notify the view layer
	@Override
	public void setStatusConnEcoscard(boolean status) {
		statusConnEcoscard = status;
	}

	//Insert a transaction in a List and also maps it with its thread id.
	@Override
	public void insertTransaction(long threadId, TransactionData transaction) {
		synchronized (this) {
			transactionList.add(transaction);
			threadIndexMap.put(threadId, transactionList.size() - 1);
		}
	}

	
	//Turn on/off the table update
	@Override
	public void setOnOffUpdateTrans(boolean update) {
		onOffUpdate = update;
	}

	@Override
	public boolean getOnOffUpdateTrans() {
		return onOffUpdate;
	}

	//Set statistic informations
	
	@Override
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;

	}

	@Override
	public void setNumTotalTrans(int numTotalTrans) {
		this.numTotalTrans = numTotalTrans;

	}

	@Override
	public void setNumTransCompleted(int numTransCompleted) {
		this.numTransCompleted = numTransCompleted;

	}
	
	@Override
	public void setNumTransError(int numTransError) {
		this.numTransError = numTransError;
	}

	@Override
	public void setNumTransReversal(int numTransReversal) {
		this.numTransReversal = numTransReversal;

	}

	@Override
	public void setNumTransInProcess(int numTransInProcess) {
		this.numTransInProcess = numTransInProcess;

	}
	
	//Get statistics informations 

	@Override
	public String getCurrentDate() {
		return currentDate;
	}

	@Override
	public int getNumTotalTrans() {
		return numTotalTrans;
	}

	@Override
	public int getNumTransCompleted() {
		return numTransCompleted;
	}
	
	@Override
	public int getNumTransError() {
		return numTransError;
	}	

	@Override
	public int getNumTransReversal() {
		return numTransReversal;
	}

	@Override
	public int getNumTransInProcess() {
		return numTransInProcess;
	}

	@Override
	public boolean getStatusConnHSM() {
		return statusConnHSM;
	}

	@Override
	public boolean getStatusConnEcoscard() {
		return statusConnEcoscard;
	}

	@Override
	public List<TransactionData> getTransactionList() {
		return transactionList;
	}
	
	//Update the transaction status on the List based on its thread id
	@Override
	public int updateTransactionStatus(long threadId, int status) {
		Integer index;
		synchronized (this) {
			index = threadIndexMap.get(threadId);

			if (index == null) {
				return Integer.MAX_VALUE;
			}
			
			TransactionData transactionData = transactionList.get(index);
			transactionData.setStatus(status);
			transactionList.set(index, transactionData);
		}
		return index;
	}	
}
