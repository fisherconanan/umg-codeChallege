package com.umg.isrcapp;

public class EmployeeNotFoundException extends RuntimeException {
    EmployeeNotFoundException(Long id) {
        super("Could not find ID " + id);
    }

    EmployeeNotFoundException(String ISRC) {
        super("Could not find ISRC = " + ISRC);
    }
}
