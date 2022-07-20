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

public class WrapperPlayServerUpdateTime
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_TIME;

    public WrapperPlayServerUpdateTime() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerUpdateTime(PacketContainer packet) {
        super(packet, TYPE);
    }

    public long getAgeOfTheWorld() {
        return this.handle.getLongs().read(0);
    }

    public void setAgeOfTheWorld(long value) {
        this.handle.getLongs().write(0, value);
    }

    public long getTimeOfDay() {
        return this.handle.getLongs().read(1);
    }

    public void setTimeOfDay(long value) {
        this.handle.getLongs().write(1, value);
    }
}

