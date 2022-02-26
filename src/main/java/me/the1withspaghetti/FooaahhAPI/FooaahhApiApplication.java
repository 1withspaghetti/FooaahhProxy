package me.the1withspaghetti.FooaahhAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.the1withspaghetti.FooaahhAPI.api.SessionApi;
import me.the1withspaghetti.FooaahhAPI.console.ConsoleCommandManager;

@SpringBootApplication
public class FooaahhApiApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(FooaahhApiApplication.class, args);
		new ConsoleCommandManager(new BufferedReader(new InputStreamReader(System.in)));
		SessionApi.initCleanSessions();
	}

}
