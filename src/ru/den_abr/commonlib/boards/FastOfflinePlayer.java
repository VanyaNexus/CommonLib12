/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.boards;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class FastOfflinePlayer implements OfflinePlayer {
    private String playerName;
    private final UUID uuid;
    private static final Map<String, FastOfflinePlayer> cache = new HashMap<String, FastOfflinePlayer>();

    public static FastOfflinePlayer get(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) {
            cache.put(name, FastOfflinePlayer.get(name, p.getUniqueId()));
        } else {
            cache.put(name, new FastOfflinePlayer(name));
        }
        return FastOfflinePlayer.get(name);
    }

    public static FastOfflinePlayer getWithOfflineUUID(String correctCasedName) {
        Player p = Bukkit.getPlayerExact(correctCasedName);
        if (p != null) {
            return FastOfflinePlayer.get(p.getName(), p.getUniqueId());
        }
        return FastOfflinePlayer.get(correctCasedName, UUID.nameUUIDFromBytes(("OfflinePlayer:" + correctCasedName).getBytes(StandardCharsets.UTF_8)));
    }

    public static FastOfflinePlayer get(String name, UUID uuid) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        FastOfflinePlayer pl = new FastOfflinePlayer(name, uuid);
        cache.put(name, pl);
        return pl;
    }

    FastOfflinePlayer(String playerName) {
        this(playerName, UUID.randomUUID());
    }

    FastOfflinePlayer(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public boolean isOnline() {
        return false;
    }

    public String getName() {
        return this.playerName;
    }

    public boolean isBanned() {
        return false;
    }

    public boolean isWhitelisted() {
        return false;
    }

    public void setWhitelisted(boolean value) {
    }

    public Player getPlayer() {
        return null;
    }

    public long getFirstPlayed() {
        return System.currentTimeMillis();
    }

    public long getLastPlayed() {
        return System.currentTimeMillis();
    }

    public boolean hasPlayedBefore() {
        return false;
    }

    public Location getBedSpawnLocation() {
        return new Location(Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0);
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean value) {
    }

    public void setName(String name) {
        this.playerName = name;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap result = Maps.newLinkedHashMap();
        result.put("UUID", this.getUniqueId());
        result.put("name", this.playerName);
        return result;
    }

    public int hashCode() {
        return Objects.hashCode(this.playerName);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastOfflinePlayer)) {
            return false;
        }
        FastOfflinePlayer other = (FastOfflinePlayer)obj;
        return Objects.equals(this.playerName, other.playerName);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FastOfflinePlayer [playerName=").append(this.playerName).append(", uuid=").append(this.uuid).append("]");
        return builder.toString();
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public void setBanned(boolean arg0) {
    }
}

