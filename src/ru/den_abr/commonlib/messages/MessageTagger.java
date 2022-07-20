/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib.messages;

public interface MessageTagger {
    default public Message tagMessage(Message message) {
        this.applyMessageTags(message);
        return message;
    }

    default public Message tagMessage(MessageKey key) {
        return this.tagMessage(key.message());
    }

    public void applyMessageTags(Message var1);
}

