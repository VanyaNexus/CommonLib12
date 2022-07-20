/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerPlayerListHeaderFooter
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER;

    public WrapperPlayServerPlayerListHeaderFooter() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerPlayerListHeaderFooter(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrappedChatComponent getHeader() {
        return this.handle.getChatComponents().read(0);
    }

    public void setHeader(WrappedChatComponent value) {
        this.handle.getChatComponents().write(0, value);
    }

    public WrappedChatComponent getFooter() {
        return this.handle.getChatComponents().read(1);
    }

    public void setFooter(WrappedChatComponent value) {
        this.handle.getChatComponents().write(1, value);
    }
}

