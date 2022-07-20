/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$ResourcePackStatus
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayClientResourcePackStatus
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.RESOURCE_PACK_STATUS;

    public WrapperPlayClientResourcePackStatus() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientResourcePackStatus(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getHash() {
        return this.handle.getStrings().read(0);
    }

    public void setHash(String value) {
        this.handle.getStrings().write(0, value);
    }

    public EnumWrappers.ResourcePackStatus getResult() {
        return this.handle.getResourcePackStatus().read(0);
    }

    public void setResult(EnumWrappers.ResourcePackStatus value) {
        this.handle.getResourcePackStatus().write(0, value);
    }
}

