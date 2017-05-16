package cemfreitas.autorizador.tests;

import java.math.BigDecimal;

import javax.swing.SwingUtilities;

import cemfreitas.autorizadorMVC.AutorizadorController;
import cemfreitas.autorizadorMVC.AutorizadorModel;
import cemfreitas.autorizadorMVC.Model;
import cemfreitas.autorizadorMVC.TransactionData;

public class TesteInterface {
	static Model autorizadorModel;
	static AutorizadorController autorizadorController;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void createAndShowGUI() throws Exception {
		autorizadorModel = new AutorizadorModel();
		autorizadorController = new AutorizadorController(autorizadorModel);
		autorizadorController.setConnectStatusHSM(false);
		autorizadorController.setConnectStatusEcoscard(true);
		autorizadorController.setApplicationVersion("vs 2.0");		
		
		TransactionData transactionData;		
		long threadId = 500;
		for (int i = 0; i < 50; i++) {
			transactionData = new TransactionData();
			transactionData.setData("15/04/2017 15:50");
			transactionData.setCodigo("0100");
			transactionData.setProcesso("000003");
			transactionData.setValor(new BigDecimal(80.60));
			transactionData.setNSU(String.valueOf(i));
			transactionData.setEstabelecimento("0123456789012345");
			transactionData.setNumCartao("03215545455458");
			autorizadorController.inertTransaction(threadId + i, transactionData);
		}
		
		for (int i = 50; i < 100; i++) {
			transactionData = new TransactionData();
			transactionData.setData("15/04/2017 15:50");
			transactionData.setCodigo("0100");
			transactionData.setProcesso("000003");
			transactionData.setValor(new BigDecimal(80.60));
			transactionData.setNSU(String.valueOf(i));
			transactionData.setEstabelecimento("0123456789012345");
			transactionData.setNumCartao("03215545455458");
			autorizadorController.inertTransaction(threadId + i, transactionData);			
		}
		
		autorizadorController.updateTransactionStatus(510, 2);
		autorizadorController.updateTransactionStatus(520, 3);
		autorizadorController.updateTransactionStatus(535, 4);
		
		autorizadorController.updateTransactionStatus(550, 2);
		autorizadorController.updateTransactionStatus(570, 3);
		autorizadorController.updateTransactionStatus(595, 4);
		
		autorizadorController.updateStatistics(null);
	}
}
