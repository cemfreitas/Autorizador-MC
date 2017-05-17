package cemfreitas.autorizadorMVC;

public interface Controller {
	void turnUpdateOn();
	void turnUpdateOff();
	void setConnectStatusHSM(int status);
	void setConnectStatusEcoscard(int status);
	void inertTransaction(long threadId, TransactionData transactionData);
	void updateTransactionStatus(long threadId, int status);
	void updateStatistics(TransactionStatistic statistics);
	void setApplicationVersion(String version);
	void shutDownApplication();
}
