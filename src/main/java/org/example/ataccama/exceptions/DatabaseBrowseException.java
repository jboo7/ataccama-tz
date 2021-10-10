package org.example.ataccama.exceptions;

public class DatabaseBrowseException extends Exception {
    public DatabaseBrowseException(String message) {
        super(message);
    }

    public DatabaseBrowseException(String message, Throwable cause) {
        super(message, cause);
    }
}
