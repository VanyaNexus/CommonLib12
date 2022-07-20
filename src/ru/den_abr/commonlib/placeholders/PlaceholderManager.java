package ru.den_abr.commonlib.placeholders;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.placeholders.custom.LoadablePlaceholder;
import ru.den_abr.commonlib.placeholders.custom.PlaceholdersClassLoader;
import ru.den_abr.commonlib.utility.AutoCatch;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PlaceholderManager
{
    private static Multimap<Plugin, PlaceholderInfo> placeholders;
    
    public static void registerPlaceholder(final Plugin p, final String ph, final Placeholder replacer) {
        registerPlaceholder(p, ph, replacer, false);
    }
    
    public static void registerPlaceholder(final Plugin p, final String ph, final Placeholder replacer, final boolean forceSync) {
        registerPlaceholder(p, ph, replacer, forceSync, true);
    }
    
    public static void registerPlaceholder(final Plugin p, final String ph, final Placeholder replacer, final boolean forceSync, final boolean playerRequired) {
        Preconditions.checkNotNull((Object)p);
        Preconditions.checkNotNull((Object)ph);
        Preconditions.checkNotNull((Object)replacer);
        final PlaceholderInfo info = new PlaceholderInfo(ph, p, replacer, !forceSync, playerRequired);
        PlaceholderManager.placeholders.put(p, info);
        info.injectForeignPlaceholders();
    }
    
    public static void registerCustomPlaceholder(final PlaceholderInfo info) {
        registerCustomPlaceholder(info.getHolder(), info);
    }
    
    public static void registerCustomPlaceholder(final Plugin p, final PlaceholderInfo info) {
        Preconditions.checkNotNull((Object)p);
        Preconditions.checkNotNull((Object)info);
        unregister(p, info.getId());
        PlaceholderManager.placeholders.put(p, info);
        info.injectForeignPlaceholders();
    }
    
    public static Collection<PlaceholderInfo> getRegisteredPlaceholders(final Plugin p) {
        return ImmutableList.copyOf(PlaceholderManager.placeholders.get(p));
    }
    
    public static PlaceholderInfo getPlaceholder(final Plugin p, final String name) {
        return getRegisteredPlaceholders(p).stream().filter(pi -> pi.getId().equalsIgnoreCase(name)).findAny().orElse(null);
    }
    
    public static void unregisterAll(final Plugin p) {
        PlaceholderManager.placeholders.removeAll(p);
    }
    
    public static void unregister(final Plugin p, final String pname) {
        for (final PlaceholderInfo pi : getRegisteredPlaceholders(p)) {
            if (pi.getId().equalsIgnoreCase(pname)) {
                PlaceholderManager.placeholders.remove(p, pi);
                pi.uninjectForeignPlaceholders();
            }
        }
    }
    
    public static void unregisterAll() {
        unloadFromClasses();
        PlaceholderManager.placeholders.values().forEach(PlaceholderInfo::uninjectForeignPlaceholders);
        PlaceholderManager.placeholders.clear();
    }
    
    public static Multimap<Plugin, PlaceholderInfo> getAllPlaceholders() {
        return ImmutableMultimap.copyOf((Multimap)PlaceholderManager.placeholders);
    }
    
    public static String proccessString(final Player p, final String message) {
        return proccessString(p, message, true);
    }
    
    public static String proccessString(final Player p, final String message, final boolean andColors) {
        return proccessString(p, message, andColors, false);
    }
    
    public static String proccessString(final Player p, String message, final boolean andColors, final boolean fromHooks) {
        for (final PlaceholderInfo pi : getAllPlaceholders().values()) {
            message = pi.proccess(p, message);
        }
        if (!fromHooks && UtilityMethods.has("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(p, message);
        }
        return (andColors ? UtilityMethods.color(message) : message).intern();
    }
    
    public static Collection<String> proccessCollection(final Player p, final Collection<String> mess) {
        return proccessCollection(p, mess, true);
    }
    
    public static Collection<String> proccessCollection(final Player p, final Collection<String> mess, final boolean andColors) {
        final List<String> newCol = new ArrayList<String>();
        for (final String mes : mess) {
            final String proc = proccessString(p, mes, andColors);
            newCol.add(proc);
        }
        return newCol;
    }
    
    public static ItemStack proccessItemStack(final Player p, final ItemStack is) {
        return proccessItemStack(p, is, true);
    }
    
    public static ItemStack proccessItemStack(final Player p, final ItemStack is, final boolean andColors) {
        return proccessItemStack(p, is, andColors, false);
    }
    
    public static ItemStack proccessItemStack(final Player p, final ItemStack is, final boolean andColors, final boolean checkSkull) {
        if (!is.hasItemMeta()) {
            return is;
        }
        boolean changes = false;
        final ItemMeta im = is.getItemMeta();
        if (checkSkull && im instanceof SkullMeta) {
            final SkullMeta sm = (SkullMeta)im;
            if (sm.hasOwner()) {
                final String owner = proccessString(p, sm.getOwner(), andColors);
                if (!Objects.equals(owner, sm.getOwner())) {
                    sm.setOwner(owner);
                    changes = true;
                }
            }
        }
        if (im.hasDisplayName()) {
            final String procName = proccessString(p, im.getDisplayName(), andColors);
            if (!Objects.equals(procName, im.getDisplayName())) {
                im.setDisplayName(procName);
                changes = true;
            }
        }
        if (im.hasLore()) {
            final List<String> procLore = (List<String>) proccessCollection(p, im.getLore(), andColors);
            if (!Objects.equals(procLore, im.getLore())) {
                im.setLore(procLore);
                changes = true;
            }
        }
        if (changes) {
            is.setItemMeta(im);
        }
        return is;
    }
    
    public static void unloadClassPlaceholder(final ClassPlaceholderInfo pl) {
        pl.uninjectForeignPlaceholders();
        PlaceholderManager.placeholders.remove(pl.getHolder(), pl);
        AutoCatch.run(pl.getHandlingPlaceholder().getLoader()::close);
    }
    
    public static void loadFromClasses() {
        for (final File file : getPlaceholdersFolder().listFiles((f, n) -> f.isDirectory() || n.endsWith(".class"))) {
            final LoadablePlaceholder pl = loadPlaceholderFromFile(file);
            if (pl != null) {
                registerClassPlaceholder(pl);
                CommonLib.INSTANCE.getLogger().info(pl.getId() + " v" + pl.getVersion() + " by " + pl.getAuthor() + " loaded from " + file.getName());
            }
        }
    }
    
    public static void unloadFromClasses() {
        getRegisteredPlaceholders(CommonLib.INSTANCE).stream().filter(ClassPlaceholderInfo.class::isInstance).map(ClassPlaceholderInfo.class::cast).forEach(PlaceholderManager::unloadClassPlaceholder);
    }
    
    public static ClassPlaceholderInfo registerClassPlaceholder(final LoadablePlaceholder pl) {
        final ClassPlaceholderInfo info = new ClassPlaceholderInfo(pl);
        pl.init();
        registerCustomPlaceholder(info);
        return info;
    }
    
    public static LoadablePlaceholder loadPlaceholderFromFile(final File file) {
        if (!file.exists()) {
            return null;
        }
        final PlaceholdersClassLoader loader = new PlaceholdersClassLoader(CommonLib.INSTANCE, file);
        return loader.lookupPlaceholder();
    }
    
    public static File getPlaceholdersFolder() {
        final File placeholdersFolder = new File(CommonLib.INSTANCE.getDataFolder(), "placeholders");
        if (!placeholdersFolder.exists()) {
            placeholdersFolder.mkdirs();
        }
        return placeholdersFolder;
    }
    
    static {
        PlaceholderManager.placeholders = MultimapBuilder.linkedHashKeys().arrayListValues().build();
    }
}
