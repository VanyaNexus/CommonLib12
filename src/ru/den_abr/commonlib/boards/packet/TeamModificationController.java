/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketAdapter$AdapterParameteters
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.injector.GamePhase
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.boards.packet;

import clib.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;

import java.util.List;

public class TeamModificationController
extends PacketAdapter {
    TeamModificationController(PacketAdapter.AdapterParameteters params) {
        super(params);
    }

    public void onPacketSending(PacketEvent event) {
        VirtualTeam entryTeam;
        PacketContainer pc = event.getPacket();
        VirtualScoreboard board = CommonLib.getMainScoreboard().getHandle();
        if (!board.getWatching().contains((Object)event.getPlayer())) {
            return;
        }
        WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam(pc);
        String teamName = teamPacket.getName();
        VirtualTeam existingTeam = board.getTeam(teamName);
        VirtualTeam.TeamPacketType type = VirtualTeam.TeamPacketType.fromId(teamPacket.getMode());
        if (type == VirtualTeam.TeamPacketType.CREATE) {
            VirtualTeam existTeam = board.getTeam(teamName);
            if (existTeam == null) {
                for (String entry : teamPacket.getPlayers()) {
                    VirtualTeam entryTeam2 = board.getEntryTeam(entry);
                    if (entryTeam2 == null) continue;
                    event.setCancelled(true);
                    if (!CommonLib.CONF.isTraceNametagConflicts()) continue;
                    throw new RuntimeException("Someone trying to modify current teams by creating new team. " + entry + " -> " + teamName + " (should be " + entryTeam2.getName() + ")");
                }
            } else if (!teamPacket.getPlayers().containsAll(existingTeam.getEntries())) {
                System.out.println("Someone sent modified packet of " + teamName + " with changed players. Rolling back changes...");
                teamPacket.setPlayers((List<String>)ImmutableList.copyOf(existingTeam.getEntries()));
                event.setPacket(pc);
            }
        }
        if (type == VirtualTeam.TeamPacketType.ADD_PLAYERS) {
            for (String entry : teamPacket.getPlayers()) {
                entryTeam = board.getEntryTeam(entry);
                if (entryTeam == null || entryTeam.getName().equals(teamName)) continue;
                event.setCancelled(true);
                if (!CommonLib.CONF.isTraceNametagConflicts()) continue;
                throw new RuntimeException("Someone trying to modify current teams by adding player to another team. " + entry + " -> " + teamName + " (should be " + entryTeam.getName() + ")");
            }
        }
        if (type == VirtualTeam.TeamPacketType.REMOVE_PLAYERS) {
            for (String entry : teamPacket.getPlayers()) {
                entryTeam = board.getEntryTeam(entry);
                if (entryTeam == null) continue;
                event.setCancelled(true);
                if (!CommonLib.CONF.isTraceNametagConflicts()) continue;
                throw new RuntimeException("Someone trying to modify current teams by removing " + entry + " from VirtualTeam. Found in " + entryTeam + ", tried to remove from " + (existingTeam != null ? existingTeam : teamName));
            }
        }
        if (type == VirtualTeam.TeamPacketType.DELETE && board.getTeam(teamName) != null) {
            event.setCancelled(true);
            if (CommonLib.CONF.isTraceNametagConflicts()) {
                throw new RuntimeException("Someone trying to remove VirtualTeam " + teamName);
            }
        }
    }

    public static TeamModificationController get(CommonLib pl) {
        PacketAdapter.AdapterParameteters params = new PacketAdapter.AdapterParameteters();
        params.plugin((Plugin)pl);
        params.listenerPriority(ListenerPriority.HIGHEST);
        params.gamePhase(GamePhase.PLAYING);
        params.types(new PacketType[]{PacketType.Play.Server.SCOREBOARD_TEAM});
        return new TeamModificationController(params);
    }
}

