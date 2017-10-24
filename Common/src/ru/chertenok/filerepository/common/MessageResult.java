package ru.chertenok.filerepository.common;

public class MessageResult extends Message{
    public final boolean success;
    public final String message;

    public MessageResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
