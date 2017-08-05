package cemfreitas.autorizadorMC.manager;

public abstract class TransactionFactoryBase {
	abstract Transaction createMasterCardTransaction(Mediator mediator) throws AutorizadorException;
	abstract Transaction createEcoscardTransaction(Mediator mediator) throws AutorizadorException;
	abstract Transaction createHsmTransaction(Mediator mediator) throws AutorizadorException;
	abstract Transaction createHsmDbTransaction(Mediator mediator) throws AutorizadorException;
	abstract Transaction createAutDbTransaction(Mediator mediator) throws AutorizadorException;
}
