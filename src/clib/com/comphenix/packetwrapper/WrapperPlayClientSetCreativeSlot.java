/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  org.bukkit.inventory.ItemStack
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayClientSetCreativeSlot
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SET_CREATIVE_SLOT;

    public WrapperPlayClientSetCreativeSlot() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientSetCreativeSlot(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getSlot() {
        return this.handle.getIntegers().read(0);
    }

    public void setSlot(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public ItemStack getClickedItem() {
        return this.handle.getItemModifier().read(0);
    }

    public void setClickedItem(ItemStack value) {
        this.handle.getItemModifier().write(0, value);
    }
}

