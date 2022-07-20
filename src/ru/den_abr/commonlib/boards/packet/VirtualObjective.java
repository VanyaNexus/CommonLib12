/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 */
package ru.den_abr.commonlib.boards.packet;

import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import ru.den_abr.commonlib.utility.PlayerMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VirtualObjective
implements Objective {
    private VirtualScoreboard scoreboard;
    private final String name;
    private String displayName;
    private PlayerMap<String> playerNames = new PlayerMap();
    private String criteria;
    protected DisplaySlot displaySlot;
    private Map<String, VirtualScore> scores = new HashMap<String, VirtualScore>();

    public VirtualObjective(String name, String criteria, VirtualScoreboard board) {
        this.name = name;
        this.displayName = name;
        this.scoreboard = board;
        this.criteria = criteria;
    }

    public String getCriteria() throws IllegalStateException {
        return this.criteria;
    }

    public String getDisplayName() throws IllegalStateException {
        return this.displayName;
    }

    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        return this.displaySlot;
    }

    public String getName() throws IllegalStateException {
        return this.name;
    }

    public VirtualScore getScore(OfflinePlayer arg0) throws IllegalArgumentException, IllegalStateException {
        return this.getScore(arg0.getName());
    }

    public List<VirtualScore> getScores() {
        return ImmutableList.copyOf(this.scores.values());
    }

    public VirtualScore getScore(String arg0) throws IllegalArgumentException, IllegalStateException {
        this.checkValid();
        VirtualScore score = this.scores.get(arg0);
        if (score == null) {
            score = new VirtualScore(arg0, this);
            this.scores.put(arg0, score);
            this.scoreboard.getScoresByName(arg0).add(score);
        }
        return score;
    }

    public VirtualScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isModifiable() throws IllegalStateException {
        return true;
    }

    public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
        this.checkValid();
        Preconditions.checkNotNull((Object)displayName, (Object)"displayName cannot be null");
        Preconditions.checkArgument((displayName.length() <= 32 ? 1 : 0) != 0, (Object)"displayName cannot be longer than 32 characters");
        if (!this.displayName.equals(displayName)) {
            this.displayName = displayName;
            this.scoreboard.broadcastObjectiveUpdate(this);
        }
    }

    public void setDisplayName(String displayName, Player p) {
        this.checkValid();
        Preconditions.checkNotNull((Object)displayName, (Object)"displayName cannot be null");
        Preconditions.checkArgument((displayName.length() <= 32 ? 1 : 0) != 0, (Object)"displayName cannot be longer than 32 characters");
        if (!Objects.equals(displayName, this.playerNames.put(p, displayName))) {
            this.getUpdatePacket(p).sendPacket(p);
        }
    }

    public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {
        this.checkValid();
        if (slot != this.displaySlot) {
            if (this.displaySlot != null) {
                this.scoreboard.setDisplaySlot(this.displaySlot, null);
            }
            if (slot != null) {
                this.scoreboard.setDisplaySlot(slot, this);
            }
        }
    }

    public void unregister() throws IllegalStateException {
        this.checkValid();
        this.scores.forEach((key, value) -> this.scoreboard.getScoresByName((String)key).remove(value));
        this.scoreboard.removeObjective(this);
        this.scoreboard = null;
    }

    WrapperPlayServerScoreboardObjective getCreatePacket(Player p) {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setName(this.getName());
        packet.setDisplayName(this.playerNames.getOrDefault((Object)p, this.getDisplayName()));
        packet.setMode(0);
        packet.setHealthDisplay(this.getCriteria().equals("health") ? "hearts" : "integer");
        packet.getHandle().addMetadata("CommonLib", (Object)true);
        return packet;
    }

    WrapperPlayServerScoreboardObjective getRemovePacket() {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setName(this.getName());
        packet.setMode(1);
        packet.getHandle().addMetadata("CommonLib", (Object)true);
        return packet;
    }

    WrapperPlayServerScoreboardObjective getUpdatePacket(Player p) {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setName(this.getName());
        packet.setDisplayName(this.playerNames.getOrDefault((Object)p, this.getDisplayName()));
        packet.setMode(2);
        packet.setHealthDisplay(this.getCriteria().equals("health") ? "hearts" : "integer");
        packet.getHandle().addMetadata("CommonLib", (Object)true);
        return packet;
    }

    void removeScore(VirtualScore score) {
        this.scores.remove(score.getEntry());
    }

    void checkValid() {
        Preconditions.checkState((this.scoreboard != null ? 1 : 0) != 0, (Object)"Cannot modify unregistered objective");
    }

    public void unset(Player p) {
        this.getRemovePacket().sendPacket(p);
        this.scores.values().forEach(score -> score.reset(p));
        this.playerNames.remove((Object)p);
    }
}

