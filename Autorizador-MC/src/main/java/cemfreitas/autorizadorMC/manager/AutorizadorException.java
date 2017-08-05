package cemfreitas.autorizadorMC.manager;

/* AutorizadorException class.
 * Class used to encapsulate all error messages of the application.
 * When an error is thrown, it is caught and then translated for an
 * adequate message to log. 
 */
public class AutorizadorException extends Exception {
	
	private static final long serialVersionUID = 2247723352483953193L;

	public AutorizadorException() {
	}	

	public AutorizadorException(String message) {
		super(message);
	}

	public AutorizadorException(Throwable cause) {
		super(cause);
	}

	public AutorizadorException(String message, Throwable cause) {
		super(message, cause);
	}

}
