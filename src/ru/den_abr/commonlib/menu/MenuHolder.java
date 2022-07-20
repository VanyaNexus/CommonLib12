/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package ru.den_abr.commonlib.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder
implements InventoryHolder {
    private Player opened;
    private Menu menu;
    private Inventory inventory;
    private int openId = -1;

    public MenuHolder(Menu menu, Player holder) {
        this.menu = menu;
        this.opened = holder;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return this.opened;
    }

    public void setOpenId(int openId) {
        this.openId = openId;
    }

    public int getOpenId() {
        return this.openId;
    }
}

