/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib.nametags;

import java.util.HashMap;
import java.util.Map;

public class Nametag
implements Cloneable {
    private String holder;
    private String prefix = "";
    private String suffix = "";
    private Type type;
    private int priority;
    private Map<String, Object> metadata = new HashMap<String, Object>();

    public Nametag(String holder, String prefix, String suffix, Type type) {
        this(holder, prefix, suffix, -1, type);
    }

    public Nametag(String holder, String prefix, String suffix, int priority, Type type) {
        this.holder = holder;
        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
        this.priority = Integer.max(-1, priority);
        this.type = type;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getPrefixSafe() {
        return NametagsManager.cutName(this.prefix);
    }

    public String getHolder() {
        return this.holder;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getSuffixSafe() {
        return NametagsManager.cutName(this.suffix);
    }

    public Type getType() {
        return this.type;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix == null ? "" : suffix;
    }

    public void setPriority(int priority) {
        this.priority = Integer.max(-1, priority);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public <T> T getMetadata(String key) {
        return (T)this.metadata.get(key);
    }

    public Object setMetadata(String key, Object val) {
        return val == null ? this.metadata.remove(key) : this.metadata.put(key, val);
    }

    public Nametag clone() {
        return new Nametag(this.holder, this.prefix, this.suffix, this.type);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Nametag [holder=").append(this.holder).append(", prefix=").append(this.prefix).append(", suffix=").append(this.suffix).append(", type=").append((Object)this.type).append(", priority=").append(this.priority).append(", metadata=").append(this.metadata).append("]");
        return builder.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.holder == null ? 0 : this.holder.hashCode());
        result = 31 * result + (this.prefix == null ? 0 : this.prefix.hashCode());
        result = 31 * result + this.priority;
        result = 31 * result + (this.suffix == null ? 0 : this.suffix.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Nametag)) {
            return false;
        }
        Nametag other = (Nametag)obj;
        if (this.holder == null ? other.holder != null : !this.holder.equals(other.holder)) {
            return false;
        }
        if (this.prefix == null ? other.prefix != null : !this.prefix.equals(other.prefix)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if (this.suffix == null ? other.suffix != null : !this.suffix.equals(other.suffix)) {
            return false;
        }
        return this.type == other.type;
    }

    public static enum Type {
        GROUP,
        PLAYER;

    }
}

