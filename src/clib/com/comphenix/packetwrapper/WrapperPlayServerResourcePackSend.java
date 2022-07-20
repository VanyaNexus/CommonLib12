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

public class WrapperPlayServerResourcePackSend
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.RESOURCE_PACK_SEND;

    public WrapperPlayServerResourcePackSend() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerResourcePackSend(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getUrl() {
        return this.handle.getStrings().read(0);
    }

    public void setUrl(String value) {
        this.handle.getStrings().write(0, value);
    }

    public String getHash() {
        return this.handle.getStrings().read(1);
    }

    public void setHash(String value) {
        this.handle.getStrings().write(1, value);
    }
}

