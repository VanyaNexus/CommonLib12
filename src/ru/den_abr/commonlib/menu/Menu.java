/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.menu;

import org.bukkit.entity.Player;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.menu.event.ClickHandler;

import java.util.List;

public interface Menu {
    public void refreshIcon(Player var1, int var2);

    public void refreshIcons(Player var1, int ... var2);

    public void refreshIconForAll(int var1);

    public void refreshIconsForAll(int ... var1);

    public void refreshContents(Player var1);

    public void refreshAll();

    public void open(Player var1);

    default public void open(ClickEvent event) {
        this.open(event.getPlayer());
    }

    public void close(Player var1);

    default public void close(ClickEvent event) {
        this.close(event.getPlayer());
    }

    public void closeAll();

    public List<Player> getViewers();

    @Deprecated
    default public boolean onClick(Player player, int slot, boolean shift, boolean left, boolean right) {
        throw new UnsupportedOperationException("Deprecated");
    }

    public void onPress(ClickEvent var1);

    public String getName();

    public String getTitle(Player var1);

    public void registerClickHandler(ClickHandler var1);

    public int getRows();

    public void dispose(Player var1);

    public void onOpen(Player var1);

    public void onClose(Player var1);

    public boolean isOutsideClicksAllowed();

    public void setAllowOutsideClicks(boolean var1);
}

