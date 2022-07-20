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

public class WrapperPlayServerChat
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerChat(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrappedChatComponent getMessage() {
        return this.handle.getChatComponents().read(0);
    }

    @Deprecated
    public WrappedChatComponent getJsonData() {
        return this.getMessage();
    }

    public void setMessage(WrappedChatComponent value) {
        this.handle.getChatComponents().write(0, value);
    }

    @Deprecated
    public void setJsonData(WrappedChatComponent value) {
        this.setMessage(value);
    }

    public byte getPosition() {
        return this.handle.getBytes().read(0);
    }

    public void setPosition(byte value) {
        this.handle.getBytes().write(0, value);
    }
}

