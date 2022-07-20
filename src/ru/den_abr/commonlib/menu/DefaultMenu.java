/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.menu;

import clib.com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import clib.com.comphenix.packetwrapper.WrapperPlayServerWindowItems;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.PlayerMap;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DefaultMenu
extends AbstractMenu {
    protected transient PlayerMap<MenuHolder> inventories = new PlayerMap();
    private transient boolean proccessPlaceholders = true;
    private transient boolean skullPlaceholders = false;

    @Override
    public void refreshContents(Player player) {
        if (!this.inventories.containsKey(player)) {
            return;
        }
        MenuHolder holder = this.getPlayerHolder(player);
        Inventory inv = holder.getInventory();
        this.fill(player, inv);
        player.updateInventory();
    }

    @Override
    public void refreshIcon(Player p, int i) {
        this.refreshIcons(p, i);
    }

    @Override
    public void refreshIcons(Player p, int ... ints) {
        MenuHolder holder = this.getPlayerHolder(p);
        if (holder == null) {
            return;
        }
        Inventory inv = holder.getInventory();
        for (int i : ints) {
            Icon icon = this.getIcon(i);
            if (icon == null) continue;
            ItemStack is = this.processItem(icon, p);
            inv.setItem(i, is);
            this.notifySlot(p, i, is, holder.getOpenId());
        }
    }

    @Override
    public void refreshIconsForAll(int ... i) {
        this.filterAndGetHolders().stream().map(MenuHolder::getPlayer).forEach(p -> this.refreshIcons(p, i));
    }

    @Override
    public void refreshIconForAll(int i) {
        this.filterAndGetHolders().stream().map(MenuHolder::getPlayer).forEach(p -> this.refreshIcon(p, i));
    }

    public void notifySlot(Player p, int slot, ItemStack is, int invId) {
        WrapperPlayServerSetSlot setslot = new WrapperPlayServerSetSlot();
        setslot.setSlot(slot);
        setslot.setSlotData(is);
        setslot.setWindowId(invId);
        setslot.sendPacket(p);
    }

    public void notifyContents(Player player, ItemStack[] contents) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < contents.length; ++i) {
            items.add(contents[i] != null ? contents[i] : new ItemStack(Material.AIR));
        }
        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems();
        packet.setWindowId(UtilityMethods.getInventoryID(player));
        packet.setSlotData(items.toArray(new ItemStack[items.size()]));
    }

    public void fill(Player player, Inventory inv) {
        ItemStack[] contents = new ItemStack[inv.getSize()];
        this.getIcons().forEach((index, icon) -> {
            contents[index.intValue()] = this.processItem(icon, player);
        });
        inv.setContents(contents);
    }

    public boolean isProccessPlaceholders() {
        return this.proccessPlaceholders;
    }

    public void doProccessPlaceholders(boolean proccessPlaceholders) {
        this.proccessPlaceholders = proccessPlaceholders;
    }

    public boolean isSkullPlaceholders() {
        return this.skullPlaceholders;
    }

    public void setSkullPlaceholders(boolean skullPlaceholders) {
        this.skullPlaceholders = skullPlaceholders;
    }

    public Collection<MenuHolder> getHolders() {
        return ImmutableList.copyOf(this.inventories.values());
    }

    public Collection<MenuHolder> filterAndGetHolders() {
        ImmutableList.Builder<MenuHolder> b = new ImmutableList.Builder();
        com.google.common.collect.ImmutableSet.Builder<Player> toRemove = new com.google.common.collect.ImmutableSet.Builder();
        ImmutableList.copyOf(this.inventories.values()).forEach((holder) -> {
            if (holder.getPlayer().isOnline() && holder.getPlayer().getOpenInventory().getTopInventory() != null && Objects.equals(holder.getPlayer().getOpenInventory().getTopInventory().getHolder(), holder)) {
                b.add(holder);
            } else {
                toRemove.add(holder.getPlayer());
            }

        });
        toRemove.build().forEach(this::dispose);
        return b.build();
    }

    @Override
    public void refreshAll() {
        this.filterAndGetHolders().stream().map(MenuHolder::getPlayer).forEach(this::refreshContents);
    }

    @Override
    public void open(Player player) {
        MenuHolder holder = (MenuHolder)((PlayerMap)Preconditions.checkNotNull(this.inventories, "Inventories are null again")).get(player);
        if (holder == null) {
            holder = this.createHolder(player);
            this.prepareHolder(holder);
        }
        player.openInventory(holder.getInventory());
        holder.setOpenId(UtilityMethods.getInventoryID(player));
    }

    @Override
    public void close(Player player) {
        MenuHolder holder = this.getPlayerHolder(player);
        if (holder != null) {
            player.closeInventory();
        }
    }

    @Override
    public void closeAll() {
        this.getViewers().forEach(this::close);
    }

    @Override
    public List<Player> getViewers() {
        return ImmutableList.copyOf(this.inventories.keySet());
    }

    void prepareHolder(MenuHolder holder) {
        Preconditions.checkState((this.getRows() > 0 ? 1 : 0) != 0, "Row count is not set (" + this.getRows() + ")");
        Player player = holder.getPlayer();
        Inventory inv = Bukkit.createInventory(holder, this.getRows() * 9, this.getTitle(player));
        holder.setInventory(inv);
        this.inventories.put(player, holder);
        this.fill(player, inv);
    }

    public MenuHolder getPlayerHolder(Player player) {
        return this.inventories.get(player);
    }

    @Override
    public void dispose(Player player) {
        MenuHolder inv = this.inventories.remove(player);
    }

    public ItemStack processItem(Icon icon, Player player) {
        ItemStack item = icon.getItemStack(player);
        if (this.proccessPlaceholders) {
            return PlaceholderManager.proccessItemStack(player, item, true, this.skullPlaceholders);
        }
        return item;
    }
}

