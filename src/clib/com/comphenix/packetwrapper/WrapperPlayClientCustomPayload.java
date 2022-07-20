/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WrapperPlayClientCustomPayload
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CUSTOM_PAYLOAD;

    public WrapperPlayClientCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getChannel() {
        return this.handle.getStrings().read(0);
    }

    public void setChannel(String value) {
        this.handle.getStrings().write(0, value);
    }

    public ByteBuf getContentsBuffer() {
        return (ByteBuf)this.handle.getModifier().withType(ByteBuf.class).read(0);
    }

    public byte[] getContents() {
        return this.getContentsBuffer().array();
    }

    public void setContentsBuffer(ByteBuf contents) {
        this.handle.getModifier().withType(ByteBuf.class).write(0, contents);
    }

    public void setContents(byte[] content) {
        this.setContentsBuffer(Unpooled.copiedBuffer(content));
    }
}

