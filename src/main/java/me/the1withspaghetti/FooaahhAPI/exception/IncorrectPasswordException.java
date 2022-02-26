package me.the1withspaghetti.FooaahhAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IncorrectPasswordException extends RuntimeException {

	private static final long serialVersionUID = -7264101382920327286L;

}
