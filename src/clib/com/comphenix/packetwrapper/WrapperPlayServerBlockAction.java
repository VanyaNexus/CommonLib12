/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 *  org.bukkit.Material
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Material;

public class WrapperPlayServerBlockAction
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_ACTION;

    public WrapperPlayServerBlockAction() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerBlockAction(PacketContainer packet) {
        super(packet, TYPE);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public int getByte1() {
        return this.handle.getIntegers().read(0);
    }

    public void setByte1(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getByte2() {
        return this.handle.getIntegers().read(1);
    }

    public void setByte2(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public Material getBlockType() {
        return this.handle.getBlocks().read(0);
    }

    public void setBlockType(Material value) {
        this.handle.getBlocks().write(0, value);
    }
}

