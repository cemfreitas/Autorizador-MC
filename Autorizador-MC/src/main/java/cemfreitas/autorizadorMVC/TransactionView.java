package cemfreitas.autorizadorMVC;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import cemfreitas.autorizador.AutorizadorConstants;

/* This class compounds one of the four parts on the main screen.   
 * It shows the transaction panel on the main screen. 
 */

public class TransactionView extends JPanel implements ActionListener {	
	private static final long serialVersionUID = -3928316701524641490L;
	private JTable transactionTable;
	private DefaultTableModel tableModel;
	private JCheckBox onOffTrans;
	private Controller controller;

	public TransactionView(Controller controller) {
		this.controller = controller;
		// Creating table model
		tableModel = new DefaultTableModel(null, AutorizadorConstants.TABLE_HEADER);
		// Instantiate customized JTable class
		transactionTable = new TransactionTable(tableModel);

		// Creating other views swing UI components
		onOffTrans = new JCheckBox("Liga atualização: ");
		onOffTrans.setHorizontalTextPosition(SwingConstants.LEFT);
		onOffTrans.setSelected(true);
		onOffTrans.addActionListener(this);

		// Setting border and layout
		JScrollPane tableScrollPane = new JScrollPane(transactionTable);
		tableScrollPane.setPreferredSize(new Dimension(700, 380));
		tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				" Transações em tempo real ", TitledBorder.CENTER, TitledBorder.TOP));
		
		setLayout(new BorderLayout());
		add(onOffTrans, BorderLayout.NORTH);
		add(tableScrollPane, BorderLayout.SOUTH);
	}

	// Insert a transaction into the table
	void insertTransaction(TransactionData transactionData) {
		int i = tableModel.getRowCount();
		Object[] row = new Object[8];
		row[0] = transactionData.getStatus();
		row[1] = transactionData.getData();
		row[2] = transactionData.getCodigo();
		row[3] = transactionData.getProcesso();
		row[4] = transactionData.getValor();
		row[5] = transactionData.getNSU();
		row[6] = transactionData.getEstabelecimento();
		row[7] = transactionData.getNumCartao();		
		
		transactionTable.scrollRectToVisible(new Rectangle(transactionTable.getCellRect(i, 0, true)));
		tableModel.addRow(row);
	}

	// Update a transaction when its status changes
	void updateStatusTransaction(int row, int status) {
		if (row < tableModel.getRowCount()) {
			if (!tableModel.getValueAt(row, 0).equals(AutorizadorConstants.TRANSAC_REV_STATUS)) {// Reversal
																									// transactions
																									// should
																									// not
																									// be
																									// updated
				tableModel.setValueAt(status, row, 0);
			}
		}

	}

	// Clear the table when riches a determined size
	void clearTable() {
		tableModel.setRowCount(0);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == onOffTrans) {
			if (onOffTrans.isSelected()) {
				controller.turnUpdateOn();
			} else {
				controller.turnUpdateOff();
			}
		}
		
	}
}
