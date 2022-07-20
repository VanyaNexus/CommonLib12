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

public class WrapperPlayServerGameStateChange
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.GAME_STATE_CHANGE;

    public WrapperPlayServerGameStateChange() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerGameStateChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getReason() {
        return this.handle.getIntegers().read(0);
    }

    public void setReason(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public float getValue() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setValue(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }
}

