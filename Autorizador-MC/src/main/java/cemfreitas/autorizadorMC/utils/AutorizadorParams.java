package cemfreitas.autorizadorMC.utils;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;
/* AutorizadorParams class.
 * Provides service to get application parameters from configuration file.
 */
public class AutorizadorParams extends Properties {	
	private static final long serialVersionUID = -1925013640162330888L;
	private static AutorizadorParams params;

	public static void loadProperties() {
		if (params == null) {
			params = new AutorizadorParams();
		}

		InputStream inputFile;
		try {
			inputFile = new FileInputStream("config/config.properties");
			params.load(inputFile);
			inputFile.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(new Frame(),
					"Arquivo de configuracao 'config.properties' nao encontrado na pasta 'config': " + e.getMessage(),
					"Portal Card", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new Frame(),
					"Erro ao carregar arquivo de configuracao 'config.properties': " + e.getMessage(), "Portal Card",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	public static String getValue(String key) {
		return params.getProperty(key);		
	}

	public static int getValueAsInt(String key) {
		int value;
		if (key == null || key.trim().equals("")) {
			return 0;
		}
		try {
			value = Integer.parseInt(params.getProperty(key));
		} catch (java.lang.NumberFormatException e) {
			value = 0;
		}
		return value;
	}	
}
