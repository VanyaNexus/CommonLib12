/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import ru.den_abr.commonlib.nametags.Nametag;

import java.util.Objects;

public class NametagApplyEvent
extends CancellableCEvent {
    private Nametag tag;
    private Player player;
    private boolean dirty = false;

    public NametagApplyEvent(Player player, Nametag tag) {
        this.tag = (Nametag)Preconditions.checkNotNull((Object)tag);
        this.player = (Player)Preconditions.checkNotNull((Object)player);
    }

    public Player getPlayer() {
        return this.player;
    }

    public Nametag getTag() {
        return this.tag;
    }

    public void setTag(Nametag tag) {
        Preconditions.checkNotNull((Object)tag);
        this.dirty = !Objects.equals(tag, this.tag);
        this.tag = tag;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public static HandlerList getHandlerList() {
        return NametagApplyEvent.getOrCreateHandlerList();
    }
}

