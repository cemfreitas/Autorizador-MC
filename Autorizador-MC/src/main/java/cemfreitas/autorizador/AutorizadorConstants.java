package cemfreitas.autorizador;

/* AutorizadorConstants class
Constants used in the application.
*/
public class AutorizadorConstants {
	public static final Object[] TABLE_HEADER = { "", "Data", "Código", "Processo", "Valor", "Campo 63", "Estabelecimento",
			"Núm. do Cartão" };

	public static final int CLIENT_DISCONNECTED = 0;
	public static final int CLIENT_CONNECTED = 1;
	public static final int CLIENT_DISABLED = 2;
	public static final int TRANSAC_NEW_STATUS = 1;
	public static final int TRANSAC_REV_STATUS = 2;
	public static final int TRANSAC_COMP_STATUS = 3;
	public static final int TRANSAC_NOTCOMP_STATUS = 4;

	public static final String TRANSAC_AUTHORIZATION_TYPE = "0100";
	public static final String TRANSAC_PURCHASE_TYPE = "0200";
	public static final String TRANSAC_REVERSAL_TYPE = "0420";

	public static final int TRANSAC_MC_UNPACK_PHASE = 0;
	public static final int TRANSAC_COMPLETED_PHASE = 1;
	public static final int TRANSAC_AUT_ERROR_PHASE = 2;
	
	//Sets a default time out if not settled on configuration file
	//or is settled to a value < minimum allowed.
	public static final int TIMEOUT_MIN_ALLOWED_TRANSAC = 10000;
	public static final int TIMEOUT_DEFAULT_TRANSAC = 50000;
	public static final int TIMEOUT_MIN_ALLOWED_CLIENT = 3000;
	public static final int TIMEOUT_DEFAULT_CLIENT = 30000;
}
