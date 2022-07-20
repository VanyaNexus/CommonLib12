/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Login$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.WrappedGameProfile
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class WrapperLoginClientStart
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Client.START;

    public WrapperLoginClientStart() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperLoginClientStart(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrappedGameProfile getProfile() {
        return this.handle.getGameProfiles().read(0);
    }

    public void setProfile(WrappedGameProfile value) {
        this.handle.getGameProfiles().write(0, value);
    }
}

