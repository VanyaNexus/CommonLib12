/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.NameTagVisibility
 *  org.bukkit.scoreboard.Team
 */
package ru.den_abr.commonlib.boards.packet;

import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;
import ru.den_abr.commonlib.boards.FastOfflinePlayer;
import ru.den_abr.commonlib.boards.packet.transactions.TeamDataTransaction;
import ru.den_abr.commonlib.utility.PlayerMap;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


public class VirtualTeam implements Team {
    private final String name;
    private VirtualScoreboard scoreboard;
    private String prefix = "";
    private String suffix = "";
    private final PlayerMap<String> playerPrefix = PlayerMap.concurrentSingle();
    private final PlayerMap<String> playerSuffix = PlayerMap.concurrentSingle();
    private String displayName;
    private final Set<String> players = new CopyOnWriteArraySet<String>();
    private NameTagVisibility nametagVisibility = NameTagVisibility.ALWAYS;
    private CollideRule collideRule = CollideRule.ALWAYS;
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private boolean forNameTags = false;

    private ChatColor chatColor;

    public VirtualTeam(String name, VirtualScoreboard scoreboard) {
        Preconditions.checkArgument((name.length() < 17 ? 1 : 0) != 0, "Given value length " + name + " > 16");
        this.name = name;
        this.scoreboard = scoreboard;
        this.displayName = name;
    }

    public OptionStatus getOption(Option option) throws IllegalStateException {
        return null;
    }

    public void setOption(Option option, OptionStatus optionStatus) throws IllegalStateException {
        return;
    }
    public ChatColor getColor() throws IllegalStateException {
        return this.chatColor;
    }

    public void setColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public void addEntry(String arg0) throws IllegalStateException, IllegalArgumentException {
        this.checkValid();
        this.findAndRemoveFromAnotherTeam(arg0);
        if (this.players.add(arg0)) {
            this.scoreboard.broadcast(this.getUpdateAddPlayersPacket(Collections.singletonList(arg0)));
        }
    }

    void findAndRemoveFromAnotherTeam(String entry) {
        this.scoreboard.getTeams().stream().filter(t -> !this.equals(t) && t.hasEntry(entry)).forEach(t -> t.removeEntry(entry));
    }

    public void addPlayer(OfflinePlayer arg0) throws IllegalStateException, IllegalArgumentException {
        this.checkValid();
        this.addEntry(arg0.getName());
    }

    public boolean allowFriendlyFire() throws IllegalStateException {
        return this.allowFriendlyFire;
    }

    public void setForNameTags(boolean forNameTags) {
        this.forNameTags = forNameTags;
    }

    public boolean isForNameTags() {
        return this.forNameTags;
    }

    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        return this.canSeeFriendlyInvisibles;
    }

    public String getDisplayName() throws IllegalStateException {
        return this.displayName;
    }

    public Set<String> getEntries() throws IllegalStateException {
        return ImmutableSet.copyOf(this.players);
    }

    public String getName() throws IllegalStateException {
        return this.name;
    }

    public NameTagVisibility getNameTagVisibility() throws IllegalArgumentException {
        return this.nametagVisibility;
    }

    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        return this.getEntries().stream().map(FastOfflinePlayer::get).collect(Collectors.toSet());
    }

    public String getPrefix() throws IllegalStateException {
        return this.prefix;
    }

    public VirtualScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getSize() throws IllegalStateException {
        return this.players.size();
    }

    public String getSuffix() throws IllegalStateException {
        return this.suffix;
    }

    public boolean hasEntry(String arg0) throws IllegalArgumentException, IllegalStateException {
        return this.players.contains(arg0);
    }

    public boolean hasPlayer(OfflinePlayer arg0) throws IllegalArgumentException, IllegalStateException {
        return this.hasEntry(arg0.getName());
    }

    public boolean removeEntry(String arg0) throws IllegalStateException, IllegalArgumentException {
        this.checkValid();
        boolean had = this.players.remove(arg0);
        if (had) {
            this.scoreboard.broadcast(this.getUpdateRemovePlayersPacket(Collections.singletonList(arg0)));
        }
        return had;
    }

    public boolean removePlayer(OfflinePlayer arg0) throws IllegalStateException, IllegalArgumentException {
        this.checkValid();
        return this.removeEntry(arg0.getName());
    }

    public void setAllowFriendlyFire(boolean arg0) throws IllegalStateException {
        this.checkValid();
        if (arg0 != this.allowFriendlyFire) {
            this.allowFriendlyFire = arg0;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }

    public void setCanSeeFriendlyInvisibles(boolean arg0) throws IllegalStateException {
        this.checkValid();
        if (arg0 != this.canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = arg0;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }

    public CollideRule getCollideRule() {
        return this.collideRule;
    }

    public void setCollideRule(CollideRule collideRule) {
        this.checkValid();
        this.collideRule = collideRule;
    }

    public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
        Preconditions.checkArgument((displayName.length() < 17 ? 1 : 0) != 0, "Given value length " + displayName + " > 16");
        this.checkValid();
        if (!Objects.equals(displayName, this.displayName)) {
            this.displayName = displayName;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }

    public void setNameTagVisibility(NameTagVisibility arg0) throws IllegalArgumentException {
        this.checkValid();
        if (arg0 != this.nametagVisibility) {
            this.nametagVisibility = arg0;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }

    public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
        Preconditions.checkArgument((prefix.length() < 17 ? 1 : 0) != 0, "Given value length " + prefix + " > 16");
        this.checkValid();
        if (!Objects.equals(prefix, this.prefix)) {
            this.prefix = prefix;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }

    public void setPrefix(String prefix, Player p) {
        Preconditions.checkArgument((prefix.length() < 17 ? 1 : 0) != 0, "Given value length " + prefix + " > 16");
        this.checkValid();
        String put = this.playerPrefix.put(p, prefix);
        if (!prefix.equals(put)) {
            this.sendUpdatePacket(p);
        }
    }

    public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
        Preconditions.checkArgument((suffix.length() < 17 ? 1 : 0) != 0, "Given value length " + suffix + " > 16");
        this.checkValid();
        if (!Objects.equals(suffix, this.suffix)) {
            this.suffix = suffix;
            this.scoreboard.broadcastTeam(this, TeamPacketType.UPDATE);
        }
    }


    public void setSuffix(String suffix, Player p) {
        Preconditions.checkArgument((suffix.length() < 17 ? 1 : 0) != 0, "Given value length " + suffix + " > 16");
        this.checkValid();
        if (!suffix.equals(this.playerSuffix.put(p, suffix))) {
            this.sendUpdatePacket(p);
        }
    }

    public void resetSuffix(Player p) {
        this.playerSuffix.remove(p);
        this.sendUpdatePacket(p);
    }

    public void resetPrefix(Player p) {
        this.playerPrefix.remove(p);
        this.sendUpdatePacket(p);
    }

    public void unregister() throws IllegalStateException {
        this.checkValid();
        this.scoreboard.removeTeam(this);
        this.scoreboard = null;
    }

    public void pushTransaction(TeamDataTransaction t) {
        this.pushTransaction(t, true);
    }

    public void pushTransaction(TeamDataTransaction t, boolean broadcast) {
        Set<TeamPacketType> types = t.calculatePacketTypes();
        if (!this.isValid() || t.isConsumed() || broadcast && types.isEmpty()) {
            return;
        }
        this.apply(t);
        if (!t.getRemoveEntries().isEmpty() && broadcast) {
            this.scoreboard.broadcast(this.getUpdateRemovePlayersPacket(ImmutableList.copyOf(t.getRemoveEntries())));
        }
        if (!t.getAddEntries().isEmpty() && broadcast) {
            this.scoreboard.broadcast(this.getUpdateAddPlayersPacket(ImmutableList.copyOf(t.getAddEntries())));
        }
        if (broadcast) {
            types.forEach(type -> this.scoreboard.broadcastTeam(this, (TeamPacketType) type));
        }
    }

    public void apply(TeamDataTransaction t) {
        if (t.isConsumed()) {
            return;
        }
        if (t.getDisplayName() != null) {
            this.displayName = t.getDisplayName();
        }
        if (t.getPrefix() != null) {
            this.prefix = t.getPrefix();
        }
        if (t.getSuffix() != null) {
            this.suffix = t.getSuffix();
        }
        if (!t.getPlayerPrefix().isEmpty()) {
            t.getPlayerPrefix().forEach((k, v) -> {
                if (v == null) {
                    this.playerPrefix.remove(k);
                } else {
                    this.playerPrefix.put(k, v);
                }
            });
        }
        if (!t.getPlayerSuffix().isEmpty()) {
            t.getPlayerSuffix().forEach((k, v) -> {
                if (v == null) {
                    this.playerSuffix.remove(k);
                } else {
                    this.playerSuffix.put(k, v);
                }
            });
        }
        if (t.getAllowFriendlyFire() != null) {
            this.allowFriendlyFire = t.getAllowFriendlyFire();
        }
        if (t.getCanSeeFriendlyInvisibles() != null) {
            this.canSeeFriendlyInvisibles = t.getCanSeeFriendlyInvisibles();
        }
        if (t.getCollideRule() != null) {
            this.collideRule = t.getCollideRule();
        }
        if (t.getNametagVisibility() != null) {
            this.nametagVisibility = t.getNametagVisibility();
        }
        if (!t.getRemoveEntries().isEmpty()) {
            this.players.removeAll(t.getRemoveEntries());
        }
        if (!t.getAddEntries().isEmpty()) {
            List<String> filtered = t.getAddEntries().stream().filter(s -> !this.players.contains(s)).collect(Collectors.toList());
            filtered.forEach(this::findAndRemoveFromAnotherTeam);
            this.players.addAll(filtered);
        }
        t.consume();
    }

    public boolean isValid() {
        return this.scoreboard != null;
    }

    void checkValid() {
        Preconditions.checkState(this.isValid(), "Cant modify unregistered team");
    }

    WrapperPlayServerScoreboardTeam getCreatePacket(Player p) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(this.name);
        packet.setDisplayName(this.displayName);
        packet.setPrefix(this.prefix);
        packet.setSuffix(this.suffix);
        packet.setPlayers(ImmutableList.copyOf(this.players));
        packet.setColor(0);
        packet.setNameTagVisibility(this.toInternal(this.nametagVisibility));
        packet.setCollideRule(this.collideRule);
        packet.setPackOptionData(this.getData());
        packet.setMode(TeamPacketType.CREATE.getMode());
        packet.getHandle().addMetadata("CommonLib", true);
        return packet;
    }

    WrapperPlayServerScoreboardTeam getUpdatePacket(Player p) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(this.name);
        packet.setMode(2);
        packet.setDisplayName(this.displayName);
        packet.setPrefix(this.playerPrefix.getOrDefault(p, this.prefix));
        packet.setSuffix(this.playerSuffix.getOrDefault(p, this.suffix));
        packet.setPackOptionData(this.getData());
        packet.setNameTagVisibility(this.toInternal(this.nametagVisibility));
        packet.setCollideRule(this.collideRule);
        packet.setMode(TeamPacketType.UPDATE.getMode());
        packet.getHandle().addMetadata("CommonLib", true);
        return packet;
    }

    public void sendUpdatePacket(Player p) {
        this.getUpdatePacket(p).sendPacket(p);
    }

    void sendCreatePacket(Player p) {
        this.getCreatePacket(p).sendPacket(p);
    }

    WrapperPlayServerScoreboardTeam getUpdateAddPlayersPacket(List<String> addPlayers) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(this.name);
        packet.setPlayers(addPlayers);
        packet.setMode(TeamPacketType.ADD_PLAYERS.getMode());
        packet.getHandle().addMetadata("CommonLib", true);
        return packet;
    }

    WrapperPlayServerScoreboardTeam getUpdateRemovePlayersPacket(List<String> removedPlayers) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(this.name);
        packet.setPlayers(removedPlayers);
        packet.setMode(TeamPacketType.REMOVE_PLAYERS.getMode());
        packet.getHandle().addMetadata("CommonLib", true);
        return packet;
    }

    WrapperPlayServerScoreboardTeam getRemovePacket() {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(this.name);
        packet.setMode(TeamPacketType.DELETE.getMode());
        packet.getHandle().addMetadata("CommonLib", true);
        return packet;
    }

    public VirtualTeam registerClone(String newName) {
        TeamDataTransaction trans = new TeamDataTransaction();
        trans.setPrefix(this.prefix);
        trans.setSuffix(this.suffix);
        trans.setAllowFriendlyFire(this.allowFriendlyFire);
        trans.setCanSeeFriendlyInvisibles(this.canSeeFriendlyInvisibles);
        trans.setNametagVisibility(this.nametagVisibility);
        trans.setDisplayName(this.displayName);
        trans.setCollideRule(this.collideRule);
        VirtualTeam team = this.getScoreboard().registerNewTeam(newName, trans);
        return team;
    }

    int getData() {
        int data = 0;
        if (this.allowFriendlyFire) {
            data |= 1;
        }
        if (this.canSeeFriendlyInvisibles) {
            data |= 2;
        }
        return data;
    }

    String toInternal(NameTagVisibility visibility) {
        switch (visibility) {
            case ALWAYS: {
                return "always";
            }
            case HIDE_FOR_OTHER_TEAMS: {
                return "hideForOtherTeams";
            }
            case HIDE_FOR_OWN_TEAM: {
                return "hideForOwnTeam";
            }
            case NEVER: {
                return "never";
            }
        }
        return null;
    }

    public void unset(Player p) {
        this.playerPrefix.remove(p);
        this.playerSuffix.remove(p);
        this.getRemovePacket().sendPacket(p);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VirtualTeam [name=").append(this.name).append(", scoreboard=").append(this.scoreboard).append(", prefix=").append(this.prefix).append(", suffix=").append(this.suffix).append(", playerPrefix=").append(this.playerPrefix).append(", playerSuffix=").append(this.playerSuffix).append(", displayName=").append(this.displayName).append(", players=").append(this.players).append(", nametagVisibility=").append(this.nametagVisibility).append(", collideRule=").append(this.collideRule).append(", allowFriendlyFire=").append(this.allowFriendlyFire).append(", canSeeFriendlyInvisibles=").append(this.canSeeFriendlyInvisibles).append(", forNameTags=").append(this.forNameTags).append("]");
        return builder.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.scoreboard == null ? 0 : this.scoreboard.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VirtualTeam)) {
            return false;
        }
        VirtualTeam other = (VirtualTeam)obj;
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        return !(this.scoreboard == null ? other.scoreboard != null : !this.scoreboard.equals(other.scoreboard));
    }

    public enum CollideRule {
        ALWAYS("always"),
        PUSH_OTHER("pushOtherTeams"),
        PUSH_OWN("pushOwnTeam"),
        NEVER("never");

        private final String internal;

        CollideRule(String rule) {
            this.internal = rule;
        }

        public String getId() {
            return this.internal;
        }
    }

    public enum TeamPacketType {
        CREATE(0),
        DELETE(1),
        UPDATE(2),
        ADD_PLAYERS(3),
        REMOVE_PLAYERS(4);

        private final int mode;

        TeamPacketType(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }

        public static TeamPacketType fromId(int i) {
            for (TeamPacketType type : TeamPacketType.values()) {
                if (type.mode != i) continue;
                return type;
            }
            return null;
        }
    }
}

