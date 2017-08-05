package cemfreitas.autorizadorMC;

import cemfreitas.autorizadorMC.MVC.AutorizadorController;
import cemfreitas.autorizadorMC.MVC.AutorizadorModel;
import cemfreitas.autorizadorMC.MVC.Controller;
import cemfreitas.autorizadorMC.MVC.Model;
import cemfreitas.autorizadorMC.manager.AutorizadorException;
import cemfreitas.autorizadorMC.manager.TransactionManager;
import cemfreitas.autorizadorMC.manager.TransactionMonitor;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;
import cemfreitas.autorizadorMC.utils.Logging;

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
	private static final String version = "2.01";
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

	//Shutdown hook.  
	public static void shutDownApplication() throws AutorizadorException {		
		Logging.shutdown();
	}

}
