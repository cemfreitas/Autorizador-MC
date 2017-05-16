package cemfreitas.autorizador.manager;

import java.util.Observer;
/* Manager interface.
 * Implemented by TransactionMediator class and provides service methods to Transaction Manager.
 */
public interface Manager {
	void perform();
	void addObservable(Observer o);	
}
