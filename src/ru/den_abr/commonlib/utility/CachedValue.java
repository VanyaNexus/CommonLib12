package ru.den_abr.commonlib.utility;

import java.util.function.Supplier;

public class CachedValue<T>
{
    private final Supplier<T> provider;
    private final long refreshTime;
    private long lastAccess;
    private T lastGot;
    
    public CachedValue(final Supplier<T> provider, final long refreshTime) {
        this.lastAccess = 0L;
        this.provider = provider;
        this.refreshTime = refreshTime;
    }
    
    public T get() {
        if (System.currentTimeMillis() - this.lastAccess > this.refreshTime) {
            this.lastAccess = System.currentTimeMillis();
            return this.lastGot = this.provider.get();
        }
        return this.lastGot;
    }
    
    public long getLastAccess() {
        return this.lastAccess;
    }
}
