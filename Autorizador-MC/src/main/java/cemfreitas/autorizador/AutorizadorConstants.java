package cemfreitas.autorizador;

/* AutorizadorConstants class
Constants used in the application.
*/
public class AutorizadorConstants {
	public static final Object[] TABLE_HEADER = { "", "Data", "Código", "Processo", "Valor", "NSU", "Estabelecimento",
			"Núm. do Cartão" };

	public static final boolean CONNECTED = true;
	public static final boolean NOT_CONNECTED = false;
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
}
