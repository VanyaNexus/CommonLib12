package ru.den_abr.commonlib.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClibPAPIHook extends PlaceholderExpansion
{
    public String onRequest(final OfflinePlayer p, final String placeholder) {
        if (p != null && !(p instanceof Player)) {
            throw new IllegalArgumentException("Requested placeholder " + placeholder + " for non-player " + p.getName());
        }
        final Player player = (Player)p;
        final String proceded = PlaceholderManager.proccessString(player, "(" + placeholder + ")", true, true);
        if (("(" + placeholder + ")").equals(proceded)) {
            return null;
        }
        return proceded;
    }
    
    public String getAuthor() {
        return "Den_Abr";
    }
    
    public String getIdentifier() {
        return "clib";
    }
    
    public String getPlugin() {
        return "CommonLib";
    }
    
    public String getVersion() {
        return "1.0";
    }
}
