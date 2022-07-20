/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.wrappers.WrappedAttribute
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class WrapperPlayServerUpdateAttributes
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_ATTRIBUTES;

    public WrapperPlayServerUpdateAttributes() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerUpdateAttributes(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEntityID() {
        return this.handle.getIntegers().read(0);
    }

    public void setEntityID(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public Entity getEntity(World world) {
        return this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }

    public List<WrappedAttribute> getAttributes() {
        return this.handle.getAttributeCollectionModifier().read(0);
    }

    public void setAttributes(List<WrappedAttribute> value) {
        this.handle.getAttributeCollectionModifier().write(0, value);
    }
}

