/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 */
package ru.den_abr.commonlib.menu.event;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.den_abr.commonlib.menu.Icon;
import ru.den_abr.commonlib.menu.Menu;

public class ClickEvent {
    private Icon icon;
    private final int slot;
    private final boolean shift;
    private final boolean left;
    private final boolean right;
    private final Player player;
    private final Menu menu;
    private Boolean close;
    private InventoryClickEvent handle;

    public ClickEvent(Player p, Icon i, Menu m, int slot, boolean shift, boolean left, boolean right) {
        this.icon = i;
        this.player = p;
        this.menu = m;
        this.slot = slot;
        this.shift = shift;
        this.left = left;
        this.right = right;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Player getPlayer() {
        return this.player;
    }

    public InventoryClickEvent getHandle() {
        return this.handle;
    }

    public void setHandle(InventoryClickEvent handle) {
        this.handle = handle;
    }

    public <T extends Menu> T getMenu(Class<T> clazz) {
        return (T) clazz.cast(this.menu);
    }

    public <T extends Icon> T getIcon(Class<T> clazz) {
        return (T) clazz.cast(this.icon);
    }

    public boolean isIcon(Class<? extends Icon> clazz) {
        return this.icon.getClass().equals(clazz);
    }

    public boolean isMenu(Class<? extends Menu> clazz) {
        return this.menu.getClass().equals(clazz);
    }

    public boolean isShiftClick() {
        return this.shift;
    }

    public boolean isRightClick() {
        return this.right;
    }

    public boolean isLeftClick() {
        return this.left;
    }

    public int getSlot() {
        return this.slot;
    }

    public void close(boolean close) {
        this.close = close;
    }

    public boolean close() {
        return this.closeStateChanged() ? this.close : false;
    }

    public boolean closeStateChanged() {
        return this.close != null;
    }
}

