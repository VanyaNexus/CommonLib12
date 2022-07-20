/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import org.bukkit.event.HandlerList;
import ru.den_abr.commonlib.utility.UniversalEconomyService;

public class EconomyReadyEvent
extends CEvent {
    private UniversalEconomyService service;

    public EconomyReadyEvent(UniversalEconomyService service) {
        this.service = service;
    }

    public UniversalEconomyService getService() {
        return this.service;
    }

    public static HandlerList getHandlerList() {
        return EconomyReadyEvent.getOrCreateHandlerList();
    }
}

