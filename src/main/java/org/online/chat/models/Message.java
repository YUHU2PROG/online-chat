package org.online.chat.models;

import java.util.Objects;

public class Message {
    private final String name;
    private final String message;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        return Objects.equals(name, message1.name) && Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
