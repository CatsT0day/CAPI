package me.catst0day.Eclipse.Exceptions;

public class CommandRegistryException extends RuntimeException {
    public CommandRegistryException(String message, Throwable t) {
        super(message, t);
    }
}
