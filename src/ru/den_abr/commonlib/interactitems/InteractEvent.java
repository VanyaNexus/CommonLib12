/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.interactitems;

import org.bukkit.entity.Player;

public class InteractEvent {
    private Player player;
    private boolean right;
    private InteractiveItem item;
    private int slot;
    private boolean sneaking;
    private boolean onBlock;

    public InteractEvent(Player player, boolean right, InteractiveItem item, int slot, boolean sneaking, boolean onBlock) {
        this.player = player;
        this.right = right;
        this.item = item;
        this.slot = slot;
        this.sneaking = sneaking;
        this.onBlock = onBlock;
    }

    public InteractiveItem getItem() {
        return this.item;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isRightClick() {
        return this.right;
    }

    public boolean isOnBlock() {
        return this.onBlock;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }
}

