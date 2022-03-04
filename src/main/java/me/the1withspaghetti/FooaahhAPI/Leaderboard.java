package me.the1withspaghetti.FooaahhAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Leaderboard {
	
	public static final String URL_PREFIX = "https://s7w04rp9.api.lootlocker.io";
	public static final String API_VERSION = "2021-03-01";
	public static final String GAME_VERSION = "10.0.0.0";
	public static final String LEADERBOARD_ID = "867";
	
	public static String SERVER_API_KEY;
	public static String API_TOKEN = "";
	
	public static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
	public static final Gson gson = new Gson();
	
	public static void init() throws IOException, InterruptedException {
		SERVER_API_KEY = new String(Leaderboard.class.getResourceAsStream("/server_api_key.secret").readAllBytes());
		registerSession();
		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					Leaderboard.heartbeatSession();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.scheduleAtFixedRate(task, 5000, 1800000); // 30 minutes
	}
	
	public static void registerSession() throws MalformedURLException, IOException, InterruptedException {
		JsonObject json = new JsonObject();
		json.addProperty("game_version", GAME_VERSION);
		json.addProperty("is_development", false);
		HttpRequest req = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(json.toString()))
                .uri(URI.create(URL_PREFIX+"/server/session"))
                .header("Content-Type", "application/json")
                .setHeader("LL-Version", API_VERSION)
                .setHeader("x-server-key", SERVER_API_KEY)
                .build();
		System.out.println("POST req to "+req.uri());
		System.out.println("Body: "+json.toString());
		System.out.println("Headers: "+req.headers().toString());
		System.out.println();
		
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		JsonObject resJson = gson.fromJson(res.body(), JsonObject.class);
		if (resJson.has("token")) {
			API_TOKEN = resJson.get("token").getAsString();
			System.out.println("Registered Session: "+resJson.get("token").getAsString());
		} else {
			System.err.println("Error registering session: "+res.statusCode()+" "+resJson.get("error").getAsString());
			System.err.println("POST req to "+req.uri());
			System.err.println("Body: "+json.toString());
			System.err.println("Headers: "+req.headers().toString());
			System.err.println("--- Response ---");
			System.err.println(res.body());
		}
	}
	
	public static void heartbeatSession() throws MalformedURLException, IOException, InterruptedException {
		HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL_PREFIX+"/server/ping"))
                .setHeader("LL-Version", API_VERSION)
                .setHeader("x-auth-token", API_TOKEN)
                .build();
		
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		JsonObject resJson = gson.fromJson(res.body(), JsonObject.class);
		if (resJson.has("error")) {
			System.err.println("Error pinging session: "+res.statusCode()+" "+resJson.get("error").getAsString());
		}
	}
	
	public static void submitScore(String user, int score) {
		try {
			JsonObject json = new JsonObject();
			json.addProperty("member_id", user);
			json.addProperty("score", score);
			
			HttpRequest req = HttpRequest.newBuilder()
	                .POST(BodyPublishers.ofString(json.toString()))
	                .uri(URI.create(URL_PREFIX+"/server/leaderboards/"+LEADERBOARD_ID+"/submit"))
	                .header("Content-Type", "application/json")
	                .setHeader("LL-Version", API_VERSION)
	                .setHeader("x-auth-token", API_TOKEN)
	                .build();
			
			HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
			JsonObject resJson = gson.fromJson(res.body(), JsonObject.class);
			if (resJson.has("error") || res.statusCode() != 200) {
				System.err.println("##### Error submitting score: "+res.statusCode()+" "+resJson.get("error").getAsString()+" #####");
				System.err.println("POST req to "+req.uri());
				System.err.println("Body: "+json.toString());
				System.err.println("Headers: "+req.headers().toString());
				System.err.println("##### Response "+res.statusCode()+" #####");
				System.err.println(res.body());
			} else {
				System.out.println(res.body());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
