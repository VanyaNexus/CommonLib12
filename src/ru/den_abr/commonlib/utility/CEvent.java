package ru.den_abr.commonlib.utility;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public class CEvent extends Event
{
    private static final HandlerList handlers;
    
    public HandlerList getHandlers() {
        return CEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return CEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
