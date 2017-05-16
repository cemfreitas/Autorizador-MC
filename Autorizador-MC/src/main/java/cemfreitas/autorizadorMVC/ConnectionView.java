package cemfreitas.autorizadorMVC;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/* This class compounds one of the four parts on the main screen.   
 * It shows the Connection status panel on the main screen. 
 */
public class ConnectionView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3438253104171640737L;
	
	//Use a BlinkLabel interface to choose between a method which blinks or not
	private BlinkLabelnterface lbEcoscardStatusConnect = new BlinkLabel();
	private BlinkLabelnterface lbHSMStatusConnect = new BlinkLabel();

	public ConnectionView() {
		//Setting border and layout
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Conexões ",
				TitledBorder.CENTER, TitledBorder.TOP));
		setLayout(new GridLayout(1, 2));

		// Creating Connection Status components
		JLabel labelEcoscard = new JLabel("Ecoscard :");
		JLabel labelHSM = new JLabel("HSM :");		

		JPanel statusHSM = new JPanel();
		statusHSM.setLayout(new FlowLayout());
		statusHSM.add(labelHSM);
		statusHSM.add((Component) lbHSMStatusConnect);

		JPanel statusEcoscard = new JPanel();
		statusEcoscard.setLayout(new FlowLayout());
		statusEcoscard.add(labelEcoscard);
		statusEcoscard.add((Component) lbEcoscardStatusConnect);

		add(statusHSM);
		add(statusEcoscard);
		
		//Set as disconected at the first time.
		setHSMConnectionOff();
		setEcoscardConnectionOff();
	}
	
	/* Change the connection status of the HSM and Ecoscard 
	 * 
	 */
	
	void setHSMConnectionOn() {
		lbHSMStatusConnect.setConnectedText("Conectado");		
	}
	
	void setHSMConnectionOff() {
		lbHSMStatusConnect.setDesconectedText("Desconectado");
	}
	
	void setEcoscardConnectionOn() {
		lbEcoscardStatusConnect.setConnectedText("Conectado");
	}
	
	void setEcoscardConnectionOff() {
		lbEcoscardStatusConnect.setDesconectedText("Desconectado");
	}

}
