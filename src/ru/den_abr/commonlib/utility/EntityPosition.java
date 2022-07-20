//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class EntityPosition implements Position {
    private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public EntityPosition(String world, double x, double y, double z) {
        this(world, x, y, z, 0.0F, 0.0F);
    }

    public EntityPosition(String world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public EntityPosition(Location l) {
        this(l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
    }

    public EntityPosition(Entity e) {
        this(e.getLocation());
    }

    public Location toLocation() {
        return new Location(this.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Block toBlock() {
        return this.getBlock();
    }

    public float getYaw() {
        return this.yaw;
    }

    /** @deprecated */
    @Deprecated
    public Block getBlock() {
        return this.toLocation().getBlock();
    }

    public String getWorldName() {
        return this.world;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double distanceTo(Position pos) {
        return Math.sqrt(Math.pow(this.x - pos.getX(), 2.0D) + Math.pow(this.y - pos.getY(), 2.0D) + Math.pow(this.z - pos.getZ(), 2.0D));
    }

    public int getBlockX() {
        return Position.locToBlock(this.x);
    }

    public int getBlockY() {
        return Position.locToBlock(this.y);
    }

    public int getBlockZ() {
        return Position.locToBlock(this.z);
    }

    public void setWorld(String world) {
        Preconditions.checkNotNull(world);
        this.world = world;
    }

    public void setWorld(World world) {
        Preconditions.checkNotNull(world);
        this.world = world.getName();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String toString() {
        return "EntityPosition{" + this.world + ", " + this.x + ", " + this.y + ", " + this.z + ", " + this.pitch + ", " + this.yaw + "}";
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.world, this.x, this.y, this.z, this.pitch, this.yaw});
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            EntityPosition bp = (EntityPosition)obj;
            return this.world.equals(bp.world) && bp.x == this.x && bp.y == this.y && bp.z == this.z && bp.pitch == this.pitch && bp.yaw == this.yaw;
        }
    }
}
