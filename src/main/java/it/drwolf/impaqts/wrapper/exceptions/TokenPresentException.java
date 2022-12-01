package it.drwolf.impaqts.wrapper.exceptions;

public class TokenPresentException extends RuntimeException {

	public TokenPresentException() {
		super();
	}

	public TokenPresentException(String message) {
		super(message);
	}

	public TokenPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenPresentException(Throwable cause) {
		super(cause);
	}
}
