package org.exception;

import java.util.List;

public class InvalidValidatorException extends RuntimeException {
    private final List<String> messages;

    public InvalidValidatorException(List<String> messages) {
        super("Validation failed with multiple errors");
        this.messages = messages;
    }

    public List<String> getAllMessage() {
      return messages;
    }
}
