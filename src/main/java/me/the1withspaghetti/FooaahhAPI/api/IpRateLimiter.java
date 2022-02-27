package me.the1withspaghetti.FooaahhAPI.api;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import me.the1withspaghetti.FooaahhAPI.exception.ApiLimitException;


public class IpRateLimiter {
	
	private static HashMap<byte[], Integer> limits = new HashMap<>();
	
	public static final int MAX_REQUESTS_PER_PERIOD = 50;
	
	public static void init() {
		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				IpRateLimiter.clearLimits();
			}
		};
		t.scheduleAtFixedRate(task, 60000, 60000);
	}
	
	public static void clearLimits() {
		limits.clear();
	}
	
	public static void checkIP(byte[] ip, int increment) {
		limits.compute(ip, (k, v) -> {
			if (v != null && v > MAX_REQUESTS_PER_PERIOD) throw new ApiLimitException();
			return (v == null) ? increment : v+increment;
		});
	}
	
	public static void checkIP(String ip, int increment) {
		checkIP(parseIP(ip), increment);
	}
	
	public static void checkIP(HttpServletRequest req, int increment) {
		String forward = req.getHeader("X-FORWARDED-FOR");
		if (forward != null) {
			if (forward.contains(","))
				checkIP(forward.substring(0, forward.indexOf(',')), increment);
			else
				checkIP(forward, increment);
		} else {
			checkIP(req.getRemoteAddr(), increment);
		}
	}
	
	
	public static byte[] parseIP(String ipAddr) {
		byte[] ret = new byte[4];
		String[] ipArr = ipAddr.split("\\.");
		ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
		ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
		ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
		ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
		return ret;
	}
	
}