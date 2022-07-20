/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.menu;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.utility.ItemLore;

import java.util.function.Consumer;

public class DefaultIcon
implements Icon {
    public static final Consumer<ClickEvent> NOOP = e -> {};
    protected ItemLore icon = new ItemLore(new ItemStack(Material.STONE));
    private transient Consumer<ClickEvent> clickHandler;

    public DefaultIcon(ItemStack icon) {
        this(new ItemLore(icon));
    }

    public DefaultIcon(ItemLore itemLore) {
        this(itemLore, NOOP);
    }

    public DefaultIcon(ItemStack item, Consumer<ClickEvent> handler) {
        this(new ItemLore(item), handler);
    }

    public DefaultIcon(ItemLore item, Consumer<ClickEvent> handler) {
        this.icon = item;
        this.clickHandler = handler;
    }

    public DefaultIcon() {
        this(new ItemStack(Material.AIR));
    }

    public ItemLore getRawIcon() {
        return this.icon;
    }

    public void setRawIcon(ItemLore item) {
        this.icon = (ItemLore)Preconditions.checkNotNull((Object)item);
    }

    @Override
    public ItemStack getItemStack(Player p) {
        return this.icon.toItem();
    }

    public <T extends DefaultIcon> T clickHandler(Consumer<ClickEvent> handler) {
        this.clickHandler = handler;
        return (T)this;
    }

    public <T extends DefaultIcon> T clickHandler(boolean close, Consumer<ClickEvent> handler) {
        return this.clickHandler(e -> {
            e.close(close);
            handler.accept(e);
        });
    }

    public <T extends DefaultIcon> T autoClose() {
        return this.clickHandler(true, NOOP);
    }

    @Override
    @Deprecated
    public boolean onClick(ClickEvent event) {
        return Icon.super.onClick(event);
    }

    @Override
    public void onPress(ClickEvent event) {
        this.clickHandler.accept(event);
    }
}

