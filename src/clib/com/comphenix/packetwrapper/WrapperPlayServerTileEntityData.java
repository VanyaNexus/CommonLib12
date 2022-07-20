/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 *  com.comphenix.protocol.wrappers.nbt.NbtBase
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtBase;

public class WrapperPlayServerTileEntityData
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TILE_ENTITY_DATA;

    public WrapperPlayServerTileEntityData() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerTileEntityData(PacketContainer packet) {
        super(packet, TYPE);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public int getAction() {
        return this.handle.getIntegers().read(0);
    }

    public void setAction(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public NbtBase<?> getNbtData() {
        return this.handle.getNbtModifier().read(0);
    }

    public void setNbtData(NbtBase<?> value) {
        this.handle.getNbtModifier().write(0, value);
    }
}

