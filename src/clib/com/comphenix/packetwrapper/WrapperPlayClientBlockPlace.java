/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 *  org.bukkit.inventory.ItemStack
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayClientBlockPlace
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.BLOCK_PLACE;

    public WrapperPlayClientBlockPlace() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientBlockPlace(PacketContainer packet) {
        super(packet, TYPE);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public int getFace() {
        return this.handle.getIntegers().read(0);
    }

    public void setFace(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public ItemStack getHeldItem() {
        return this.handle.getItemModifier().read(0);
    }

    public void setHeldItem(ItemStack value) {
        this.handle.getItemModifier().write(0, value);
    }

    public float getCursorPositionX() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setCursorPositionX(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public float getCursorPositionY() {
        return this.handle.getFloat().read(1).floatValue();
    }

    public void setCursorPositionY(float value) {
        this.handle.getFloat().write(1, Float.valueOf(value));
    }

    public float getCursorPositionZ() {
        return this.handle.getFloat().read(2).floatValue();
    }

    public void setCursorPositionZ(float value) {
        this.handle.getFloat().write(2, Float.valueOf(value));
    }
}

