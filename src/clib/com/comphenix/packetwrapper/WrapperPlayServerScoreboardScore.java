/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$ScoreboardAction
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayServerScoreboardScore
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_SCORE;

    public WrapperPlayServerScoreboardScore() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardScore(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getScoreName() {
        return this.handle.getStrings().read(0);
    }

    public void setScoreName(String value) {
        this.handle.getStrings().write(0, value);
    }

    public String getObjectiveName() {
        return this.handle.getStrings().read(1);
    }

    public void setObjectiveName(String value) {
        this.handle.getStrings().write(1, value);
    }

    public int getValue() {
        return this.handle.getIntegers().read(0);
    }

    public void setValue(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public EnumWrappers.ScoreboardAction getAction() {
        return this.handle.getScoreboardActions().read(0);
    }

    public void setScoreboardAction(EnumWrappers.ScoreboardAction value) {
        this.handle.getScoreboardActions().write(0, value);
    }
}

