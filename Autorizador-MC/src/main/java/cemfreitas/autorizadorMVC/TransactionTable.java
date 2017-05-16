package cemfreitas.autorizadorMVC;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import cemfreitas.autorizador.AutorizadorConstants;

/* TransactionTable class
 *  It represents a JTable instance, but customized to show transactions and
 *  its status color.  
 */
public class TransactionTable extends JTable {	
	private static final long serialVersionUID = -7296753984205933682L;

	public TransactionTable(DefaultTableModel model) {
		super(model);
		configure();
	}

	/*
	 * Method prepareRenderer overrided from superclass to show different color
	 * on the first column depending on the transaction status
	 * 
	 */
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component comp=null;
		Object value;
		try {			
			comp = super.prepareRenderer(renderer, row, col);
			value = getModel().getValueAt(row, col);
			
			if (value != null && col == 0) { // Status on the first column (0)
				switch ((int) value) {
				case AutorizadorConstants.TRANSAC_NEW_STATUS:
					comp.setBackground(Color.CYAN);
					comp.setForeground(Color.CYAN);
					break;
				case AutorizadorConstants.TRANSAC_REV_STATUS:
					comp.setBackground(Color.YELLOW);
					comp.setForeground(Color.YELLOW);
					break;
				case AutorizadorConstants.TRANSAC_COMP_STATUS:
					comp.setBackground(Color.GREEN);
					comp.setForeground(Color.GREEN);
					break;
				case AutorizadorConstants.TRANSAC_NOTCOMP_STATUS:
					comp.setBackground(Color.RED);
					comp.setForeground(Color.RED);
					break;
				default:
					comp.setBackground(Color.WHITE);
				}
			} else {
				comp.setBackground(Color.WHITE);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
		return comp;
	}

	private void configure() { // Set each column Width to an appropriate size
		getColumnModel().getColumn(0).setPreferredWidth(10);// Status
		getColumnModel().getColumn(1).setPreferredWidth(85);// Data
		getColumnModel().getColumn(2).setPreferredWidth(50);// Codigo
		getColumnModel().getColumn(3).setPreferredWidth(50);// Processo
		getColumnModel().getColumn(4).setPreferredWidth(50);// Valor
		getColumnModel().getColumn(5).setPreferredWidth(50);// NSU
		getColumnModel().getColumn(6).setPreferredWidth(110);// Estabelecimento
		getColumnModel().getColumn(7).setPreferredWidth(120);// Num. do cartao
		for (int i = 0; i < 8; i++) { // Then centralize its text
			TableColumn col = getColumnModel().getColumn(i);
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			col.setCellRenderer(dtcr);
		}
	}
}
