package com.fullstack.gstbillingwithinvoicemngmt.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
