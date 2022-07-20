/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.injector.PacketConstructor
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class WrapperPlayServerSpawnEntityLiving
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
    private static PacketConstructor entityConstructor;

    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(WrapperPlayServerSpawnEntityLiving.fromEntity(entity), TYPE);
    }

    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null) {
            entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, entity);
        }
        return entityConstructor.createPacket(entity);
    }

    public int getEntityID() {
        return this.handle.getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }

    public void setEntityID(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public EntityType getType() {
        return EntityType.fromId(this.handle.getIntegers().read(1));
    }

    public void setType(EntityType value) {
        this.handle.getIntegers().write(1, Integer.valueOf(value.getTypeId()));
    }

    public double getX() {
        return (double) this.handle.getIntegers().read(2).intValue() / 32.0;
    }

    public void setX(double value) {
        this.handle.getIntegers().write(2, (int)Math.floor(value * 32.0));
    }

    public double getY() {
        return (double) this.handle.getIntegers().read(3).intValue() / 32.0;
    }

    public void setY(double value) {
        this.handle.getIntegers().write(3, (int)Math.floor(value * 32.0));
    }

    public double getZ() {
        return (double) this.handle.getIntegers().read(4).intValue() / 32.0;
    }

    public void setZ(double value) {
        this.handle.getIntegers().write(4, (int)Math.floor(value * 32.0));
    }

    public float getYaw() {
        return (float) this.handle.getBytes().read(0).byteValue() * 360.0f / 256.0f;
    }

    public void setYaw(float value) {
        this.handle.getBytes().write(0, (byte)(value * 256.0f / 360.0f));
    }

    public float getHeadPitch() {
        return (float) this.handle.getBytes().read(1).byteValue() * 360.0f / 256.0f;
    }

    public void setHeadPitch(float value) {
        this.handle.getBytes().write(1, (byte)(value * 256.0f / 360.0f));
    }

    public float getHeadYaw() {
        return (float) this.handle.getBytes().read(2).byteValue() * 360.0f / 256.0f;
    }

    public void setHeadYaw(float value) {
        this.handle.getBytes().write(2, (byte)(value * 256.0f / 360.0f));
    }

    public double getVelocityX() {
        return (double) this.handle.getIntegers().read(5).intValue() / 8000.0;
    }

    public void setVelocityX(double value) {
        this.handle.getIntegers().write(5, (int)(value * 8000.0));
    }

    public double getVelocityY() {
        return (double) this.handle.getIntegers().read(6).intValue() / 8000.0;
    }

    public void setVelocityY(double value) {
        this.handle.getIntegers().write(6, (int)(value * 8000.0));
    }

    public double getVelocityZ() {
        return (double) this.handle.getIntegers().read(7).intValue() / 8000.0;
    }

    public void setVelocityZ(double value) {
        this.handle.getIntegers().write(7, (int)(value * 8000.0));
    }

    public WrappedDataWatcher getMetadata() {
        return this.handle.getDataWatcherModifier().read(0);
    }

    public void setMetadata(WrappedDataWatcher value) {
        this.handle.getDataWatcherModifier().write(0, value);
    }
}

