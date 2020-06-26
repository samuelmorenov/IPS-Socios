package logic.exception;

public class DatosDeCursillosErroneosException extends Exception {

	private static final long serialVersionUID = 3426880529798680425L;
	

	// Parameterless Constructor
	public DatosDeCursillosErroneosException() {
	}

	// Constructor that accepts a message
	public DatosDeCursillosErroneosException(String message) {
		super(message);
	}

	public DatosDeCursillosErroneosException(Throwable cause) {
        super (cause);
    }
}
