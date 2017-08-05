package cemfreitas.autorizadorMC.MVC;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/* This class compounds one of the four parts on the main screen.   
 * It shows the statistic panel on the main screen 
 */
public class EstatisticaView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2229253655687001447L;
	private JLabel lbTotalTrans, lbCompSucess, lbCompError, lbTransDesf, lbTransInProcess, lbCurrentDate;	

	public EstatisticaView() {
		// Setting border and layout
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Estatísticas ",
				TitledBorder.CENTER, TitledBorder.TOP));
		BoxLayout layout1 = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout1);

		// Create views swing UI components
		JPanel panelEst0 = new JPanel();
		panelEst0.setLayout(new BoxLayout(panelEst0, BoxLayout.LINE_AXIS));
		panelEst0.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel labelMsg1 = new JLabel("Transações referente ao dia ");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		lbCurrentDate = new JLabel(sdf.format(new Date()));
		Font font = lbCurrentDate.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize() + 2);
		lbCurrentDate.setFont(boldFont);

		JLabel labelMsg3 = new JLabel(" (00:00 às 23:59)");

		panelEst0.add(labelMsg1);
		panelEst0.add(lbCurrentDate);
		panelEst0.add(labelMsg3);

		JPanel panelEst1 = new JPanel();
		panelEst1.setLayout(new BoxLayout(panelEst1, BoxLayout.LINE_AXIS));
		panelEst1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelEst1.add(new JLabel("Núm. total de transações: "));
		lbTotalTrans = new JLabel("0");

		lbTotalTrans.setFont(boldFont);
		panelEst1.add(lbTotalTrans);

		JPanel panelEst2 = new JPanel();
		panelEst2.setLayout(new BoxLayout(panelEst2, BoxLayout.LINE_AXIS));
		panelEst2.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelEst2.add(new JLabel("Transações completadas com sucesso: "));
		lbCompSucess = new JLabel("0");
		lbCompSucess.setFont(boldFont);
		panelEst2.add(lbCompSucess);

		JPanel panelEst3 = new JPanel();
		panelEst3.setLayout(new BoxLayout(panelEst3, BoxLayout.LINE_AXIS));
		panelEst3.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelEst3.add(new JLabel("Transações completadas com erro: "));
		lbCompError = new JLabel("0");
		lbCompError.setFont(boldFont);
		panelEst3.add(lbCompError);

		JPanel panelEst4 = new JPanel();
		panelEst4.setLayout(new BoxLayout(panelEst4, BoxLayout.LINE_AXIS));
		panelEst4.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelEst4.add(new JLabel("Transações desfeitas (420): "));
		lbTransDesf = new JLabel("0");
		lbTransDesf.setFont(boldFont);
		panelEst4.add(lbTransDesf);

		JPanel panelEst5 = new JPanel();
		panelEst5.setLayout(new BoxLayout(panelEst5, BoxLayout.LINE_AXIS));
		panelEst5.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelEst5.add(new JLabel("Transações em processamento: "));
		lbTransInProcess = new JLabel("0");
		lbTransInProcess.setFont(boldFont);
		panelEst5.add(lbTransInProcess);

		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelEst0);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(panelEst1);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelEst2);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelEst3);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelEst4);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelEst5);
	}

	// Methods to set the JLabel variables with statistic values

	void setNumTotalTrans(String numTotalTrans) {
		lbTotalTrans.setText(numTotalTrans);
	}

	void setNumTransCompleted(String numTransCompleted) {
		lbCompSucess.setText(numTransCompleted);
	}
	
	void setNumTransError(String numTransError) {
		lbCompError.setText(numTransError);
	}

	void setNumTransRevers(String numTransRevers) {
		lbTransDesf.setText(numTransRevers);
	}

	void setNumTransInProcess(String numTransInCompleted) {
		lbTransInProcess.setText(numTransInCompleted);
	}

	void setCurrentDate(String date) {
		lbCurrentDate.setText(date);
	}	
}
