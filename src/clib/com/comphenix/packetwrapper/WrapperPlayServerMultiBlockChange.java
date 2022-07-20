/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.ChunkCoordIntPair
 *  com.comphenix.protocol.wrappers.MultiBlockChangeInfo
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;

public class WrapperPlayServerMultiBlockChange
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MULTI_BLOCK_CHANGE;

    public WrapperPlayServerMultiBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMultiBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    public ChunkCoordIntPair getChunk() {
        return this.handle.getChunkCoordIntPairs().read(0);
    }

    public void setChunk(ChunkCoordIntPair value) {
        this.handle.getChunkCoordIntPairs().write(0, value);
    }

    public MultiBlockChangeInfo[] getRecords() {
        return this.handle.getMultiBlockChangeInfoArrays().read(0);
    }

    public void setRecords(MultiBlockChangeInfo[] value) {
        this.handle.getMultiBlockChangeInfoArrays().write(0, value);
    }
}

