/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.nametags;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.boards.packet.VirtualTeam;
import ru.den_abr.commonlib.boards.packet.transactions.TeamDataTransaction;
import ru.den_abr.commonlib.events.NametagApplyEvent;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.HashMap;
import java.util.Map;

public class NametagsManager {
    public static String GROUP_TEAM_PREFIX = "$GRT-";
    public static String GROUP_ENTRY_PREFIX = "\u00a7r$GRE-";
    private static Map<String, Nametag> groupTags = new HashMap<String, Nametag>();
    private static Map<String, Nametag> playerTags = new HashMap<String, Nametag>();
    private static boolean disablePush = true;
    private static char[] alphabet = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static boolean isDisablePush() {
        return disablePush;
    }

    public static void setDisablePush(boolean disablePush) {
        NametagsManager.disablePush = disablePush;
    }

    public static void setGroupPriority(String group, int prior) {
        Preconditions.checkArgument((prior >= -1 && prior < alphabet.length ? 1 : 0) != 0);
        NametagsManager.setGroupNametag(group, null, null, prior);
    }

    public static void setGroupNametag(String group, String prefix, String suffix, int priority) {
        Nametag tag;
        Preconditions.checkArgument((priority == Integer.MIN_VALUE || priority >= -1 && priority < alphabet.length ? 1 : 0) != 0, (Object)("Illegal priority value. Must be between -1 and " + alphabet.length + ". Given - " + priority));
        if (prefix != null) {
            prefix = prefix.substring(0, Math.min(16, prefix.length()));
        }
        if (suffix != null) {
            suffix = suffix.substring(0, Math.min(16, suffix.length()));
        }
        if ((tag = NametagsManager.getGroupNametag(group)) == null) {
            tag = new Nametag(group, prefix, suffix, priority, Nametag.Type.GROUP);
            groupTags.put(group.toLowerCase(), tag);
        } else {
            if (prefix != null) {
                tag.setPrefix(prefix);
            }
            if (suffix != null) {
                tag.setSuffix(suffix);
            }
            if (priority != Integer.MIN_VALUE) {
                tag.setPriority(priority);
            }
        }
        String teamEntry = NametagsManager.getGroupEntry(group);
        TeamDataTransaction trans = new TeamDataTransaction();
        trans.setCollideRule(NametagsManager.getCollideRule());
        trans.setPrefix(NametagsManager.color(tag.getPrefixSafe()));
        trans.setSuffix(NametagsManager.color(tag.getSuffixSafe()));
        trans.addEntry(teamEntry);
        String teamName = NametagsManager.getGroupTeam(group, tag.getPriority());
        VirtualTeam oldTeam = CommonLib.getMainScoreboard().getHandle().getEntryTeam(teamEntry);
        if (oldTeam != null) {
            if (oldTeam.getName().equals(teamName)) {
                oldTeam.pushTransaction(trans);
                return;
            }
            if (NametagsManager.isGroupTagTeam(oldTeam)) {
                oldTeam.getEntries().forEach(trans::addEntry);
                oldTeam.unregister();
            }
        }
        VirtualTeam team = CommonLib.getMainScoreboard().getHandle().getOrCreate(teamName, trans);
        team.setForNameTags(true);
        team.pushTransaction(trans);
    }

    public static void setGroupNametag(String group, String prefix, String suffix) {
        NametagsManager.setGroupNametag(group, prefix, suffix, Integer.MIN_VALUE);
    }

    public static void applyNameTag(Player player, Nametag tag) {
        Preconditions.checkNotNull((Object)tag);
        NametagApplyEvent event = (NametagApplyEvent)((Object)new NametagApplyEvent(player, tag).call());
        if (event.isCancelled()) {
            return;
        }
        tag = event.getTag();
        if (tag.getType() == Nametag.Type.PLAYER) {
            TeamDataTransaction trans = new TeamDataTransaction();
            trans.setCollideRule(NametagsManager.getCollideRule());
            trans.addEntry(player.getName());
            trans.setPrefix(NametagsManager.color(tag.getPrefixSafe()));
            trans.setSuffix(NametagsManager.color(tag.getSuffixSafe()));
            String teamname = NametagsManager.getPlayerTeam(player, tag.getPriority());
            VirtualTeam oldTeam = CommonLib.getMainScoreboard().getHandle().getEntryTeam(player.getName());
            if (oldTeam != null && oldTeam.isForNameTags()) {
                if (NametagsManager.isGroupTagTeam(oldTeam)) {
                    oldTeam.removePlayer((OfflinePlayer)player);
                } else if (!oldTeam.getName().equalsIgnoreCase(teamname)) {
                    oldTeam.unregister();
                } else {
                    oldTeam.pushTransaction(trans);
                    return;
                }
            }
            VirtualTeam team = CommonLib.getMainScoreboard().getHandle().getEntryTeamOrCreate(player.getName(), teamname, trans);
            team.setForNameTags(true);
            team.pushTransaction(trans);
        } else {
            VirtualTeam team;
            VirtualTeam oldTeam = CommonLib.getMainScoreboard().getHandle().getEntryTeam(player.getName());
            if (oldTeam != null && oldTeam.isForNameTags() && !NametagsManager.isGroupTagTeam(oldTeam)) {
                oldTeam.unregister();
            }
            if ((team = CommonLib.getMainScoreboard().getHandle().getEntryTeam(NametagsManager.getGroupEntry(tag.getHolder()))) == null) {
                System.out.println("Setting " + player.getName() + " tag to not nametagged team " + tag.getHolder());
                team = CommonLib.getMainScoreboard().getHandle().getEntryTeamOrCreate(NametagsManager.getGroupEntry(tag.getHolder()), NametagsManager.getGroupTeam(tag.getHolder(), tag.getPriority()), null);
                team.setForNameTags(true);
            }
            team.addEntry(player.getName());
        }
    }

    private static VirtualTeam.CollideRule getCollideRule() {
        return NametagsManager.isDisablePush() ? VirtualTeam.CollideRule.NEVER : VirtualTeam.CollideRule.ALWAYS;
    }

    public static void setPlayerPriority(Player player, int prior) {
        Preconditions.checkArgument((prior == -1 || prior >= 0 && prior < alphabet.length ? 1 : 0) != 0, (Object)(prior + " is not between 0 and " + (alphabet.length - 1)));
        NametagsManager.setPlayerNametag(player, null, null, prior);
    }

    public static void setSuffix(Player p, String suffix) {
        NametagsManager.setPlayerNametag(p, null, suffix, Integer.MIN_VALUE);
    }

    public static void setPrefix(Player p, String prefix) {
        NametagsManager.setPlayerNametag(p, prefix, null, Integer.MIN_VALUE);
    }

    public static void setPlayerNametag(Player player, String prefix, String suffix) {
        NametagsManager.setPlayerNametag(player, prefix, suffix, Integer.MIN_VALUE);
    }

    public static void setPlayerNametag(Player player, String prefix, String suffix, int priority) {
        Nametag tag;
        if (prefix != null) {
            prefix = prefix.substring(0, Math.min(16, prefix.length()));
        }
        if (suffix != null) {
            suffix = suffix.substring(0, Math.min(16, suffix.length()));
        }
        if ((tag = NametagsManager.getPlayerNameTag(player)) == null) {
            tag = new Nametag(player.getName(), prefix, suffix, priority, Nametag.Type.PLAYER);
            playerTags.put(player.getName().toLowerCase(), tag);
        } else {
            if (prefix != null) {
                tag.setPrefix(prefix);
            }
            if (suffix != null) {
                tag.setSuffix(suffix);
            }
            if (priority != Integer.MIN_VALUE) {
                tag.setPriority(priority);
            }
        }
        NametagsManager.applyNameTag(player, tag);
    }

    public static String cutName(String name) {
        if (name == null) {
            return name;
        }
        if (name.length() <= 16) {
            return name;
        }
        return name.substring(0, 16);
    }

    public static void setGroupTag(Player p, String group) {
        Nametag tag = NametagsManager.getGroupNametag(group);
        if (tag == null) {
            tag = new Nametag(group, null, null, Nametag.Type.GROUP);
        }
        NametagsManager.applyNameTag(p, tag);
    }

    public static boolean isGroupTagSet(String group) {
        return CommonLib.getMainScoreboard().getHandle().getEntryTeam(NametagsManager.getGroupEntry(group)) != null;
    }

    public static Nametag getNametag(Player p) {
        Nametag tag = playerTags.get(p.getName().toLowerCase());
        if (tag == null) {
            return groupTags.getOrDefault(UtilityMethods.getGroup(p).toLowerCase(), null);
        }
        return tag;
    }

    public static Nametag getPlayerNameTag(Player player) {
        return playerTags.get(player.getName().toLowerCase());
    }

    public static Nametag getGroupNametag(String group) {
        return groupTags.get(group.toLowerCase());
    }

    public static void resetPlayer(Player p) {
        VirtualTeam team = CommonLib.getMainScoreboard().getHandle().getEntryTeam(p.getName());
        if (team != null) {
            team.unregister();
        }
    }

    public static void resetGroup(String gr) {
        VirtualTeam team = CommonLib.getMainScoreboard().getHandle().getEntryTeam(NametagsManager.getGroupEntry(gr));
        if (team != null) {
            team.unregister();
        }
    }

    public static boolean isGroupTagTeam(VirtualTeam team) {
        if (!team.isForNameTags()) {
            return false;
        }
        return team.getEntries().stream().anyMatch(s -> s.startsWith(GROUP_ENTRY_PREFIX));
    }

    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)String.valueOf(s));
    }

    public static String getGroupEntry(String group) {
        String ent = GROUP_ENTRY_PREFIX + group.toLowerCase();
        return ent.substring(0, Math.min(16, ent.length()));
    }

    public static String getPlayerTeam(Player player, int prior) {
        String ent = prior > -1 ? alphabet[prior] + "-" + player.getUniqueId() : player.getUniqueId().toString();
        return ent.substring(0, Math.min(16, ent.length()));
    }

    public static String getGroupTeam(String group, int prior) {
        String ent = prior > -1 ? alphabet[prior] + "-" + group : GROUP_TEAM_PREFIX + group.toLowerCase();
        return ent.substring(0, Math.min(16, ent.length()));
    }

    public static boolean tagsReady(Player p) {
        return p.hasMetadata("ClibBoard");
    }
}

