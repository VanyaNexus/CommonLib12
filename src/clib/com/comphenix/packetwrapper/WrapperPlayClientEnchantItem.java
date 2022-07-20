/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientEnchantItem
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ENCHANT_ITEM;

    public WrapperPlayClientEnchantItem() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientEnchantItem(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(byte value) {
        this.handle.getIntegers().write(0, Integer.valueOf(value));
    }

    public int getEnchantment() {
        return this.handle.getIntegers().read(1);
    }

    public void setEnchantment(int value) {
        this.handle.getIntegers().write(1, value);
    }
}

