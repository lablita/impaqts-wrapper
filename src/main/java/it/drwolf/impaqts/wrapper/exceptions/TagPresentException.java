package it.drwolf.impaqts.wrapper.exceptions;

public class TagPresentException extends RuntimeException {

	public TagPresentException() {
		super();
	}

	public TagPresentException(String message) {
		super(message);
	}

	public TagPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public TagPresentException(Throwable cause) {
		super(cause);
	}
}
