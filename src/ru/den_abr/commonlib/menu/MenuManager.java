/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 */
package ru.den_abr.commonlib.menu;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.utility.PlayerMap;

import java.util.concurrent.ConcurrentHashMap;

public class MenuManager {
    private static final PlayerMap<Menu> currentMenu = new PlayerMap(new ConcurrentHashMap());
    private static final PlayerMap<Menu> lastMenus = new PlayerMap(new ConcurrentHashMap());

    public static void dispose(Player player) {
        Menu menu = currentMenu.remove(player);
        if (menu == null) {
            CommonLib.debug("No menu for player " + player.getName());
            return;
        }
        CommonLib.debug("Disposing last menu " + menu.getName() + " for " + player.getName());
        menu.dispose(player);
    }

    public static void setOpenedMenu(Player player, Menu current) {
        Preconditions.checkNotNull((Object)current, "Menu cannot be null");
        MenuManager.dispose(player);
        CommonLib.debug("Setting menu " + current.getName() + " as opened for " + player.getName());
        currentMenu.put(player, current);
    }

    public static Menu getOpenedMenu(Player player) {
        return currentMenu.get(player);
    }

    public static boolean isMenuInventory(Inventory inv) {
        Preconditions.checkNotNull((Object)inv, "Inventory cannot be null");
        return inv.getHolder() instanceof MenuHolder;
    }

    public static Menu inventoryToMenu(Inventory inv) {
        if (!MenuManager.isMenuInventory(inv)) {
            return null;
        }
        return ((MenuHolder)inv.getHolder()).getMenu();
    }

    public static Menu getLastMenu(Player player) {
        return lastMenus.removeOffline().get(player);
    }

    public static void setLastMenu(Player player, Menu menu) {
        lastMenus.put(player, menu);
    }

    public static Menu inventoryToMenu(InventoryView view) {
        if (view == null || view.getTopInventory() == null) {
            return null;
        }
        return MenuManager.inventoryToMenu(view.getTopInventory());
    }

    public static void closeMenu(Player player) {
        if (MenuManager.inventoryToMenu(player.getOpenInventory()) != null) {
            player.closeInventory();
        }
    }
}

