package me.the1withspaghetti.FooaahhAPI.exception;

public class CheatDetection extends Exception {
	private static final long serialVersionUID = -8924582570336960887L;

	public CheatDetection() {
		super();
	}
	
	public CheatDetection(String msg) {
		super(msg);
	}
}
