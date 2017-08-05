package cemfreitas.autorizadorMC.manager;

import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import cemfreitas.autorizadorMC.AutorizadorConstants;
import cemfreitas.autorizadorMC.utils.AppFunctions;
import cemfreitas.autorizadorMC.utils.AutorizadorLog;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;

/* TransactionManager Class.
 * Implements a server to receive MasterCard (MC) transactions.
 * For each connection received, starts a thread to process the transaction.
 */
public class TransactionManager {
	//Socked parameters.
	private static final int NUMBER_MAX_CONNECTION = 1000;
	private static final int LISTENING_PORT = AutorizadorParams.getValueAsInt("PortaEscuta");
	private static final int ISO_MIN_MESSAGE_LENGTH = 14;

	//Connection variables.	
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

			} catch (IOException e) {
				continue;
			}
			//Starts thread to process transaction.
			Transaction transaction = new Transaction(connection);
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
		Socket connection;		

		//Receives connection streams from outer class.
		Transaction(Socket connection) {
			this.connection = connection;
		}

		@Override
		public void run() {
			try {
				byte[] mcMessage = receiveMessage();
				if (mcMessage != null) {//If is a valid message. 
					Manager transaction = new TransactionMediator(mcMessage, connection);
					transaction.addObservable(this);//Register as an Observable  
					transaction.perform();
				}
			} catch (IOException e) {
				return;
			}
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
				//For each notifications other than MC_UNPACK_PHASE, checks the ecoscard and HSM connections and update main screen.
				if (currentPhase != AutorizadorConstants.TRANSAC_MC_UNPACK_PHASE) {
					if (mediator.isEcoDisconected()) {
						TransactionMonitor.updateEcoscardConnectionStatusView(AutorizadorConstants.CLIENT_DISCONNECTED);
					} else {
						TransactionMonitor.updateEcoscardConnectionStatusView(AutorizadorConstants.CLIENT_CONNECTED);
					}
					if (mediator.isHsmDisconected()) {
						TransactionMonitor.updateHsmConnectionStatusView(AutorizadorConstants.CLIENT_DISCONNECTED);
					} else {
						TransactionMonitor.updateHsmConnectionStatusView(AutorizadorConstants.CLIENT_CONNECTED);
					}
				}
			}
		}

		//Receives message from MC
		private byte[] receiveMessage() {
			byte[] message = null;
			byte pbyte = 0;
			InputStream inputStream;
			try {
				inputStream = connection.getInputStream();
				pbyte = (byte) inputStream.read();
				message = new byte[inputStream.available()];
				inputStream.read(message);

				message = AppFunctions.concatenate(pbyte, message);

				if (message.length < ISO_MIN_MESSAGE_LENGTH) {//Test whether is an ISO transaction.
					message = null;
				}

			} catch (IOException e) {
				message = null;
			}

			return message;
		}
	}
}
