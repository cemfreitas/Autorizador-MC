package cemfreitas.autorizador.tests;

import cemfreitas.autorizador.AutorizadorDB;
import cemfreitas.autorizador.manager.TransactionManager;
import cemfreitas.autorizador.utils.AutorizadorParams;
import cemfreitas.autorizador.utils.Logging;

public class TesteServer {
	
	public static void main(String[] args) {
		AutorizadorParams.loadProperties();
		AutorizadorDB.init();
		Logging.init(false);
		new TransactionManager();
	}

}
