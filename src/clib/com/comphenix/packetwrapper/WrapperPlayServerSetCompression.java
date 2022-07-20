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

public class WrapperPlayServerSetCompression
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SET_COMPRESSION;

    public WrapperPlayServerSetCompression() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSetCompression(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getThreshold() {
        return this.handle.getIntegers().read(0);
    }

    public void setThreshold(int value) {
        this.handle.getIntegers().write(0, value);
    }
}

