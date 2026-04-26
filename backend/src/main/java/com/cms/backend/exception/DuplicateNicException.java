package com.cms.backend.exception;

public class DuplicateNicException extends RuntimeException {
    public DuplicateNicException(String nic) {
        super("Customer with NIC '" + nic + "' already exists");
    }
}