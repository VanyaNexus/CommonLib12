/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.google.common.base.Objects
 *  org.bukkit.entity.Player
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.utility.UtilityMethods;

public abstract class AbstractPacket {
    protected PacketContainer handle;

    protected AbstractPacket(PacketContainer handle, PacketType type) {
        if (handle == null) {
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        }
        if (!Objects.equal(handle.getType(), type)) {
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        }
        this.handle = handle;
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Player receiver) {
        UtilityMethods.sendPacket(receiver, this.getHandle());
    }

    public void broadcastPacket() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(this.getHandle());
    }

    @Deprecated
    public void recievePacket(Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot recieve packet.", e);
        }
    }

    public void receivePacket(Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot recieve packet.", e);
        }
    }
}

