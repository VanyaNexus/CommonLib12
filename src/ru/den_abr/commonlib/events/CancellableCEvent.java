/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 */
package ru.den_abr.commonlib.events;

import org.bukkit.event.Cancellable;

public abstract class CancellableCEvent
extends CEvent
implements Cancellable {
    private boolean cancelled = false;

    public CancellableCEvent() {
    }

    public CancellableCEvent(boolean async) {
        super(async);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}

