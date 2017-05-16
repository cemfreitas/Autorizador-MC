package cemfreitas.autorizador.manager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jpos.iso.ISOMsg;

import cemfreitas.autorizadorMVC.TransactionData;

/* Mediator interface.
 * Implemented by TransactionMediator class and provides service methods to all
 * classes which communicates with mediator.  
 */
public interface Mediator {	
	byte[] getTransactionFromMC();	
	InputStream getInputStream();	
	ISOMsg getIsoTransactionFromMC();		
	byte[] getTransactionFromEcoscard();	
	List<String> getDatabaseHsmReturn();	
	List<String> getDatabaseAutReturn();	
	ISOMsg getIsoTransactionFromEcoscard();	
	ISOMsg getIsoTransactionToEcoscard();	
	OutputStream getOutputStream();	
	byte[] getTransactionToMC();	
	byte[] getTransactionToEcoscard();	
	ISOMsg getIsoTransactionToMC();	
	byte[] getTransactionFromHSM();	
	byte[] getTransactionToHSM();	
	int getTransactionPhase();	
	TransactionData getTransactionData();
	long getEcoExecutionTime();
	long getHsmDbExecutionTime();
	long getHsmExecutionTime();
	long getAutDbExecutionTime();
	long getAutExecutionTime();
	Exception getAutException();
	Exception getEcoException();
	Exception getHsmException();
	
	boolean isHSMTransaction();	
	boolean isChipCardTransaction();
	boolean isHsmDisconected();
	boolean isEcoDisconected();
	boolean isTimeOut();	
	
	void setTransactionFromMC(byte[] transactionFromMC);
	void setIsoTransactionFromMC(ISOMsg isoTransactionFromMC);
	void setHSMTransaction(boolean isHSMTransaction);
	void setChipCardTransaction(boolean isChipCardTransaction);
	void setTransactionFromEcoscard(byte[] transactionFromEcoscard);
	void setDatabaseHsmReturn(List<String> databaseHsmReturn);
	void setDatabaseAutReturn(List<String> databaseAutReturn);
	void setIsoTransactionFromEcoscard(ISOMsg isoTransactionFromEcoscard);
	void setIsoTransactionToEcoscard(ISOMsg isoTransactionToEcoscard);
	void setTransactionToMC(byte[] transactionToMC);
	void setTransactionToEcoscard(byte[] transactionToEcoscard);
	void setIsoTransactionToMC(ISOMsg isoTransactionToMC);
	void setTransactionFromHSM(byte[] transactionFromHSM);
	void setTransactionToHSM(byte[] transactionToHSM);	
	void setTransactionData(TransactionData transactionData);	
	void setHsmDisconected(boolean isHsmDisconected);	
	void setEcoDisconected(boolean isEcoDisconected);	
}
