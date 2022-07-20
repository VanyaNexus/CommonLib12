/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerScoreboardDisplayObjective
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;

    public WrapperPlayServerScoreboardDisplayObjective() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardDisplayObjective(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getPosition() {
        return this.handle.getIntegers().read(0);
    }

    public void setPosition(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public String getScoreName() {
        return this.handle.getStrings().read(0);
    }

    public void setScoreName(String value) {
        this.handle.getStrings().write(0, value);
    }
}

