package com.app.yolla.shared.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class MyException extends RuntimeException {

	private String message;

	public MyException(String message) {
		super(message);
	}

}
