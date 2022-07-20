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
import ru.den_abr.commonlib.menu.Menu;

public class MenuOpenEvent
extends CancellableCEvent {
    private Menu menu;
    private Player player;

    public MenuOpenEvent(Player player, Menu menu) {
        this.menu = menu;
        this.player = player;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public <T extends Menu> T getMenu(Class<T> clazz) {
        return (T)((Menu)clazz.cast(this.menu));
    }

    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return MenuOpenEvent.getOrCreateHandlerList();
    }
}

