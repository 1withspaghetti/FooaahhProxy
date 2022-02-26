package me.the1withspaghetti.FooaahhAPI.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.the1withspaghetti.FooaahhAPI.Leaderboard;

public class ConsoleCommandManager {
	ExecutorService executor;
	BufferedReader in;
	
	public ConsoleCommandManager(BufferedReader in) {
		this.executor = Executors.newSingleThreadExecutor();
		this.in = in;
		executor.execute(start);
	}
	
	Runnable start = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					String cmd = in.readLine();
					List<String> args = parseString(cmd);
					switch (args.get(0)) {
						case "help":
							System.out.println("----- All commands: -----");
							/*System.out.println("get \"username\"  -  Gets all storeed info about a user");
							System.out.println("add \"username\" 10 2  -  Adds a user with a score of 10 and 2 attempts");
							System.out.println("remove \"username\"  -  Removes a user from the leaderboard");*/
							System.out.println("update \"username\" 15  -  Updates a users score to 15");
							/*System.out.println("rename \"username\" \"othername\"  -  Renames a user");
							System.out.println("purge [startswith|contains|endswith|scoreunder|scoreequals|scoreover] \"input\""
									+ "  -  Purges user accounts in database specified by the args (requires running confirm-last-purge afterwards)");
							System.out.println("session [session-token]  -  Gets info on a running player session");
							System.out.println("reload  -  Reloads the admin passwords and leaderboard cache");*/
							System.out.println("--------------------------");
							break;
						/*case "get":
							if (args.size() != 2) {
								System.out.println("Usage: get \"username\"");
								break;
							}
							System.out.println(Leaderboard.cmdGetUser(args.get(1)));
							break;
						case "add":
							if (args.size() != 4) {
								System.out.println("Usage: add \"username\" [int score] [int attempts]");
								break;
							}
							System.out.println(Leaderboard.cmdAddUser(args.get(1), Integer.parseInt(args.get(2)), Integer.parseInt(args.get(3))));
							break;
						case "remove":
							if (args.size() != 2) {
								System.out.println("Usage: remove \"username\"");
								break;
							}
							System.out.println(Leaderboard.cmdRemoveUser(args.get(1)));
							break;*/
						case "update":
							if (args.size() != 3) {
								System.out.println("Usage: update \"username\" [int score]");
								break;
							}
							Leaderboard.submitScore(args.get(1), Integer.parseInt(args.get(2)));
							break;
						/*case "rename":
							if (args.size() != 3) {
								System.out.println("Usage: rename \"username\" \"new_username\"");
								break;
							}
							System.out.println(Leaderboard.cmdRenameUser(args.get(1), args.get(2)));
							break;
						case "purge":
							if (args.size() != 3) {
								System.out.println("Usage: purge [startswith|contains|endswith|under|over] \"input\"");
								break;
							}
							String SQL = "";
							switch (args.get(1)) {
							case "startswith": SQL = "user LIKE \""+args.get(2)+"%\"";break;
							case "contains": SQL = "user LIKE \"%"+args.get(2)+"%\"";break;
							case "endswith": SQL = "user LIKE \""+args.get(2)+"\"";break;
							case "scoreunder": SQL = "score < "+Integer.parseInt(args.get(2));break;
							case "scoreequals": SQL = "score = "+Integer.parseInt(args.get(2));break;
							case "scoreover": SQL = "score > "+Integer.parseInt(args.get(2));break;
							}
							
							if (SQL == "") {
								System.out.println("Usage: purge [startswith|contains|endswith|under|over] \"input\"");
								break;
							}
							ResultSet rs = Leaderboard.executeQuery(BASE_SELECT_SQL+SQL);
							if (rs.isBeforeFirst()) {
								System.out.println("Use the command confirm-last-purge to remove the following users: Key:[username,score,attempts]");
								lastSQL = SQL;
								while (rs.next()) {
									System.out.print("["+rs.getString("user")+","+rs.getInt("score")+","+rs.getInt("attempts")+"] ");
								}
								System.out.println();
							}
							else System.out.println("No users match query!");
							break;
						case "confirm-last-purge":
							if (lastSQL == "") {
								System.out.println("You must use the purge command first!");
								break;
							}
							System.out.println("Deleting users by query: "+lastSQL);
							Leaderboard.execute(BASE_PURGE_SQL+lastSQL);
							System.out.println("Done!");
							lastSQL = "";
							break;
						case "session":
							if (args.size() != 2) {
								System.out.println("Usage: session [session-token]");
								break;
							}
							if (args.get(1) != "list") {
								SessionData data = SessionApi.sessions.get(args.get(1));
								if (data != null) {
									System.out.println(data.toString());
								} else {
									System.out.println("Could not find the session: "+args.get(1));
								}
							} else {
								System.out.println("All currently active sessions:");
								SessionApi.sessions.forEach((str,data) -> {
									System.out.println(str+": "+data.user+" times out in ");
								});
							}
							
							break;
						case "reload":
							Leaderboard.updateCache();
							AdminApi.reloadPasswords();
							System.out.println("Reloaded Items");
							break;*/
						default:
							System.out.println("Unknown command! Use \"help\" to see the list of commands");
							break;
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	public static List<String> parseString(String s)
            throws IOException {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
 
        boolean inEscape = false;
        boolean inQuotes = false;
        for (char c : s.toCharArray()) {
            if (inEscape) {
                inEscape = false;
            } else if (c == '\\') {
                inEscape = true;
                continue;
            } else if (c == '"' && !inEscape) {
            	inQuotes = !inQuotes;
            	continue;
            } else if (c == ' ' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
                continue;
            }
            sb.append(c);
        }
        if (inEscape || inQuotes)
            throw new IOException("Invalid terminal escape");
 
        tokens.add(sb.toString());
 
        return tokens;
    } 
	
}
