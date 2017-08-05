package cemfreitas.autorizadorMC.MVC;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

/* BlinkLabel class.
 * It represents a JLabel instance, but implements an interface to provide blink behavior.
 * It uses the Timer API to do so.   
 */  
public class BlinkLabel extends JLabel implements BlinkLabelnterface {
	private static final long serialVersionUID = 8947525073263432995L;
	private static final Color COLOR_CONNECTED = Color.GREEN; //Color used in the "connected" text
	private static final Color COLOR_DESCONNECTED = Color.RED; //Color used in the "disconnected" text
	private static final Color COLOR_DISABLED = Color.BLUE; //Color used in the "disabled" text
	private static final int BLINKING_RATE = 800;// Blink rate constant

	private boolean blinkingOn = true;
	private TimerListener timerListener = new TimerListener();
	private Timer timer = new Timer(BLINKING_RATE, timerListener);

	private class TimerListener implements ActionListener {
		private BlinkLabel bl;
		private Color bg;
		private Color fg;
		private boolean isForeground = true;

		void setBlinkLabel(BlinkLabel bl) {
			this.bl = bl;
			fg = bl.getForeground();
			bg = bl.getBackground();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (bl.blinkingOn) {
				if (isForeground) {
					bl.setForeground(fg);
				} else {
					bl.setForeground(bg);
				}
				isForeground = !isForeground;
			} else {
				if (isForeground) {
					bl.setForeground(fg);
					isForeground = false;
				}
			}
		}
	}

	@Override
	public void setDesconectedText(String text) {		
		setText(text);
		setForeground(COLOR_DESCONNECTED);
		timerListener.setBlinkLabel(this);
		timer.setInitialDelay(0);
		timer.start();		
	}

	@Override
	public void setConnectedText(String text) {
		setText(text);
		setForeground(COLOR_CONNECTED);
		timer.stop();
	}

	@Override
	public void setDisbledText(String text) {
		setText(text);
		setForeground(COLOR_DISABLED);
		timer.stop();		
	}

}