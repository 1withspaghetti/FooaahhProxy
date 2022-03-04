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
		checkIP(getTrueIp(req), increment);
	}
	
	public static String getTrueIp(HttpServletRequest req) {
		String forward = req.getHeader("X-FORWARDED-FOR");
		if (forward != null) {
			if (forward.contains(","))
				return forward.substring(0, forward.indexOf(','));
			else
				return forward;
		} else {
			return req.getRemoteAddr();
		}
	}
	
	
	public static byte[] parseIP(String ip) {
		if (ip.contains(".")) {
			// ipv4
			byte[] ret = new byte[4];
			String[] ipArr = ip.split("\\.");
			ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
			return ret;
		} else if (ip.contains(":")) {
			// ipv6
			byte[] ret = new byte[8];
			String[] ipArr = ip.split(":");
			ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
			ret[4] = (byte) (Integer.parseInt(ipArr[4]) & 0xFF);
			ret[5] = (byte) (Integer.parseInt(ipArr[5]) & 0xFF);
			ret[6] = (byte) (Integer.parseInt(ipArr[6]) & 0xFF);
			ret[7] = (byte) (Integer.parseInt(ipArr[7]) & 0xFF);
			return ret;
		} else {
			System.out.println("Unknown ip string: "+ip);
			return null;
		}
		
	}
	
}
