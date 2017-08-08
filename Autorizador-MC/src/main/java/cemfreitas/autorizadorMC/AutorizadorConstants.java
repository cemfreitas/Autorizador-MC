package cemfreitas.autorizadorMC;

/* AutorizadorConstants class
Constants used in the application.
*/
public class AutorizadorConstants {
	public static final Object[] TABLE_HEADER = { "", "Data", "Código", "Processo", "Valor", "Campo 63", "Estabelecimento",
			"Núm. do Cartão" };	
	
	public static enum ClientConnectionStatus {
		CLIENT_DISCONNECTED, CLIENT_CONNECTED, CLIENT_DISABLED
	}	
	
	public static enum TransactionStatus {
		TRANSAC_NEW, TRANSAC_REVERTED, TRANSAC_COMPLETED, TRANSAC_NOT_COMPLETED
	}

	public static final String TRANSAC_AUTHORIZATION_TYPE = "0100";
	public static final String TRANSAC_PURCHASE_TYPE = "0200";
	public static final String TRANSAC_REVERSAL_TYPE = "0420";	
	
	public static enum TransactionPhase {
		TRANSAC_UNPACK, TRANSAC_COMPLETED, TRANSAC_AUT_ERROR
	}
	
	
	//Sets a default time out if not settled on configuration file
	//or is settled to a value < minimum allowed.
	public static final int TIMEOUT_MIN_ALLOWED_TRANSAC = 10000;
	public static final int TIMEOUT_DEFAULT_TRANSAC = 50000;
	public static final int TIMEOUT_MIN_ALLOWED_CLIENT = 3000;
	public static final int TIMEOUT_DEFAULT_CLIENT = 30000;
}
