package com.revature.registrar.exceptions;

public class CapacityReachedException extends RuntimeException {
    public CapacityReachedException(String message) {
        super(message);
    }
}
