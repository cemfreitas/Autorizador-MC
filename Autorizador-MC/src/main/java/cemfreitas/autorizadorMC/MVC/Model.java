package cemfreitas.autorizadorMC.MVC;

import java.util.List;

import cemfreitas.autorizadorMC.AutorizadorConstants.ClientConnectionStatus;
import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;

public interface Model {
	
	void initialize();	
	void setStatusConnHSM(ClientConnectionStatus status);
	void setStatusConnEcoscard(ClientConnectionStatus status);		
	void setOnOffUpdateTrans(boolean update);	
	void setCurrentDate(String currentDate);
	void setNumTotalTrans(int numTotalTrans);
	void setNumTransCompleted(int numTransCompleted);
	void setNumTransError(int numTransError);
	void setNumTransReversal(int numTransReversal);
	void setNumTransInProcess(int numTransInProcess);	
	void insertTransaction(long threadId, TransactionData transaction);
	int updateTransactionStatus(long threadId, TransactionStatus status);
	ClientConnectionStatus getStatusConnHSM();
	ClientConnectionStatus getStatusConnEcoscard();
	boolean getOnOffUpdateTrans();
	String getCurrentDate();
	int getNumTotalTrans();
	int getNumTransCompleted();
	int getNumTransError();
	int getNumTransReversal();
	int getNumTransInProcess();	
	List<TransactionData> getTransactionList();	
}

