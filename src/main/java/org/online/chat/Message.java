package org.online.chat;

import java.util.Objects;

public record Message(String name, String message) {
    public Message(String name, String message) {
        this.name = Objects.requireNonNull(name);
        this.message = Objects.requireNonNull(message);
    }
}
