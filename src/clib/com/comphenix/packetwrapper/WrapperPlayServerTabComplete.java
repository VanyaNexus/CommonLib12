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

public class WrapperPlayServerTabComplete
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TAB_COMPLETE;

    public WrapperPlayServerTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerTabComplete(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getCount() {
        return this.handle.getStringArrays().read(0).length;
    }

    public String[] getMatches() {
        return this.handle.getStringArrays().read(0);
    }

    public void setMatches(String[] value) {
        this.handle.getStringArrays().write(0, value);
    }
}

