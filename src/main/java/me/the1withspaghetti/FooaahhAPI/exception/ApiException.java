package me.the1withspaghetti.FooaahhAPI.exception;

public class ApiException extends Exception {

	private static final long serialVersionUID = -3515653173944178352L;

	public ApiException() {
		super();
	}
	
	public ApiException(String msg) {
		super(msg);
	}
}
