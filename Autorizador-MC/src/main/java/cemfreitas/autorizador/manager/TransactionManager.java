package cemfreitas.autorizador.manager;

import java.awt.Frame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import cemfreitas.autorizador.AutorizadorConstants;
import cemfreitas.autorizador.utils.AutorizadorLog;
import cemfreitas.autorizador.utils.AutorizadorParams;

/* TransactionManager Class.
 * Implements a server to receive MasterCard (MC) transactions.
 * For each connection received, starts a thread to process the transaction.
 */
public class TransactionManager {
	//Socked parameters.
	private static final int NUMBER_MAX_CONNECTION = 1000;
	private static final int LISTENING_PORT = AutorizadorParams.getValueAsInt("PortaEscuta");

	//Connection variables. 
	private InputStream inputStream;
	private OutputStream outputStream;
	private ServerSocket server;
	private Socket connection;

	//Called by main method, execute a server socket. 
	public void perform() {
		try {
			server = new ServerSocket(LISTENING_PORT, NUMBER_MAX_CONNECTION);
		} catch (IOException e) {
			//If server socked could not be established, exit application. 
			JOptionPane.showMessageDialog(new Frame(), "Erro ao escutar na porta : " + LISTENING_PORT, "Portal Card",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return;
		}

		while (true) {

			try {
				// Waiting for MC connections 
				connection = server.accept();
				// Get streams
				inputStream = connection.getInputStream();
				outputStream = connection.getOutputStream();
			} catch (IOException e) {
				return;
			}
			//Starts thread to process transaction.
			Transaction transaction = new Transaction(inputStream, outputStream);
			transaction.start();
		}
	}

	/*
	 * Inner class used to process a MC transaction. Instantiates a mediator
	 * class and give it the connection streams, the mediator is responsible for
	 * process the transaction.
	 * 
	 * It is notified by mediator whenever the transaction status changes.
	 */

	private class Transaction extends Thread implements Observer {
		private InputStream inputStream;
		private OutputStream outputStream;

		//Receives connection streams from outer class.
		Transaction(InputStream inputstream, OutputStream outputStream) {
			this.inputStream = inputstream;
			this.outputStream = outputStream;
		}

		@Override
		public void run() {
			Manager transaction = new TransactionMediator(inputStream, outputStream);
			transaction.addObservable(this);
			transaction.perform();
		}

		/*
		 * Observer method implementation. Receives the mediator notifications.
		 * *
		 */
		@Override
		public void update(Observable mediatorPushed, Object threadPushed) {
			AutorizadorLog autorizadorLog;
			if (mediatorPushed instanceof Mediator) {
				Mediator mediator = (Mediator) mediatorPushed;
				long threadId = (long) threadPushed;
				int currentPhase = mediator.getTransactionPhase();
				switch (currentPhase) {
				case AutorizadorConstants.TRANSAC_MC_UNPACK_PHASE:
					// Add transaction data to main screen;
					TransactionMonitor.addTransaction(threadId, mediator.getTransactionData());
					break;
				case AutorizadorConstants.TRANSAC_COMPLETED_PHASE:
					// Update monitor with completed status transaction
					TransactionMonitor.updateTransaction(threadId, AutorizadorConstants.TRANSAC_COMP_STATUS);
					//When transaction process ends, send a mediator reference to build the log informations. 
					autorizadorLog = new AutorizadorLog(mediator);
					autorizadorLog.build();
					autorizadorLog.log();
					break;
				case AutorizadorConstants.TRANSAC_AUT_ERROR_PHASE:
					// Update monitor with completed status transaction
					TransactionMonitor.updateTransaction(threadId, AutorizadorConstants.TRANSAC_NOTCOMP_STATUS);
					//When transaction process ends, send a mediator reference to build the log informations.
					autorizadorLog = new AutorizadorLog(mediator);
					autorizadorLog.build();
					autorizadorLog.log();
					break;
				}
				//For each notifications, checks the ecoscard and HSM connections
				// and update main screen.
				if (mediator.isEcoDisconected()) {
					TransactionMonitor.updateEcoscardConnectionStatusView(false);
				} else {
					TransactionMonitor.updateEcoscardConnectionStatusView(true);
				}
				if (mediator.isHsmDisconected()) {
					TransactionMonitor.updateHsmConnectionStatusView(false);
				} else {
					TransactionMonitor.updateHsmConnectionStatusView(true);
				}
			}
		}
	}

	//Close connection with MC. 
	//Used at shutdown process.
	public void closeConnection() throws AutorizadorException {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (IOException e) {
			throw new AutorizadorException(e);
		}
	}
}
