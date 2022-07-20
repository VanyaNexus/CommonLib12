/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  co.aikar.timings.Timing
 *  co.aikar.timings.Timings
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package ru.den_abr.commonlib;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.events.MenuCloseEvent;
import ru.den_abr.commonlib.events.MenuOpenEvent;
import ru.den_abr.commonlib.interactitems.InteractEvent;
import ru.den_abr.commonlib.interactitems.InteractiveItem;
import ru.den_abr.commonlib.menu.Menu;
import ru.den_abr.commonlib.menu.MenuManager;
import ru.den_abr.commonlib.menu.PaginatedMenu;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.PlayerMap;
import ru.den_abr.commonlib.utility.PreloadMe;
import ru.den_abr.commonlib.utility.RateLimiter;
import ru.den_abr.commonlib.utility.UniversalEconomyService;
import ru.den_abr.commonlib.utility.UtilityMethods;
//import sun.reflect.CallerSensitive;
//import sun.reflect.Reflection;

public class InternalListener
        implements Listener {
    private final CommonLib plugin;
    private final PlayerMap<RateLimiter> clickLimiters = new PlayerMap();

    protected InternalListener(CommonLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        if (!this.plugin.equals(e.getPlugin())) {
            this.plugin.unregisterHooks(e.getPlugin());
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        if (e.getPlugin() instanceof PreloadMe) {
            UtilityMethods.forciblyLoadAllClasses((JavaPlugin)e.getPlugin());
        }
        if (e.getPlugin().getName().equals("Vault") && !CommonLib.isEconomyReady()) {
            UniversalEconomyService.start();
        }
        if (e.getPlugin().getName().equals("PlaceholderAPI")) {
            this.plugin.registerPlaceholdersAPI();
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (CommonLib.CONF.isReplacePlaceholdersInChat()) {
            UtilityMethods.setChatFormatDirectly(e, PlaceholderManager.proccessString(e.getPlayer(), e.getFormat(), true));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        CommonLib.reqMan.cancelRequests(e.getPlayer());
        CommonLib.getMainScoreboard().getHandle().unset(e.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onMenuInventoryClose(InventoryCloseEvent e) {
        Menu menu = MenuManager.inventoryToMenu(e.getInventory());
        if (menu == null) {
            return;
        }
        Player player = (Player)e.getPlayer();
        try (Timing timing = Timings.ofStart(CommonLib.INSTANCE, "Menu close event")){
            MenuManager.setLastMenu(player, menu);
            new MenuCloseEvent(player, menu).call();
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onMenuOpen(InventoryOpenEvent e) {
        if (MenuManager.isMenuInventory(e.getInventory())) {
            Menu menu;
            Player player = (Player)e.getPlayer();
            MenuOpenEvent event = new MenuOpenEvent(player, menu = MenuManager.inventoryToMenu(e.getInventory())).call();
            if (event.isCancelled()) {
                e.setCancelled(true);
                menu.dispose(player);
                return;
            }
            menu.onOpen(player);
            MenuManager.setOpenedMenu(player, menu);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onMenuClose(MenuCloseEvent e) {
        try {
            e.getMenu().onClose(e.getPlayer());
        }
        catch (VirtualMachineError se) {
            throw se;
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
        MenuManager.dispose(e.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        CommonLib.reqMan.cancelRequests(e.getPlayer());
        CommonLib.getMainScoreboard().getHandle().unset(e.getPlayer());
        Player player = e.getPlayer();
        this.clickLimiters.remove(player);
        Menu menu = MenuManager.getOpenedMenu(player);
        if (menu != null) {
            new MenuCloseEvent(player, menu).call();
        }
    }

    @EventHandler
    public void saveOnQuit(PlayerQuitEvent e) {
        if (CommonLib.CONF.isSavePlayerOnQuit()) {
            Bukkit.getScheduler().runTaskLater(CommonLib.INSTANCE, ((Player)e.getPlayer())::saveData, 15L);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void discardCommandsFromOfflinePlayers(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().isOnline()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (MenuManager.isMenuInventory(inv)) {
            if (!(e.getWhoClicked() instanceof Player)) {
                this.plugin.getLogger().warning(e.getWhoClicked() + " is not a player entity. Cancelling click.");
                e.setCancelled(true);
                return;
            }
            int slot = e.getSlot();
            Player p = (Player)e.getWhoClicked();
            Menu menu = MenuManager.inventoryToMenu(inv);
            if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof Player) {
                if (e.isShiftClick() || !menu.isOutsideClicksAllowed()) {
                    e.setCancelled(true);
                }
                return;
            }
            e.setCancelled(true);
            ClickEvent clickEvent = new ClickEvent(p, null, menu, slot, e.isShiftClick(), e.isLeftClick(), e.isRightClick());
            clickEvent.setHandle(e);
            RateLimiter clickLimiter = this.clickLimiters.computeIfAbsent(p, player -> new RateLimiter(CommonLib.CONF.getMenuMaxClickRate(), CommonLib.CONF.getMenuClickRatePeriod(), TimeUnit.MILLISECONDS));
            if (clickLimiter.isLimited()) {
                CommonLib.INSTANCE.getLogger().warning("Rate limited clicks for " + p.getName() + " - " + clickLimiter.getAmount() + " for " + clickLimiter.getPeriod() + "ms. Menu " + menu.getName());
                p.closeInventory();
                return;
            }
            clickLimiter.increment();
            try (Timing menuTimings = Timings.ofStart(this.plugin, "Menu: " + menu.getName(), null);
                 Timing slotTiming = Timings.ofStart(this.plugin, "Click: Slot - " + slot + Optional.of(menu).filter(PaginatedMenu.class::isInstance).map(PaginatedMenu.class::cast).map(m -> " Page: " + m.getPlayerPage(p)).orElse("") + " Player: " + e.getWhoClicked().getName() + " | Name: " + menu.getName(), menuTimings)){

                menu.onPress(clickEvent);
            }
            if (clickEvent.close()) {
                p.closeInventory();
                p.updateInventory();
            }
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem()) {
            Action action = e.getAction();
            for (InteractiveItem ii : InteractiveItem.getItems()) {
                if (!ii.getItem().isSimilar(e.getItem()) || action == Action.PHYSICAL) continue;
                Player player = e.getPlayer();
                e.setCancelled(true);
                InteractEvent event = new InteractEvent(player, action.name().startsWith("RIGHT"), ii, player.getInventory().getHeldItemSlot(), player.isSneaking(), action.name().endsWith("BLOCK"));
                ii.getListener().onInteract(event);
            }
        }
    }
}

