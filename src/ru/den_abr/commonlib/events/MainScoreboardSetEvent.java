/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MainScoreboardSetEvent
extends CEvent {
    private Player player;

    public MainScoreboardSetEvent(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return MainScoreboardSetEvent.getOrCreateHandlerList();
    }
}

