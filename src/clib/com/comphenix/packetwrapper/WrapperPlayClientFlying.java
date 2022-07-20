/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientFlying
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.FLYING;

    public WrapperPlayClientFlying() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientFlying(PacketContainer packet) {
        super(packet, TYPE);
    }

    public boolean getOnGround() {
        return this.handle.getSpecificModifier(Boolean.TYPE).read(0);
    }

    public void setOnGround(boolean value) {
        this.handle.getSpecificModifier(Boolean.TYPE).write(0, value);
    }
}

