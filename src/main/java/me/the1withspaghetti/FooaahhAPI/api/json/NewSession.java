package me.the1withspaghetti.FooaahhAPI.api.json;

public class NewSession extends Response {
	public String session_token;
	public String base64key;

	public NewSession(String session_token, String base64key) {
		super(true);
		this.session_token = session_token;
		this.base64key = base64key;
	}

}
