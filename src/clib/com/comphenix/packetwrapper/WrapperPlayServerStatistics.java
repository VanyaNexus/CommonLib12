/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.WrappedStatistic
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedStatistic;

import java.util.Map;

public class WrapperPlayServerStatistics
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.STATISTICS;

    public WrapperPlayServerStatistics() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerStatistics(PacketContainer packet) {
        super(packet, TYPE);
    }

    public Map<WrappedStatistic, Integer> getStatistics() {
        return (Map)this.handle.getSpecificModifier(Map.class).read(0);
    }

    public void setStatistics(Map<WrappedStatistic, Integer> value) {
        this.handle.getSpecificModifier(Map.class).write(0, value);
    }
}

