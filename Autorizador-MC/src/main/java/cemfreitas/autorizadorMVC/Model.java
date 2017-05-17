package cemfreitas.autorizadorMVC;

import java.util.List;

public interface Model {
	
	void initialize();	
	void setStatusConnHSM(int status);
	void setStatusConnEcoscard(int status);		
	void setOnOffUpdateTrans(boolean update);	
	void setCurrentDate(String currentDate);
	void setNumTotalTrans(int numTotalTrans);
	void setNumTransCompleted(int numTransCompleted);
	void setNumTransError(int numTransError);
	void setNumTransReversal(int numTransReversal);
	void setNumTransInProcess(int numTransInProcess);	
	void insertTransaction(long threadId, TransactionData transaction);
	int updateTransactionStatus(long threadId, int status);
	int getStatusConnHSM();
	int getStatusConnEcoscard();
	boolean getOnOffUpdateTrans();
	String getCurrentDate();
	int getNumTotalTrans();
	int getNumTransCompleted();
	int getNumTransError();
	int getNumTransReversal();
	int getNumTransInProcess();	
	List<TransactionData> getTransactionList();	
}

