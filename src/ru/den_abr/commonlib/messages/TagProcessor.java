/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package ru.den_abr.commonlib.messages;

import com.google.gson.JsonElement;

import java.util.function.Function;

public interface TagProcessor<T>
extends Function<T, JsonElement> {
    public JsonElement toElement(T var1);

    @Override
    default public JsonElement apply(T t) {
        return this.toElement(t);
    }
}

