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

public class WrapperPlayServerMapChunkBulk
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP_CHUNK_BULK;

    public WrapperPlayServerMapChunkBulk() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMapChunkBulk(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int[] getChunksX() {
        return this.handle.getIntegerArrays().read(0);
    }

    public void setChunksX(int[] value) {
        this.handle.getIntegerArrays().write(0, value);
    }

    public int[] getChunksZ() {
        return this.handle.getIntegerArrays().read(1);
    }

    public void setChunksZ(int[] value) {
        this.handle.getIntegerArrays().write(1, value);
    }

    public Object[] getChunks() {
        return (Object[])this.handle.getModifier().read(2);
    }

    public void setChunks(Object[] value) {
        this.handle.getModifier().write(2, value);
    }

    public boolean isSkyLightSent() {
        return this.handle.getBooleans().read(0);
    }

    public void setSkyLightSent(boolean value) {
        this.handle.getBooleans().write(0, value);
    }
}

