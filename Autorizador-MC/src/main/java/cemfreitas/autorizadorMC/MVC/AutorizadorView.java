package cemfreitas.autorizadorMC.MVC;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;

/* AutorizadorView class
 * It represents the view layer of the MVC model.
 * It contains reference of other view parts to compound the main screen.   
 */
public class AutorizadorView {
	private Model model;
	private ConnectionView connectionView;
	private TransactionView transactionView;
	private LegendasView legendasView;
	private EstatisticaView estatisticaView;
	private JFrame frame;

	public AutorizadorView(Controller controller, Model model) {
		// Get the references of the MVC model and controller

		this.model = model;
		// Instantiating panels components to compound the main screen
		connectionView = new ConnectionView();
		transactionView = new TransactionView(controller);
		legendasView = new LegendasView();
		estatisticaView = new EstatisticaView();

		// Creating a panel to put the lower side components
		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new GridLayout(1, 2));
		panelSouth.add(legendasView);
		panelSouth.add(estatisticaView);

		// Display it all in a window and make the window appear
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.add(connectionView, BorderLayout.NORTH);
		frame.add(transactionView, BorderLayout.CENTER);
		frame.add(panelSouth, BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		//If window is closed.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.shutDownApplication();
			}
		});
	}

	// Changes the HSM connection status whenever the model changes
	void updateHSMConnection() {
		
		switch (model.getStatusConnHSM()) {
		case CLIENT_CONNECTED:
			connectionView.setHSMConnectionOn();
			break;
		case CLIENT_DISCONNECTED:
			connectionView.setHSMConnectionOff();
			break;
		case CLIENT_DISABLED:
			connectionView.setHSMDisabled();
			break;
		}
	}

	// Changes the Ecoscard connection status whenever the model changes
	void updateEcoscardConnection() {
		
		switch (model.getStatusConnEcoscard()) {
		case CLIENT_CONNECTED:
			connectionView.setEcoscardConnectionOn();
			break;
		case CLIENT_DISCONNECTED:
			connectionView.setEcoscardConnectionOff();
			break;
		case CLIENT_DISABLED:
			connectionView.setEcoscardDisabled();
			break;
		}		
	}

	// Insert a transaction in the model whenever the model changes
	void insertTransaction() {
		List<TransactionData> transactionList = model.getTransactionList();
		TransactionData transactionData = transactionList.get(transactionList.size() - 1);
		transactionView.insertTransaction(transactionData);
	}

	// Update a transaction status
	void updateStausTransaction(int row, TransactionStatus status) {
		transactionView.updateStatusTransaction(row, status);
	}

	// Clear the transaction table
	void clearTransactionTable() {
		transactionView.clearTable();
	}

	// Set the window title with the current version
	void setApplicationVersion(String version) {
		frame.setTitle("Autorizador MasterCard - " + version);
	}

	// Set all statistics information

	void setNumTotalTrans(String num) {
		estatisticaView.setNumTotalTrans(num);
	}

	void setNumTransCompleted(String num) {
		estatisticaView.setNumTransCompleted(num);
	}

	void setNumTransRevers(String num) {
		estatisticaView.setNumTransRevers(num);
	}

	void setNumTransInProcess(String num) {
		estatisticaView.setNumTransInProcess(num);
	}

	void setNumTransError(String num) {
		estatisticaView.setNumTransError(num);
	}

	void setCurrentDate(String date) {
		estatisticaView.setCurrentDate(date);
	}

	//Check whether exist transactions still been processed and warns before close.   
	boolean showShutdownWarningMessage() {
		int dialogResult = JOptionPane.showConfirmDialog(null,
				"Existe(m) ainda trasacao(oes) sendo processada(s).\nTem certeza que deseja sair?", "Cuidado !",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	boolean showShutdownNormalMessage() {
		int dialogResult = JOptionPane.showConfirmDialog(null,
				"O Autorizador MasterCard sera finalizado.\nTem certeza que deseja sair?", "Aviso !",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}
}
