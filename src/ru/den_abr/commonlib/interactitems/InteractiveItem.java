/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.interactitems;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class InteractiveItem {
    private static Set<InteractiveItem> items = new HashSet<InteractiveItem>();
    private InteractListener listener;
    private ItemStack item;

    public ItemStack getItem() {
        return this.item.clone();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setListener(InteractListener listener) {
        this.listener = listener;
    }

    public InteractListener getListener() {
        return this.listener;
    }

    public void register() {
        items.add(this);
    }

    public static Set<InteractiveItem> getItems() {
        return items;
    }
}

