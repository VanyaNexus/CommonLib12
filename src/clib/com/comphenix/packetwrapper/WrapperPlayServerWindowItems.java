/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  org.bukkit.inventory.ItemStack
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayServerWindowItems
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WINDOW_ITEMS;

    public WrapperPlayServerWindowItems() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerWindowItems(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public ItemStack[] getSlotData() {
        return this.handle.getItemArrayModifier().read(0);
    }

    public void setSlotData(ItemStack[] value) {
        this.handle.getItemArrayModifier().write(0, value);
    }
}

