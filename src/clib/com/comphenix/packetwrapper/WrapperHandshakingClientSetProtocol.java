/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Handshake$Client
 *  com.comphenix.protocol.PacketType$Protocol
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperHandshakingClientSetProtocol
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Handshake.Client.SET_PROTOCOL;

    public WrapperHandshakingClientSetProtocol() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperHandshakingClientSetProtocol(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getProtocolVersion() {
        return this.handle.getIntegers().read(0);
    }

    public void setProtocolVersion(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public String getServerAddressHostnameOrIp() {
        return this.handle.getStrings().read(0);
    }

    public void setServerAddressHostnameOrIp(String value) {
        this.handle.getStrings().write(0, value);
    }

    public int getServerPort() {
        return this.handle.getIntegers().read(1);
    }

    public void setServerPort(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public PacketType.Protocol getNextState() {
        return this.handle.getProtocols().read(0);
    }

    public void setNextState(PacketType.Protocol value) {
        this.handle.getProtocols().write(0, value);
    }
}

