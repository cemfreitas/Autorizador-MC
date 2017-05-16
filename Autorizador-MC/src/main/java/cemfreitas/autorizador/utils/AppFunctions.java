package cemfreitas.autorizador.utils;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.jpos.iso.ISOUtil;

/* AppFunctions class.
 * Provides helper functions used in whole application
 */

public class AppFunctions extends ISOUtil {

	synchronized static public byte[] concatenate(byte b1, byte[] b2) {

		if (b2 == null) {
			return null;
		}
		byte[] result = new byte[1 + b2.length];
		int i = 0;

		result[i++] = b1;

		for (int j = 0; j < b2.length; i++, j++) {
			result[i] = b2[j];
		}
		return result;
	}

	synchronized static public byte[] addHeader(byte[] msg) {

		byte newMsg[];
		int newMsgLength;
		int hi = 0;
		int lo = 0;
		int index;

		newMsgLength = msg.length;

		hi = (newMsgLength >>> 8) & 0xFF;
		lo = newMsgLength & 0xFF;

		newMsg = new byte[newMsgLength + 2];

		index = 0;

		newMsg[index++] = (byte) hi;

		newMsg[index++] = (byte) lo;

		System.arraycopy(msg, 0, newMsg, index, msg.length);

		return newMsg;
	}

	synchronized static public boolean checkHeader(byte[] msg) {
		int msgSize;
		byte[] header = new byte[2];
		byte[] rawMsg = new byte[msg.length - 2];

		System.arraycopy(msg, 0, header, 0, 2);
		System.arraycopy(msg, 2, rawMsg, 0, rawMsg.length);
		msgSize = byte2int(header);

		if (msgSize == rawMsg.length) {
			return true;
		} else {
			return false;
		}
	}

	synchronized static public byte[] copyBytes(byte[] src, int ini, int fim) {
		if (src == null) {
			return null;
		}
		byte[] result = new byte[(fim - ini) + 1];
		int i = 0;

		for (int j = ini; j <= fim; i++, j++) {
			result[i] = src[j];
		}
		return result;
	}
	
	synchronized static public double parseAmount(String valueStr) {
		String intPart,fracPart;
		double value;
		
		if (valueStr == null || valueStr.length() < 3) {
			return 0;
		}
		
		intPart = valueStr.substring(0,valueStr.length()-2);		
		fracPart = valueStr.substring(valueStr.length()-2,valueStr.length());
		
		try {
		value = Double.parseDouble(intPart + "." + fracPart);
		} catch (NumberFormatException e) {
			value = 0;
		}
		
		return value;
	}
	
	public static void showErroMessage(String message, Exception e) {
		String errorMsg = e.getMessage().replace(".", ".\n");
		
		JOptionPane.showMessageDialog(new Frame(),message + "\nMsg :" + errorMsg,"Portal Card",JOptionPane.ERROR_MESSAGE);
	}
}
