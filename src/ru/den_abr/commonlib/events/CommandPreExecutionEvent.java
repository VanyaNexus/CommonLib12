/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import com.google.common.base.Preconditions;
import commonlib.com.minnymin.command.CommandArgs;
import org.bukkit.event.HandlerList;

public class CommandPreExecutionEvent
extends CancellableCEvent {
    private CommandArgs context;

    public CommandPreExecutionEvent(CommandArgs context) {
        this.context = context;
    }

    public CommandArgs getContext() {
        return this.context;
    }

    public void setContext(CommandArgs context) {
        this.context = Preconditions.checkNotNull(context, "Context");
    }

    public static HandlerList getHandlerList() {
        return CommandPreExecutionEvent.getOrCreateHandlerList();
    }
}

