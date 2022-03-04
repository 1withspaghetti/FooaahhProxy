package me.the1withspaghetti.FooaahhAPI.api;

import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.util.concurrent.RateLimiter;

import me.the1withspaghetti.FooaahhAPI.Encryption;
import me.the1withspaghetti.FooaahhAPI.Leaderboard;
import me.the1withspaghetti.FooaahhAPI.api.json.Error;
import me.the1withspaghetti.FooaahhAPI.api.json.NewSession;
import me.the1withspaghetti.FooaahhAPI.api.json.Response;
import me.the1withspaghetti.FooaahhAPI.api.json.SessionData;
import me.the1withspaghetti.FooaahhAPI.api.json.request.EndGameRequest;
import me.the1withspaghetti.FooaahhAPI.api.json.request.HeartbeatRequest;
import me.the1withspaghetti.FooaahhAPI.api.json.request.NewSessionRequest;
import me.the1withspaghetti.FooaahhAPI.api.json.request.StartGameRequest;
import me.the1withspaghetti.FooaahhAPI.exception.ApiException;
import me.the1withspaghetti.FooaahhAPI.exception.ApiLimitException;
import me.the1withspaghetti.FooaahhAPI.exception.CheatDetection;
import me.the1withspaghetti.FooaahhAPI.exception.InvalidSessionException;

@RestController
@CrossOrigin(origins = "*", methods=RequestMethod.POST)
@RequestMapping("/fooaaahh/session")
public class SessionApi {
	
	public static final String GAME_URL = "https://fooaaahh.jcwyt.com/";
	
	public static HashMap<String,SessionData> sessions = new HashMap<>();
	static RateLimiter rate = RateLimiter.create(5);
	static Timer t = new Timer();
	
	
	public static void initCleanSessions() { // For cleaning old sessions from RAM
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				//System.out.println("Purging inactive sessions...");
				sessions.forEach((session,data) -> {
					if (data.lastInteraction < System.currentTimeMillis() - 600000 && data.startTime != 0) // If a session is 10 minutes old, delete it
						sessions.remove(session);
				});}
		};
		t.scheduleAtFixedRate(task, 300000, 300000); // Checks every 5 minutes for 10 minute old sessions
	}
	
	@PostMapping("/new")
	public static Response newSession(@RequestBody NewSessionRequest req, HttpServletRequest httpReq) {
		IpRateLimiter.checkIP(httpReq, 5);
		if (!rate.tryAcquire(5, Duration.ofSeconds(5))) throw new ApiLimitException();
		try {
			// Input data checks
			if (req.username == null) throw new ApiException("A username must be specified");
			//if (!req.username.matches("^[a-zA-Z0-9_\\-. ]{1,16}$")) throw new ApiException("Invalid Username (must only contain the alphebet, spaces, hyphens, and underlines)");
			
			// Submitting
			String token = UUID.randomUUID().toString();
			String base64key = Encryption.newBase64Key();
			sessions.put(token, new SessionData(0, System.currentTimeMillis(), req.username, base64key));
			return new NewSession(token, base64key);
		} catch (ApiException e) {
			return new Error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Error("Internal Server Error");
		}
	}
	
	@PostMapping("/heartbeat")
	public static Response heartbeat(@RequestBody HeartbeatRequest req, HttpServletRequest httpReq) {
		IpRateLimiter.checkIP(httpReq, 1);
		if (!rate.tryAcquire(1, Duration.ofSeconds(3))) throw new ApiLimitException();
		try {
			// Input data checks
			if (req.session_token == null) throw new ApiException("Must include session token");
			if (!req.session_token.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) throw new ApiException("Invalid Session Token String");
			if (!sessions.containsKey(req.session_token)) throw new InvalidSessionException();
			
			// Submitting
			sessions.replace(req.session_token, sessions.get(req.session_token)
					.setLastInteraction(System.currentTimeMillis()));
			return new Response(true);
			
		} catch (InvalidSessionException e) {
			throw e;
		} catch (ApiException e) {
			return new Error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Error("Internal Server Error");
		}
		
	}
	
	@PostMapping("/startgame")
	public static Response startGame(@RequestBody StartGameRequest req, HttpServletRequest httpReq) {
		IpRateLimiter.checkIP(httpReq, 1);
		if (!rate.tryAcquire(1, Duration.ofSeconds(3))) throw new ApiLimitException();
		try {
			// Input data checks
			if (req.session_token == null) throw new ApiException("Must include session token");
			if (!req.session_token.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) throw new ApiException("Invalid Session Token String");
			if (!sessions.containsKey(req.session_token)) throw new InvalidSessionException();
			
			// Time related checks
			if (sessions.get(req.session_token).startTime != 0) throw new CheatDetection("Invalid start");
			
			// Submitting
			sessions.replace(req.session_token, sessions.get(req.session_token)
					.setStartTime(System.currentTimeMillis())
					.setLastInteraction(System.currentTimeMillis()));
			return new Response(true);
		} catch (InvalidSessionException e) {
			throw e;
		} catch (ApiException e) {
			return new Error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Error("Internal Server Error");
		}
	}
	
	@PostMapping("/endgame")
	public static Response endGame(@RequestBody EndGameRequest req, HttpServletRequest httpReq) {
		IpRateLimiter.checkIP(httpReq, 1);
		if (!rate.tryAcquire(1, Duration.ofSeconds(3))) throw new ApiLimitException();
		try {
			// Input data checks
			if (req.session_token == null || req.score == null) throw new ApiException("Missing one or more arguments");
			if (!req.session_token.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) throw new ApiException("Invalid Session Token String");
			if (!sessions.containsKey(req.session_token)) throw new InvalidSessionException();
			
			// Time related checks
			SessionData data = sessions.get(req.session_token).setLastInteraction(System.currentTimeMillis());
			final long start = data.startTime;
			sessions.replace(req.session_token, data.setStartTime(0));
			if (start == 0 || start > System.currentTimeMillis() - 2000) throw new CheatDetection("Invalid submission");
			
			// Score checks
			int score = Integer.parseInt(Encryption.decrypt(req.score, data.base64key));
			if (score < 0 || score > 5000) throw new ApiException("Invalid Score");
			
			// Time related checks
			if (score > 100 && start > System.currentTimeMillis() - 60000) throw new ApiException("Invalid Score");
			if (score > 1000 && start > System.currentTimeMillis() - 300000) throw new ApiException("Invalid Score");
			
			// Submitting
			if (score != 0) Leaderboard.submitScore(data.user, score);
			return new Response(true);
			
		} catch (CheatDetection e) {
			return new Error("Cheat Detection: "+e.getMessage());
		} catch (NumberFormatException e) {
			return new Error("Invalid Score");
		} catch (GeneralSecurityException e) {
			return new Error("Invalid Score");
		} catch (IllegalArgumentException e) {
			return new Error("Invalid Score");
		} catch (InvalidSessionException e) {
			throw e;
		} catch (ApiException e) {
			return new Error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Error("Internal Server Error");
		}
	}
	
	@ExceptionHandler(InvalidSessionException.class)
	public static RedirectView onSessionRedirect(HttpServletRequest req) {
		RedirectView rv = new RedirectView(GAME_URL+"?InvalidSession=true");
		rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
	    return rv;
	}
}
