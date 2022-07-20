/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Status$Server
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusServerOutPing
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Server.PONG;

    public WrapperStatusServerOutPing() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperStatusServerOutPing(PacketContainer packet) {
        super(packet, TYPE);
    }

    public long getTime() {
        return this.handle.getLongs().read(0);
    }

    public void setTime(long value) {
        this.handle.getLongs().write(0, value);
    }
}

