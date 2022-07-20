/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$Difficulty
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayServerServerDifficulty
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SERVER_DIFFICULTY;

    public WrapperPlayServerServerDifficulty() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerServerDifficulty(PacketContainer packet) {
        super(packet, TYPE);
    }

    public EnumWrappers.Difficulty getDifficulty() {
        return this.handle.getDifficulties().read(0);
    }

    public void setDifficulty(EnumWrappers.Difficulty value) {
        this.handle.getDifficulties().write(0, value);
    }
}

