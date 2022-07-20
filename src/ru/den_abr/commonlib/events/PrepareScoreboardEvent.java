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
import ru.den_abr.commonlib.boards.packet.VirtualScoreboard;

public class PrepareScoreboardEvent
extends CEvent {
    private Player player;
    private VirtualScoreboard board;

    public PrepareScoreboardEvent(Player p, VirtualScoreboard board) {
        this.player = p;
        this.board = board;
    }

    public VirtualScoreboard getBoard() {
        return this.board;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return PrepareScoreboardEvent.getOrCreateHandlerList();
    }
}

