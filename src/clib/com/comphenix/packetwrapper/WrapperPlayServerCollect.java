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

public class WrapperPlayServerCollect
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.COLLECT;

    public WrapperPlayServerCollect() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerCollect(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getCollectedEntityId() {
        return this.handle.getIntegers().read(0);
    }

    public void setCollectedEntityId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getCollectorEntityId() {
        return this.handle.getIntegers().read(1);
    }

    public void setCollectorEntityId(int value) {
        this.handle.getIntegers().write(1, value);
    }
}

