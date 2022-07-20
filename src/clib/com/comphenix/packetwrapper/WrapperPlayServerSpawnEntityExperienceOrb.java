/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerSpawnEntityExperienceOrb
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB;

    public WrapperPlayServerSpawnEntityExperienceOrb() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityExperienceOrb(PacketContainer packet) {
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

    public int getX() {
        return this.handle.getIntegers().read(1);
    }

    public void setX(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getY() {
        return this.handle.getIntegers().read(2);
    }

    public void setY(int value) {
        this.handle.getIntegers().write(2, value);
    }

    public int getZ() {
        return this.handle.getIntegers().read(3);
    }

    public void setZ(int value) {
        this.handle.getIntegers().write(3, value);
    }

    public int getCount() {
        return this.handle.getIntegers().read(4);
    }

    public void setCount(int value) {
        this.handle.getIntegers().write(4, value);
    }
}

