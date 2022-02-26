package me.the1withspaghetti.FooaahhAPI.api.json;

public class SessionData {
	public long startTime;
	public long lastInteraction;
	public String user;
	public String base64key;
	public SessionData(long startTime, long lastInteraction, String user, String base64key) {
		
		this.startTime = startTime;
		this.lastInteraction = lastInteraction;
		this.user = user;
		this.base64key = base64key;
	}
	public SessionData setStartTime(long startTimes) {
		this.startTime = startTimes;
		return this;
	}
	public SessionData setLastInteraction(long lastInteraction) {
		this.lastInteraction = lastInteraction;
		return this;
	}
	public String toString() {
		return "startTime: "+startTime+", user: "+user+", base64key: "+base64key;
	}
}
