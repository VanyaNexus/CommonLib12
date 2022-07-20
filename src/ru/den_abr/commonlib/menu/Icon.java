/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.menu.event.ClickEvent;

public interface Icon {
    public ItemStack getItemStack(Player var1);

    @Deprecated
    default public boolean onClick(ClickEvent event) {
        this.onPress(event);
        return !event.close();
    }

    public void onPress(ClickEvent var1);
}

