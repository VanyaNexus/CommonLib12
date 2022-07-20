/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$Difficulty
 *  com.comphenix.protocol.wrappers.EnumWrappers$NativeGameMode
 *  org.bukkit.WorldType
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.WorldType;

public class WrapperPlayServerRespawn
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.RESPAWN;

    public WrapperPlayServerRespawn() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerRespawn(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getDimension() {
        return this.handle.getIntegers().read(0);
    }

    public void setDimension(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public EnumWrappers.Difficulty getDifficulty() {
        return this.handle.getDifficulties().read(0);
    }

    public void setDifficulty(EnumWrappers.Difficulty value) {
        this.handle.getDifficulties().write(0, value);
    }

    public EnumWrappers.NativeGameMode getGamemode() {
        return this.handle.getGameModes().read(0);
    }

    public void setGamemode(EnumWrappers.NativeGameMode value) {
        this.handle.getGameModes().write(0, value);
    }

    public WorldType getLevelType() {
        return this.handle.getWorldTypeModifier().read(0);
    }

    public void setLevelType(WorldType value) {
        this.handle.getWorldTypeModifier().write(0, value);
    }
}

