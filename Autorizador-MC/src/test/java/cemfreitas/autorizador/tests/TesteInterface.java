package cemfreitas.autorizador.tests;

import java.math.BigDecimal;

import javax.swing.SwingUtilities;

import cemfreitas.autorizadorMC.AutorizadorConstants.ClientConnectionStatus;
import cemfreitas.autorizadorMC.AutorizadorConstants.TransactionStatus;
import cemfreitas.autorizadorMC.MVC.AutorizadorController;
import cemfreitas.autorizadorMC.MVC.AutorizadorModel;
import cemfreitas.autorizadorMC.MVC.Model;
import cemfreitas.autorizadorMC.MVC.TransactionData;

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
		autorizadorController.setConnectStatusHSM(ClientConnectionStatus.CLIENT_CONNECTED);
		autorizadorController.setConnectStatusEcoscard(ClientConnectionStatus.CLIENT_CONNECTED);
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
		
		autorizadorController.updateTransactionStatus(510, TransactionStatus.TRANSAC_REVERTED);
		autorizadorController.updateTransactionStatus(520, TransactionStatus.TRANSAC_COMPLETED);
		autorizadorController.updateTransactionStatus(535, TransactionStatus.TRANSAC_NOT_COMPLETED);
		
		autorizadorController.updateTransactionStatus(550, TransactionStatus.TRANSAC_REVERTED);
		autorizadorController.updateTransactionStatus(570, TransactionStatus.TRANSAC_COMPLETED);
		autorizadorController.updateTransactionStatus(595, TransactionStatus.TRANSAC_NOT_COMPLETED);
		
		autorizadorController.updateStatistics(null);
	}
}
