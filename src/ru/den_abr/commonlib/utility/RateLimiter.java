package ru.den_abr.commonlib.utility;

import java.util.concurrent.TimeUnit;

public class RateLimiter
{
    private int max;
    private long period;
    private int amount;
    private long lastCheck;
    
    public RateLimiter(final int max, final long period) {
        this(max, period, TimeUnit.SECONDS);
    }
    
    public RateLimiter(final int max, final long period, final TimeUnit unit) {
        this.amount = 0;
        this.lastCheck = System.currentTimeMillis();
        this.period = unit.toMillis(period);
        this.max = max;
    }
    
    public void reset() {
        this.amount = 0;
        this.lastCheck = System.currentTimeMillis();
    }
    
    public boolean isLimited() {
        this.recheck();
        return this.amount >= this.max;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public long getPeriod() {
        return this.period;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public void increment() {
        ++this.amount;
    }
    
    private void recheck() {
        if (this.lastCheck + this.period < System.currentTimeMillis()) {
            this.reset();
        }
    }
}
