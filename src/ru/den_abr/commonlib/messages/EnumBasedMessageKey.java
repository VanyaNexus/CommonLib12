/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib.messages;

public interface EnumBasedMessageKey
extends MessageKey {
    @Override
    default public String getKey() {
        Enum e = (Enum)(this);
        return this.getKeyPrefix() + e.name().toLowerCase().replace('_', '.');
    }

    default public String getKeyPrefix() {
        return "";
    }
}

