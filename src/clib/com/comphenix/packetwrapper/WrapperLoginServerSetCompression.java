/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Login$Server
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginServerSetCompression
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.SET_COMPRESSION;

    public WrapperLoginServerSetCompression() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperLoginServerSetCompression(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getThreshold() {
        return this.handle.getIntegers().read(0);
    }

    public void setThreshold(int value) {
        this.handle.getIntegers().write(0, value);
    }
}

