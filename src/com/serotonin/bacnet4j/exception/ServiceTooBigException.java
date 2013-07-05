package com.serotonin.bacnet4j.exception;

public class ServiceTooBigException extends BACnetException {
    private static final long serialVersionUID = 1L;

    public ServiceTooBigException(String message) {
        super(message);
    }
}
