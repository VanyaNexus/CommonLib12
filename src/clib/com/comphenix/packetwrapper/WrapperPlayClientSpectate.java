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

import java.util.UUID;

public class WrapperPlayClientSpectate
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SPECTATE;

    public WrapperPlayClientSpectate() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientSpectate(PacketContainer packet) {
        super(packet, TYPE);
    }

    public UUID getTargetPlayer() {
        return this.handle.getSpecificModifier(UUID.class).read(0);
    }

    public void setTargetPlayer(UUID value) {
        this.handle.getSpecificModifier(UUID.class).write(0, value);
    }
}

