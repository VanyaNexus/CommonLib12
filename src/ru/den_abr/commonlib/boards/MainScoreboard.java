/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.DisplaySlot
 */
package ru.den_abr.commonlib.boards;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import ru.den_abr.commonlib.boards.packet.VirtualObjective;
import ru.den_abr.commonlib.boards.packet.VirtualScore;
import ru.den_abr.commonlib.boards.packet.VirtualScoreboard;
import ru.den_abr.commonlib.boards.packet.VirtualTeam;
import ru.den_abr.commonlib.boards.packet.transactions.TeamDataTransaction;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainScoreboard {
    private VirtualScoreboard board = new VirtualScoreboard();
    private int page = -1;

    public static String[] splitEntry(String input) {
        String[] s = new String[]{"", ""};
        if (input.length() > 32) {
            input = input.substring(0, 32);
        }
        if (input.length() > 16) {
            s[0] = input.substring(0, 16);
            s[1] = input.substring(16, input.length());
            if (s[0].endsWith("\u00a7")) {
                s[0] = s[0].substring(0, 15);
                s[1] = '\u00a7' + s[1];
            }
            s[1] = ChatColor.getLastColors((String)s[0]) + s[1];
            if (s[1].length() > 16) {
                s[1] = s[1].substring(0, 16);
            }
        } else {
            s[0] = input;
        }
        return s;
    }

    public void setLines(List<String> lines, Player player) {
        this.setLines(lines, player, this.page);
    }

    public void setLines(List<String> lines, Player player, int page) {
        this.setLines(lines, player, page, this.board);
    }

    public void setLines(List<String> lines, Player player, int page, VirtualScoreboard board) {
        this.setLinesWithColorEntry(PlaceholderManager.proccessCollection(player, lines).stream().map(line -> {
            String[] arr = new String[]{"", ""};
            if (line.contains("{spl}")) {
                String[] spl = line.split(Pattern.quote("{spl}"), 2);
                arr[0] = spl[0].substring(0, Math.min(spl[0].length(), 16));
                if (spl.length == 2) {
                    arr[1] = spl[1].substring(0, Math.min(spl[1].length(), 16));
                }
            } else {
                arr = MainScoreboard.splitEntry(line);
            }
            return new Trio(arr[0], arr[1]);
        }).collect(Collectors.toList()), player, page, board);
    }

    public void setLines(Map<String, Integer> lines, Player player, int page) {
        this.setLinesWithColorEntry(lines.entrySet().stream().map(entry -> {
            String line = (String)entry.getKey();
            String[] arr = new String[]{"", ""};
            if (line.contains("{spl}")) {
                String[] spl = line.split(Pattern.quote("{spl}"), 2);
                arr[0] = spl[0].substring(0, Math.min(spl[0].length(), 16));
                if (spl.length == 2) {
                    arr[1] = spl[1].substring(0, Math.min(spl[1].length(), 16));
                }
            } else {
                arr = MainScoreboard.splitEntry(line);
            }
            return new Trio(arr[0], arr[1], (Integer)entry.getValue());
        }).collect(Collectors.toList()), player, page, this.board);
    }

    void setLinesWithColorEntry(List<Trio> lines, Player player, int page, VirtualScoreboard board) {
        VirtualObjective obj = this.getObjective(page, board);
        Collections.reverse(lines);
        for (int i = 0; i < ChatColor.values().length; ++i) {
            String entryName;
            VirtualTeam team = board.getOrCreate("$pt-" + page + "-" + i, null);
            if (!team.hasEntry(entryName = this.getEntryString(i, page))) {
                team.addEntry(entryName);
            }
            TeamDataTransaction t = new TeamDataTransaction();
            VirtualScore score = obj.getScore(entryName);
            int scoreValue = -1;
            if (lines.size() > i) {
                Trio entry = lines.get(i);
                t.getPlayerPrefix().put(player, entry.prefix);
                t.getPlayerSuffix().put(player, entry.suffix);
                scoreValue = entry.score == -1 ? i : entry.score;
            } else {
                t.getPlayerPrefix().put(player, " ");
                t.getPlayerSuffix().put(player, " ");
            }
            team.pushTransaction(t, false);
            team.sendUpdatePacket(player);
            if (lines.size() > i) {
                score.setScore(scoreValue, player);
                continue;
            }
            score.resetScore(player);
        }
    }

    public VirtualObjective getObjective(int page) {
        return this.getObjective(page, this.board);
    }

    public VirtualObjective getObjective(int page, VirtualScoreboard board) {
        VirtualObjective objective = board.getObjective("" + page);
        if (objective == null) {
            objective = board.registerNewObjective(page + "", "dummy");
        }
        return objective;
    }

    public String getEntryString(int index, int page) {
        return (Object)ChatColor.values()[index] + ChatColor.values()[page].toString() + (Object)ChatColor.RESET;
    }

    public VirtualScoreboard getHandle() {
        return this.board;
    }

    public int getPage() {
        return this.page;
    }

    public void setTitle(String title) {
        this.prepareObjective();
        this.setTitle(title, this.page);
    }

    public void setTitle(String title, int page) {
        this.getObjective(page).setDisplayName(UtilityMethods.color(title));
    }

    public void setTitle(String title, Player player) {
        this.prepareObjective();
        this.setTitle(title, player, this.page);
    }

    public void setTitle(String title, Player player, int page) {
        this.getObjective(page).setDisplayName(PlaceholderManager.proccessString(player, title), player);
    }

    private void prepareObjective() {
        if (this.page == -1) {
            this.setActiveObjective(1);
        }
    }

    public VirtualObjective getActiveObjective() {
        this.prepareObjective();
        return this.getObjective(this.page);
    }

    public void setActiveObjective(int page) {
        this.getObjective(page).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.page = page;
    }

    public void reset() {
        VirtualObjective objective;
        for (int i = 1; i < 100 && (objective = this.getObjective(i)) != null; ++i) {
            VirtualTeam team;
            objective.unregister();
            for (int n = 0; n < ChatColor.values().length && (team = this.board.getTeam("$pt-" + this.page + "-" + n)) != null; ++n) {
                team.unregister();
            }
        }
        this.page = -1;
    }

    private class Trio {
        public String prefix;
        public String suffix;
        public int score;

        public Trio(String prefix, String suffix, int score) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.score = score;
        }

        public Trio(String prefix, String suffix) {
            this(prefix, suffix, -1);
        }

        public String toString() {
            return this.prefix + ";" + this.suffix + ";" + this.score;
        }
    }
}

