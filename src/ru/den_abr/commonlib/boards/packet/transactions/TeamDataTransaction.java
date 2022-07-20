/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.NameTagVisibility
 */
package ru.den_abr.commonlib.boards.packet.transactions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import ru.den_abr.commonlib.boards.packet.VirtualTeam;
import ru.den_abr.commonlib.utility.PlayerMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeamDataTransaction {
    private String displayName;
    private String prefix;
    private String suffix;
    private Set<String> addEntries = new HashSet<String>();
    private Set<String> removeEntries = new HashSet<String>();
    private PlayerMap<String> playerPrefix = PlayerMap.concurrentSingle();
    private PlayerMap<String> playerSuffix = PlayerMap.concurrentSingle();
    private NameTagVisibility nametagVisibility;
    private VirtualTeam.CollideRule collideRule;
    private Boolean allowFriendlyFire;
    private Boolean canSeeFriendlyInvisibles;
    private boolean consumed = false;

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public Boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public Boolean getCanSeeFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    public VirtualTeam.CollideRule getCollideRule() {
        return this.collideRule;
    }

    public NameTagVisibility getNametagVisibility() {
        return this.nametagVisibility;
    }

    public Map<Player, String> getPlayerPrefix() {
        return this.playerPrefix.removeOffline();
    }

    public Map<Player, String> getPlayerSuffix() {
        return this.playerSuffix.removeOffline();
    }

    public Set<String> getAddEntries() {
        return this.addEntries;
    }

    public Set<String> getRemoveEntries() {
        return this.removeEntries;
    }

    public void setDisplayName(String displayName) {
        Preconditions.checkNotNull((Object)displayName);
        this.displayName = displayName;
    }

    public void setPrefix(String prefix) {
        Preconditions.checkNotNull((Object)prefix);
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        Preconditions.checkNotNull((Object)suffix);
        this.suffix = suffix;
    }

    public void addEntry(String entry) {
        this.addEntries.add(entry);
    }

    public void removeEntry(String entry) {
        this.removeEntries.add(entry);
    }

    public void setNametagVisibility(NameTagVisibility nametagVisibility) {
        this.nametagVisibility = nametagVisibility;
    }

    public void setAllowFriendlyFire(Boolean allowFriendlyFire) {
        this.allowFriendlyFire = allowFriendlyFire;
    }

    public void setCollideRule(VirtualTeam.CollideRule collideRule) {
        this.collideRule = collideRule;
    }

    public void setCanSeeFriendlyInvisibles(Boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    }

    public void consume() {
        this.consumed = true;
    }

    public Set<VirtualTeam.TeamPacketType> calculatePacketTypes() {
        ImmutableSet.Builder b = new ImmutableSet.Builder();
        if (this.isConsumed()) {
            return b.build();
        }
        if (this.displayName != null || this.prefix != null || this.suffix != null || !this.playerPrefix.isEmpty() || !this.playerSuffix.isEmpty() || this.allowFriendlyFire != null || this.canSeeFriendlyInvisibles != null || this.collideRule != null || this.nametagVisibility != null) {
            b.add((Object)VirtualTeam.TeamPacketType.UPDATE);
        }
        return b.build();
    }
}

