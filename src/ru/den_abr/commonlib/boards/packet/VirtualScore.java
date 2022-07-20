/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.wrappers.EnumWrappers$ScoreboardAction
 *  com.google.common.base.Preconditions
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Score
 *  org.bukkit.scoreboard.Scoreboard
 */
package ru.den_abr.commonlib.boards.packet;

import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import ru.den_abr.commonlib.boards.FastOfflinePlayer;
import ru.den_abr.commonlib.utility.PlayerMap;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualScore
implements Score {
    private final String entry;
    private int score = 0;
    private PlayerMap<Integer> playerScores = new PlayerMap(new ConcurrentHashMap());
    private Objective objective;

    public VirtualScore(String entry, Objective objective) {
        this.entry = entry;
        this.objective = objective;
    }

    public String getEntry() {
        return this.entry;
    }

    public Objective getObjective() {
        return this.objective;
    }

    public OfflinePlayer getPlayer() {
        return FastOfflinePlayer.get(this.entry);
    }

    public int getScore() throws IllegalStateException {
        return this.score;
    }

    public Scoreboard getScoreboard() {
        return this.objective.getScoreboard();
    }

    public boolean isScoreSet() throws IllegalStateException {
        return Objects.equals(this, (Object)this.objective.getScore(this.entry));
    }

    public void setScore(int score) throws IllegalStateException {
        this.checkValid();
        if (this.score != score) {
            this.score = score;
            ((VirtualScoreboard)this.objective.getScoreboard()).broadcastScoreUpdate(this);
        }
    }

    public void setScore(int score, Player p) throws IllegalStateException {
        this.playerScores.put(p, (Integer)score);
        this.getUpdatePacket(p).sendPacket(p);
    }

    public void resetScore(Player p) {
        Integer old = this.playerScores.put(p, -1337);
        if (old == null || old != -1337) {
            this.getRemovePacket().sendPacket(p);
        }
    }

    public void reset(Player p) {
        this.playerScores.removeOffline().remove((Object)p);
        if (this.objective != null) {
            this.getUpdatePacket(p).sendPacket(p);
        }
    }

    void checkValid() {
        Preconditions.checkState((this.objective != null ? 1 : 0) != 0, (Object)("Cannot modify unregistered score " + this.entry));
    }

    WrapperPlayServerScoreboardScore getUpdatePacket(Player p) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setObjectiveName(this.objective.getName());
        packet.setScoreboardAction(!this.playerScores.containsKey((Object)p) || this.playerScores.get((Object)p) != -1337 ? EnumWrappers.ScoreboardAction.CHANGE : EnumWrappers.ScoreboardAction.REMOVE);
        packet.setScoreName(this.entry);
        if (packet.getAction() == EnumWrappers.ScoreboardAction.CHANGE) {
            packet.setValue(this.playerScores.getOrDefault((Object)p, this.score));
        }
        return packet;
    }

    WrapperPlayServerScoreboardScore getRemovePacket() {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setObjectiveName(this.objective.getName());
        packet.setScoreboardAction(EnumWrappers.ScoreboardAction.REMOVE);
        packet.setScoreName(this.entry);
        packet.getHandle().addMetadata("CommonLib", (Object)true);
        return packet;
    }
}

