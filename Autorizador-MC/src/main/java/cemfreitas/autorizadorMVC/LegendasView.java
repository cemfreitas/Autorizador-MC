package cemfreitas.autorizadorMVC;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/* This class compounds one of the four parts on the main screen.   
 * It shows the legend panel on the main screen 
 */
public class LegendasView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2533047814026306715L;

	public LegendasView() {
		//Setting border and layout
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Legendas ",
				TitledBorder.CENTER, TitledBorder.TOP));
		setLayout(new GridLayout(4, 1));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//Create views swing UI components	
		JPanel panelLeg1 = new JPanel();
		panelLeg1.setLayout(new BoxLayout(panelLeg1, BoxLayout.LINE_AXIS));
		panelLeg1.add(Box.createRigidArea(new Dimension(30, 0)));
		panelLeg1.add(new TransStatusRect(Color.CYAN));
		panelLeg1.add(Box.createRigidArea(new Dimension(30, 0)));
		panelLeg1.add(new TransStatusRect(Color.YELLOW));
		panelLeg1.add(Box.createRigidArea(new Dimension(30, 0)));
		panelLeg1.add(new TransStatusRect(Color.GREEN));
		panelLeg1.add(Box.createRigidArea(new Dimension(30, 0)));
		panelLeg1.add(new TransStatusRect(Color.RED));
		
		JPanel panelLeg2 = new JPanel();
		panelLeg2.setLayout(new BoxLayout(panelLeg2, BoxLayout.LINE_AXIS));
		panelLeg2.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelLeg2.add(Box.createRigidArea(new Dimension(25, 0)));
		panelLeg2.add(new JLabel("Nova"));
		panelLeg2.add(Box.createRigidArea(new Dimension(35, 0)));
		panelLeg2.add(new JLabel("Reversal (420)"));
		panelLeg2.add(Box.createRigidArea(new Dimension(15, 0)));
		panelLeg2.add(new JLabel("Completada"));
		panelLeg2.add(Box.createRigidArea(new Dimension(15, 0)));
		panelLeg2.add(new JLabel("Não Completada"));

		JPanel panelLeg3 = new JPanel();
		panelLeg3.setLayout(new FlowLayout());
		panelLeg3.add(new TransStatusRect(Color.GREEN));
		panelLeg3.add(new JLabel("Completada"));

		JPanel panelLeg4 = new JPanel();
		panelLeg4.setLayout(new FlowLayout());
		panelLeg4.add(new TransStatusRect(Color.RED));
		panelLeg4.add(new JLabel("Não Completada"));

		add(Box.createRigidArea(new Dimension(0, 5)));
		add(panelLeg1);
		add(panelLeg2);
	}

	/* Inner class used to draw a colored rectangle 
	 * 
	 */
	class TransStatusRect extends JPanel {

		
		/**
		 * 
		 */
		private static final long serialVersionUID = -8420815559494765411L;
		private Color color;

		TransStatusRect(Color color) {
			this.color = color;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawRect(0, 0, 20, 15);
			g.setColor(color);
			g.fillRect(0, 0, 20, 15);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(21, 21); 
		}
	}

}
