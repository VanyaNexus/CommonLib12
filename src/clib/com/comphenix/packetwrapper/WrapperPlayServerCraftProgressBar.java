/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerCraftProgressBar
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CRAFT_PROGRESS_BAR;

    public WrapperPlayServerCraftProgressBar() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerCraftProgressBar(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getProperty() {
        return this.handle.getIntegers().read(1);
    }

    public void setProperty(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getValue() {
        return this.handle.getIntegers().read(2);
    }

    public void setValue(int value) {
        this.handle.getIntegers().write(2, value);
    }
}

