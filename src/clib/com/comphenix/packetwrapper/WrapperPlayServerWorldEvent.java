/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerWorldEvent
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_EVENT;

    public WrapperPlayServerWorldEvent() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerWorldEvent(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEffectId() {
        return this.handle.getIntegers().read(0);
    }

    public void setEffectId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public int getData() {
        return this.handle.getIntegers().read(1);
    }

    public void setData(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public boolean getDisableRelativeVolume() {
        return this.handle.getSpecificModifier(Boolean.TYPE).read(0);
    }

    public void setDisableRelativeVolume(boolean value) {
        this.handle.getSpecificModifier(Boolean.TYPE).write(0, value);
    }
}

