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

public class WrapperPlayServerMapChunk
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP_CHUNK;

    public WrapperPlayServerMapChunk() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMapChunk(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getChunkX() {
        return this.handle.getIntegers().read(0);
    }

    public void setChunkX(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getChunkZ() {
        return this.handle.getIntegers().read(1);
    }

    public void setChunkZ(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public Object getChunkMap() {
        return this.handle.getModifier().read(2);
    }

    public void setChunkMap(Object value) {
        this.handle.getModifier().write(2, value);
    }

    public boolean getGroundUpContinuous() {
        return this.handle.getBooleans().read(0);
    }

    public void setGroundUpContinuous(boolean value) {
        this.handle.getBooleans().write(0, value);
    }
}

