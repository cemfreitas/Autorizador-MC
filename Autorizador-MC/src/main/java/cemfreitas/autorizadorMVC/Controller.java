package cemfreitas.autorizadorMVC;

public interface Controller {
	void turnUpdateOn();
	void turnUpdateOff();
	void setConnectStatusHSM(boolean status);
	void setConnectStatusEcoscard(boolean status);
	void inertTransaction(long threadId, TransactionData transactionData);
	void updateTransactionStatus(long threadId, int status);
	void updateStatistics(TransactionStatistic statistics);
	void setApplicationVersion(String version);
	void shutDownApplication();
}
