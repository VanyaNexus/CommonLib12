/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Status$Client
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusClientInPing
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Client.PING;

    public WrapperStatusClientInPing() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperStatusClientInPing(PacketContainer packet) {
        super(packet, TYPE);
    }

    public long getTime() {
        return this.handle.getLongs().read(0);
    }

    public void setTime(long value) {
        this.handle.getLongs().write(0, value);
    }
}

