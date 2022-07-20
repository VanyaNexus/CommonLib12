/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import commonlib.com.minnymin.command.CommandArgs;
import org.bukkit.event.HandlerList;

public class CommandPostExecutionEvent
extends CEvent {
    private CommandArgs context;
    private Result result;

    public CommandPostExecutionEvent(CommandArgs context, Result result) {
        this.context = context;
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public CommandArgs getContext() {
        return this.context;
    }

    public static HandlerList getHandlerList() {
        return CommandPostExecutionEvent.getOrCreateHandlerList();
    }

    public static enum Result {
        COMPLETE,
        HALTED,
        ERROR;

    }
}

