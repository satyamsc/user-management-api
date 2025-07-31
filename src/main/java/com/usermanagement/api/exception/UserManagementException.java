package com.usermanagement.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserManagementException extends RuntimeException {

    private final HttpStatus status;

    public UserManagementException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static UserManagementException notFound(String message) {
        return new UserManagementException(message, HttpStatus.NOT_FOUND);
    }

    public static UserManagementException badRequest(String message) {
        return new UserManagementException(message, HttpStatus.BAD_REQUEST);
    }

    public static UserManagementException conflict(String message) {
        return new UserManagementException(message, HttpStatus.CONFLICT);
    }
}