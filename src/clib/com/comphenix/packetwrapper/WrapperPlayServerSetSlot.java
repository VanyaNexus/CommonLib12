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

public class WrapperPlayServerSetSlot
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SET_SLOT;

    public WrapperPlayServerSetSlot() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSetSlot(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getSlot() {
        return this.handle.getIntegers().read(1);
    }

    public void setSlot(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public ItemStack getSlotData() {
        return this.handle.getItemModifier().read(0);
    }

    public void setSlotData(ItemStack value) {
        this.handle.getItemModifier().write(0, value);
    }
}

