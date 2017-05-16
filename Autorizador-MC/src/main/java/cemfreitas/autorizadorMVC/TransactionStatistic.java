package cemfreitas.autorizadorMVC;

import java.io.Serializable;

/* POJO class used to hold statistics informations showed   
 *  on the main screen.
 */

public class TransactionStatistic implements Serializable {
	private static final long serialVersionUID = -7601127520114851924L;
	private int transactionCounter, transactionSucessCounter, transactionErrorCounter, transactionReversalCounter,
			transactionInProcessCounter;
	private String currentDate;

	public int getTransactionErrorCounter() {
		return transactionErrorCounter;
	}

	public void setTransactionErrorCounter(int transactionErrorCounter) {
		this.transactionErrorCounter = transactionErrorCounter;
	}

	public int getTransactionCounter() {
		return transactionCounter;
	}

	public void setTransactionCounter(int transactionCounter) {
		this.transactionCounter = transactionCounter;
	}

	public int getTransactionSucessCounter() {
		return transactionSucessCounter;
	}

	public void setTransactionSucessCounter(int transactionSucessCounter) {
		this.transactionSucessCounter = transactionSucessCounter;
	}

	public int getTransactionReversalCounter() {
		return transactionReversalCounter;
	}

	public void setTransactionReversalCounter(int transactionReversalCounter) {
		this.transactionReversalCounter = transactionReversalCounter;
	}

	public int getTransactionInProcessCounter() {
		return transactionInProcessCounter;
	}

	public void setTransactionInProcessCounter(int transactionInProcessCounter) {
		this.transactionInProcessCounter = transactionInProcessCounter;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

}
