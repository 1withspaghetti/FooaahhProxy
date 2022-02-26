package me.the1withspaghetti.FooaahhAPI.api.json;

public class Error extends Response {
	public String reason;
	
	public Error(String reason) {
		super(false);
		this.reason = reason;
	}
}
