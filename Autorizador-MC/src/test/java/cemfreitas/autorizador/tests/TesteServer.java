package cemfreitas.autorizador.tests;

import cemfreitas.autorizadorMC.AutorizadorDB;
import cemfreitas.autorizadorMC.manager.TransactionManager;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;
import cemfreitas.autorizadorMC.utils.Logging;

public class TesteServer {
	
	public static void main(String[] args) {
		AutorizadorParams.loadProperties();
		AutorizadorDB.init();
		Logging.init(false);
		new TransactionManager();
	}

}
