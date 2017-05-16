package cemfreitas.autorizador.manager;

/* TransactionFactory class.
 * Implements Factory pattern.
 * Encapsulates the Transaction classes instantiation.
 */
public class TransactionFactory extends TransactionFactoryBase {

	@Override
	Transaction createMasterCardTransaction(Mediator mediator) throws AutorizadorException {		
		return new TransactionMasterCard(mediator);
	}

	@Override
	Transaction createEcoscardTransaction(Mediator mediator) throws AutorizadorException {
		return new TransactionEcoscard(mediator);
	}

	@Override
	Transaction createHsmTransaction(Mediator mediator) throws AutorizadorException {		
		return new TransactionHSM(mediator);
	}

	@Override
	Transaction createHsmDbTransaction(Mediator mediator) throws AutorizadorException {
		return new TransactionHsmDB(mediator);
	}

	@Override
	Transaction createAutDbTransaction(Mediator mediator) throws AutorizadorException {
		return new TransactionAutorizationDB(mediator);
	}

}
