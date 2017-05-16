package cemfreitas.autorizador.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;

import cemfreitas.autorizador.manager.Mediator;

/* AutorizadorLog class.
 * Used from TransactionManager, builds all log information to be log. * 
 */
public class AutorizadorLog {
	private Logger logger = Logging.getLogger();
	private StringBuffer logAutorization;
	public Mediator mediator;

	public AutorizadorLog(Mediator mediator) {
		this.mediator = mediator;
		logAutorization = new StringBuffer();
	}

	//Mounts information to be logged.
	public void build() {
		logAutorization.append("*********** Inicio da transacao ***********\n");
		logAutorization.append("Mastercard  :");
		if (mediator.getTransactionFromMC() != null) {
			logAutorization.append(AppFunctions.dumpString(mediator.getTransactionFromMC()) + "\n");
		} else {
			logAutorization.append(mediator.getAutException().getMessage() + "\n");
			return;
		}
		logAutorization.append("Desempacotando transacao da Master Card :");
		if (mediator.getIsoTransactionFromMC() != null) {
			logAutorization.append(getTransactionFields(mediator.getIsoTransactionFromMC()) + "\n");
		} else {
			logAutorization.append(mediator.getAutException().getMessage() + "\n");
			return;
		}
		if (mediator.isChipCardTransaction()) {
			logAutorization.append("Empacotando transacao para Ecoscard :");
			if (mediator.getIsoTransactionToEcoscard() != null) {
				logAutorization.append(getTransactionFields(mediator.getIsoTransactionToEcoscard()));
				logAutorization.append("Ecoscard enviando :");
				logAutorization.append(AppFunctions.dumpString(mediator.getTransactionToEcoscard()) + "\n");
				logAutorization.append("Ecoscard recebendo :");
				if (mediator.getTransactionFromEcoscard() != null) {
					logAutorization.append(AppFunctions.dumpString(mediator.getTransactionFromEcoscard()) + "\n");
				} else {
					if (mediator.getEcoException() != null) {
						logAutorization.append(mediator.getEcoException().getMessage() + "\n\n");
					}
				}
				if (mediator.getIsoTransactionFromEcoscard() != null) {
					logAutorization.append(getTransactionFields(mediator.getIsoTransactionFromEcoscard()));
					logAutorization.append("Tempo de resposta da Ecoscard :");
					logAutorization.append(mediator.getEcoExecutionTime() + " milisegundos.\n\n");
				} else {
					if (mediator.getEcoException() != null) {
						logAutorization.append(mediator.getEcoException().getMessage() + "\n\n");
					}
				}
			} else {
				if (mediator.getEcoException() != null) {
					logAutorization.append(mediator.getEcoException().getMessage() + "\n\n");
				}
			}
		}
		if (mediator.isHSMTransaction()) {
			logAutorization.append("Executando procedure HSM :");
			if (mediator.getDatabaseHsmReturn() != null) {
				logAutorization.append("Procedure HSM executada com sucesso. ");
				logAutorization.append("Tempo de resposta :");
				logAutorization.append(mediator.getHsmDbExecutionTime() + " milisegundos.\n");
				logAutorization.append("Enviando transacao para o HSM :");
				if (mediator.getTransactionFromHSM() != null) {
					logAutorization.append("OK \n");
					logAutorization.append("Tempo de resposta :");
					logAutorization.append(mediator.getHsmExecutionTime() + " milisegundos.\n\n");
				} else {
					if (mediator.getHsmException() != null) {
						logAutorization.append(mediator.getHsmException().getMessage() + "\n\n");
					}
				}
			} else {
				if (mediator.getHsmException() != null) {
					logAutorization.append(mediator.getHsmException().getMessage() + "\n\n");
				}
			}
		}
		logAutorization.append("Executando procedure Autorizacao :");
		if (mediator.getDatabaseAutReturn() != null) {
			logAutorization.append("Procedure autorizacao executada com sucesso. ");
			logAutorization.append("Tempo de resposta :");
			logAutorization.append(mediator.getAutDbExecutionTime() + " milisegundos.\n\n");
		} else {
			if (mediator.getAutException() != null) {
				logAutorization.append(mediator.getAutException().getMessage() + "\n\n");
			}
			return;
		}
		logAutorization.append("Empacotando resposta para MasterCard :");
		if (mediator.getIsoTransactionToMC() != null) {
			logAutorization.append(getTransactionFields(mediator.getIsoTransactionToMC()) + "\n");
			logAutorization.append("Enviando resposta :");
			if (mediator.getAutException() == null) {
				if (mediator.getTransactionToMC() != null) {
					logAutorization.append(AppFunctions.dumpString(mediator.getTransactionToMC()) + "\n\n");
				}
			} else {
				if (mediator.getAutException() != null) {
					logAutorization.append(mediator.getAutException().getMessage() + "\n\n");
				}
			}
		} else {
			if (mediator.getAutException() != null) {
				logAutorization.append(mediator.getAutException().getMessage() + "\n\n");
			}
		}
	}

	//log it.
	public void log() {
		if (!mediator.isTimeOut()) {
			logAutorization.append("*********** Fim da transacao ***********  ");
			logAutorization.append("Tempo total gasto :");
			logAutorization.append(mediator.getAutExecutionTime());
			logAutorization.append(" milisegundos\n");
		}
		logger.info(logAutorization.toString());
	}

	//Extracts fields from ISO transactions to log.
	private String getTransactionFields(ISOMsg isoTransaction) {
		ISOMsg msgToLog = (ISOMsg) isoTransaction.clone();
		ByteArrayOutputStream bAos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bAos);
		int[] dEsToHide = new int[] { 2, 20, 23, 35, 45, 52, 55, 125 };// Hide
																		// some
																		// sensitive
																		// data
																		// from
																		// user's
																		// transactions
																		// before
																		// logging.

		for (int i = 0; i < dEsToHide.length; i++) {
			if (msgToLog.hasField(dEsToHide[i])) {
				msgToLog.set(dEsToHide[i], "**********");
			}
		}
		msgToLog.dump(ps, "");
		ps.flush();
		return bAos.toString();

	}

}
