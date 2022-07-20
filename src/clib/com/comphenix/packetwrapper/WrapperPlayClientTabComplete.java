/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayClientTabComplete
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.TAB_COMPLETE;

    public WrapperPlayClientTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientTabComplete(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getText() {
        return this.handle.getStrings().read(0);
    }

    public void setText(String value) {
        this.handle.getStrings().write(0, value);
    }

    public BlockPosition getHasPosition() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setHasPosition(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public BlockPosition getLookedAtBlock() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLookedAtBlock(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }
}

