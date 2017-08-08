package cemfreitas.autorizadorMC.MVC;

import cemfreitas.autorizadorMC.AutorizadorConstants.ClientConnectionStatus;
import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;

public interface Controller {
	void turnUpdateOn();
	void turnUpdateOff();
	void setConnectStatusHSM(ClientConnectionStatus status);
	void setConnectStatusEcoscard(ClientConnectionStatus status);
	void inertTransaction(long threadId, TransactionData transactionData);
	void updateTransactionStatus(long threadId, TransactionStatus status);
	void updateStatistics(TransactionStatistic statistics);
	void setApplicationVersion(String version);
	void shutDownApplication();
}
