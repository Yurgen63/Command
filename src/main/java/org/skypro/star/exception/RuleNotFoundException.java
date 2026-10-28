package org.skypro.star.exception;

public class RuleNotFoundException extends RuntimeException {
    public RuleNotFoundException(Long id) {
        super("Rule with id " + id + " not found");
    }
}