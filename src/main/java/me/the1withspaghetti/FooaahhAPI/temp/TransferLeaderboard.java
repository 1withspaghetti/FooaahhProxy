package me.the1withspaghetti.FooaahhAPI.temp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.the1withspaghetti.FooaahhAPI.Leaderboard;

public class TransferLeaderboard {
	public static void transfer() throws IOException, InterruptedException {
		/*HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Leaderboard.URL_PREFIX+"/server/leaderboards/867/list?count=1000"))
                .setHeader("LL-Version", Leaderboard.API_VERSION)
                .setHeader("x-auth-token", Leaderboard.API_TOKEN)
                .build();
		System.out.println("GET req to "+req.uri());
		System.out.println("Headers: "+req.headers().toString());
		System.out.println();
		HttpResponse<String> res = Leaderboard.client.send(req, BodyHandlers.ofString());*/
		JsonObject scores = Leaderboard.gson.fromJson(new String(TransferLeaderboard.class.getResourceAsStream("/old_leaderboard.json").readAllBytes()), JsonObject.class);
		
		JsonArray items = scores.get("items").getAsJsonArray();
		
		
		for (JsonElement i : items) {
			JsonObject item = i.getAsJsonObject();
			submitScore(item.get("member_id").getAsString(), item.get("score").getAsInt());
			System.out.println(item.get("member_id").getAsString()+" - "+item.get("score").getAsInt());
		}
		
	}
	
	public static void submitScore(String user, int score) {
		try {
			JsonObject json = new JsonObject();
			json.addProperty("member_id", user);
			json.addProperty("score", score);
			
			HttpRequest req = HttpRequest.newBuilder()
	                .POST(BodyPublishers.ofString(json.toString()))
	                .uri(URI.create(Leaderboard.URL_PREFIX+"/server/leaderboards/"+Leaderboard.LEADERBOARD_ID+"/submit"))
	                .header("Content-Type", "application/json")
	                .setHeader("LL-Version", Leaderboard.API_VERSION)
	                .setHeader("x-auth-token", Leaderboard.API_TOKEN)
	                .build();
			
			HttpResponse<String> res = Leaderboard.client.send(req, BodyHandlers.ofString());
			JsonObject resJson = Leaderboard.gson.fromJson(res.body(), JsonObject.class);
			if (resJson.has("error")) {
				System.err.println("##### Error submitting score: "+res.statusCode()+" "+resJson.get("error").getAsString()+" #####");
				System.err.println("POST req to "+req.uri());
				System.err.println("Body: "+json.toString());
				System.err.println("Headers: "+req.headers().toString());
				System.err.println("##### Response "+res.statusCode()+" #####");
				System.err.println(res.body());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
