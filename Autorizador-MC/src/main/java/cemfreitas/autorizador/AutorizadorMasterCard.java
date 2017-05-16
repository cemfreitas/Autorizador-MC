package cemfreitas.autorizador;

import cemfreitas.autorizador.manager.AutorizadorException;
import cemfreitas.autorizador.manager.TransactionManager;
import cemfreitas.autorizador.manager.TransactionMonitor;
import cemfreitas.autorizador.utils.AutorizadorParams;
import cemfreitas.autorizador.utils.Logging;
import cemfreitas.autorizadorMVC.AutorizadorController;
import cemfreitas.autorizadorMVC.AutorizadorModel;
import cemfreitas.autorizadorMVC.Controller;
import cemfreitas.autorizadorMVC.Model;

/* AutorizadorMasterCard application.
 * Provides message exchange services to 
 * process ISO8583 transaction from MasterCard (MC).
 *  
 *  Main modules:
 *  
 * TransactionManager (TM) - Establish a server socket to receive messages
 * from MC then delegates to a madiator to process them.
 * 
 *  Mediator - Processes ISO transactions from TM. Acts as a mediator among several 
 *  service classes which provide transaction services such as pack, unpack, send to client, etc.
 *  
 *  Monitor - Monitors all transaction flows and update the main screen with current
 *  transactions and some statistics. 
 *     
 */
public class AutorizadorMasterCard {
	private static final String version = "2.0";
	private static Model autorizadorModel;
	private static Controller autorizadorController;
	private static TransactionManager transactionManager;

	//main method
	public static void main(String[] args) {
		AutorizadorParams.loadProperties();//Load configuration parameters.
		AutorizadorDB.init();//Initialize database. 
		if (args.length == 1) {//Check whether trace should be on
			if (args[0].equalsIgnoreCase("TRACEON")) {
				Logging.init(true);
			}
		} else {
			Logging.init(false);
		}
		autorizadorModel = new AutorizadorModel();//Instantiate application model
		autorizadorController = new AutorizadorController(autorizadorModel);//Instantiate application controller 
		autorizadorController.setApplicationVersion(version);//Set current version
		TransactionMonitor.init(autorizadorController);//Initialize transaction monitor
		transactionManager = new TransactionManager();//Perform a server to accept		
		transactionManager.perform();                 // and process MC messages.
	}

	//Shutdown hook. Perform some tasks before close application. 
	public static void shutDownApplication() throws AutorizadorException {
		transactionManager.closeConnection();
		Logging.shutdown();
	}

}
