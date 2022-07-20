/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Score
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package ru.den_abr.commonlib.boards.packet;

import clib.com.comphenix.packetwrapper.AbstractPacket;
import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.boards.FastOfflinePlayer;
import ru.den_abr.commonlib.boards.packet.transactions.TeamDataTransaction;
import ru.den_abr.commonlib.events.MainScoreboardSetEvent;
import ru.den_abr.commonlib.events.PrepareScoreboardEvent;
import ru.den_abr.commonlib.nametags.NametagsManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class VirtualScoreboard
implements Scoreboard {
    public static final String CLIBBOARD_META = "ClibBoard";
    private Map<DisplaySlot, VirtualObjective> activeObjectives = new ConcurrentHashMap<DisplaySlot, VirtualObjective>(16, 0.75f, 1);
    private Map<String, VirtualObjective> objectives = new ConcurrentHashMap<String, VirtualObjective>(16, 0.75f, 1);
    private Map<String, Set<VirtualObjective>> criteriaObjectives = new ConcurrentHashMap<String, Set<VirtualObjective>>(16, 0.75f, 1);
    private Map<String, VirtualTeam> teams = new ConcurrentHashMap<String, VirtualTeam>(16, 0.75f, 1);
    private Set<Player> watching = new CopyOnWriteArraySet<Player>();
    private Set<UUID> watchingUuids = Collections.newSetFromMap(new HashMap());
    private Map<String, Set<VirtualScore>> scores = new ConcurrentHashMap<String, Set<VirtualScore>>(16, 0.75f, 1);

    void setDisplaySlot(DisplaySlot arg0, VirtualObjective objective) {
        VirtualObjective prev;
        VirtualObjective virtualObjective = prev = objective != null ? this.activeObjectives.put(arg0, objective) : this.activeObjectives.remove((Object)arg0);
        if (prev != null) {
            prev.displaySlot = null;
        }
        if (objective == null) {
            this.broadcast(this.getDisplayPacket(arg0, ""));
        } else {
            this.broadcast(this.getDisplayPacket(arg0, objective.getName()));
            objective.displaySlot = arg0;
        }
    }

    void removeObjective(VirtualObjective virtualObjective) {
        this.objectives.remove(virtualObjective.getName());
        this.getByCriteria(virtualObjective.getCriteria()).remove(virtualObjective);
        this.activeObjectives.forEach((key, value) -> {
            if (virtualObjective.equals(value)) {
                this.setDisplaySlot((DisplaySlot)key, null);
            }
        });
        this.broadcast(virtualObjective.getRemovePacket());
    }

    Set<VirtualObjective> getByCriteria(String criteria) {
        if (!this.criteriaObjectives.containsKey(criteria)) {
            this.criteriaObjectives.put(criteria, new CopyOnWriteArraySet());
        }
        return this.criteriaObjectives.get(criteria);
    }

    Set<VirtualScore> getScoresByName(String name) {
        if (!this.scores.containsKey(name)) {
            this.scores.put(name, new CopyOnWriteArraySet());
        }
        return this.scores.get(name);
    }

    void removeTeam(VirtualTeam team) {
        this.teams.remove(team.getName());
        this.broadcastTeam(team, VirtualTeam.TeamPacketType.DELETE);
    }

    void broadcast(AbstractPacket packet) {
        this.watching.forEach(packet::sendPacket);
    }

    public void set(Player p) {
        if (!p.hasMetadata(CLIBBOARD_META)) {
            this.watchingUuids.add(p.getUniqueId());
            this.watching.add(p);
            p.setMetadata(CLIBBOARD_META, (MetadataValue)new FixedMetadataValue((Plugin)CommonLib.INSTANCE, (Object)true));
            new PrepareScoreboardEvent(p, this).call();
            this.objectives.values().stream().map(obj -> obj.getCreatePacket(p)).forEach(packet -> packet.sendPacket(p));
            this.activeObjectives.forEach((slot, obj) -> this.getDisplayPacket((DisplaySlot)slot, obj != null ? obj.getName() : "").sendPacket(p));
            this.scores.values().stream().flatMap(Collection::stream).map(score -> score.getUpdatePacket(p)).forEach(packet -> packet.sendPacket(p));
            this.teams.forEach((name, team) -> team.sendCreatePacket(p));
            new MainScoreboardSetEvent(p).call();
        }
    }

    public Set<Player> getWatching() {
        return this.watching;
    }

    public boolean isWatching(Player player) {
        return this.watching.contains((Object)player);
    }

    public boolean notWatching(UUID uuid) {
        return !this.watchingUuids.contains(uuid);
    }

    public void unset(Player p) {
        this.watching.removeIf(pl -> pl.getName().equals(p.getName()));
        this.watchingUuids.remove(p.getUniqueId());
        this.teams.values().forEach(team -> team.unset(p));
        VirtualTeam team2 = this.getEntryTeam(p.getName());
        if (team2 != null && team2.isForNameTags()) {
            if (!NametagsManager.isGroupTagTeam(team2)) {
                team2.unregister();
            } else {
                team2.removeEntry(p.getName());
            }
        }
        for (DisplaySlot slot : DisplaySlot.values()) {
            this.getDisplayPacket(slot, "").sendPacket(p);
        }
        this.objectives.values().forEach(o -> o.unset(p));
        p.removeMetadata(CLIBBOARD_META, (Plugin)CommonLib.INSTANCE);
    }

    public void clearSlot(DisplaySlot arg0) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)arg0, (Object)"Display slot cannot be null");
        this.setDisplaySlot(arg0, null);
    }

    public Set<String> getEntries() {
        return ImmutableSet.copyOf(this.scores.keySet());
    }

    public VirtualTeam getEntryTeam(String arg0) throws IllegalArgumentException {
        return this.getTeams().stream().map(VirtualTeam.class::cast).filter(team -> team.hasEntry(arg0)).findAny().orElse(null);
    }

    public VirtualObjective getObjective(String arg0) throws IllegalArgumentException {
        return this.objectives.get(arg0);
    }

    public VirtualObjective getObjective(DisplaySlot arg0) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)arg0, (Object)"Display slot cannot be null");
        return this.activeObjectives.get((Object)arg0);
    }

    public Set<Objective> getObjectives() {
        return ImmutableSet.copyOf(this.objectives.values());
    }

    public Set<Objective> getObjectivesByCriteria(String arg0) throws IllegalArgumentException {
        return ImmutableSet.copyOf((Collection)this.criteriaObjectives.get(arg0));
    }

    public VirtualTeam getPlayerTeam(OfflinePlayer arg0) throws IllegalArgumentException {
        return this.getEntryTeam(arg0.getName());
    }

    public Set<OfflinePlayer> getPlayers() {
        return this.getEntries().stream().map(FastOfflinePlayer::get).collect(Collectors.toSet());
    }

    public Set<Score> getScores(OfflinePlayer arg0) throws IllegalArgumentException {
        return this.getScores(arg0.getName());
    }

    public Set<Score> getScores(String arg0) throws IllegalArgumentException {
        return this.scores.getOrDefault(arg0, Collections.EMPTY_SET);
    }

    public VirtualTeam getTeam(String arg0) throws IllegalArgumentException {
        return this.teams.get(arg0);
    }

    public VirtualTeam getEntryTeamOrCreate(String entry, String name, TeamDataTransaction trans) {
        VirtualTeam team = this.getEntryTeam(entry);
        if (team == null) {
            team = this.getOrCreate(name, trans);
            team.addEntry(entry);
        }
        return team;
    }

    public VirtualTeam getOrCreate(String name, TeamDataTransaction trans) {
        if (this.teams.containsKey(name)) {
            return this.teams.get(name);
        }
        return this.registerNewTeam(name, trans);
    }

    public Set<Team> getTeams() {
        return ImmutableSet.copyOf(this.teams.values());
    }

    public VirtualObjective registerNewObjective(String name, String criteria) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)name, (Object)"Name cannot be null");
        Preconditions.checkNotNull((Object)criteria, (Object)"Criteria cannot be null");
        Preconditions.checkArgument((!this.objectives.containsKey(name) ? 1 : 0) != 0, (Object)("Objective \"" + name + "\" already exists"));
        VirtualObjective objective = new VirtualObjective(name, criteria, this);
        this.objectives.put(name, objective);
        this.getByCriteria(criteria).add(objective);
        this.broadcastObjectiveCreate(objective);
        return objective;
    }

    public VirtualTeam registerNewTeam(String name) throws IllegalArgumentException {
        return this.registerNewTeam(name, null);
    }

    public VirtualTeam registerNewTeam(String name, TeamDataTransaction trans) {
        Preconditions.checkNotNull((Object)name, (Object)"Name cannot be null");
        Preconditions.checkArgument((!this.teams.containsKey(name) ? 1 : 0) != 0, (Object)("Team \"" + name + "\" already exists"));
        VirtualTeam team = new VirtualTeam(name, this);
        if (trans != null) {
            team.apply(trans);
        }
        this.teams.put(name, team);
        this.broadcastTeam(team, VirtualTeam.TeamPacketType.CREATE);
        return team;
    }

    public void resetScores(OfflinePlayer arg0) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)arg0, (Object)"Player cannot be null");
        this.resetScores(arg0.getName());
    }

    public void resetScores(String arg0) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)arg0, (Object)"Name cannot be null");
        this.getScoresByName(arg0).forEach(score -> {
            this.broadcast(score.getRemovePacket());
            ((VirtualObjective)score.getObjective()).removeScore((VirtualScore)score);
        });
        this.scores.remove(arg0);
    }

    WrapperPlayServerScoreboardDisplayObjective getDisplayPacket(DisplaySlot slot, String obj) {
        WrapperPlayServerScoreboardDisplayObjective packet = new WrapperPlayServerScoreboardDisplayObjective();
        packet.setPosition(this.toId(slot));
        packet.setScoreName(obj);
        return packet;
    }

    int toId(DisplaySlot slot) {
        switch (slot) {
            case BELOW_NAME: {
                return 2;
            }
            case PLAYER_LIST: {
                return 0;
            }
            case SIDEBAR: {
                return 1;
            }
        }
        return -1;
    }

    public void broadcastTeam(VirtualTeam virtualTeam, VirtualTeam.TeamPacketType updateType) {
        switch (updateType) {
            case UPDATE: {
                this.watching.forEach(virtualTeam::sendUpdatePacket);
                break;
            }
            case CREATE: {
                this.watching.forEach(virtualTeam::sendCreatePacket);
                break;
            }
            case DELETE: {
                WrapperPlayServerScoreboardTeam delete = virtualTeam.getRemovePacket();
                this.watching.forEach(delete::sendPacket);
                break;
            }
            case ADD_PLAYERS: 
            case REMOVE_PLAYERS: {
                System.out.println((Object)((Object)updateType) + " can't be used to broadcast " + virtualTeam.getName() + " changes");
                Thread.dumpStack();
            }
        }
    }

    public void broadcastObjectiveCreate(VirtualObjective obj) {
        this.watching.forEach(p -> obj.getCreatePacket((Player)p).sendPacket((Player)p));
    }

    public void broadcastObjectiveUpdate(VirtualObjective obj) {
        this.watching.forEach(p -> obj.getUpdatePacket((Player)p).sendPacket((Player)p));
    }

    public void broadcastScoreUpdate(VirtualScore virtualScore) {
        this.watching.forEach(p -> virtualScore.getUpdatePacket((Player)p).sendPacket((Player)p));
    }

    public void unsetAll() {
        this.watching.forEach(this::unset);
    }
}

