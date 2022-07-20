/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib.messages;

public class SingleKey
implements MessageKey {
    private String key;
    private Object def;

    public SingleKey(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getDefault() {
        return this.def;
    }
}

