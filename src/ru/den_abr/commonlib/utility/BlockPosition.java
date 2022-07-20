package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class BlockPosition implements Position
{
    private String world;
    private int x;
    private int y;
    private int z;
    
    public BlockPosition(final String world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockPosition(final Location l) {
        this(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    public BlockPosition(final Block block) {
        this(block.getLocation());
    }
    
    public BlockPosition(final Entity entity) {
        this(entity.getLocation());
    }
    
    public BlockPosition(final EntityPosition pos) {
        this(pos.getWorldName(), (int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
    }
    
    @Deprecated
    public Block getBlock() {
        return this.toLocation().getBlock();
    }
    
    @Override
    public Location toLocation() {
        return new Location(this.getWorld(), (double)this.x, (double)this.y, (double)this.z);
    }
    
    @Override
    public String getWorldName() {
        return this.world;
    }
    
    @Override
    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }
    
    @Override
    public void setWorld(final String world) {
        Preconditions.checkNotNull((Object)world);
        this.world = world;
    }
    
    @Override
    public void setWorld(final World world) {
        Preconditions.checkNotNull((Object)world);
        this.world = world.getName();
    }
    
    @Override
    public void setX(final double x) {
        this.x = Position.locToBlock(x);
    }
    
    @Override
    public void setY(final double y) {
        this.y = Position.locToBlock(y);
    }
    
    @Override
    public void setZ(final double z) {
        this.z = Position.locToBlock(z);
    }
    
    @Override
    public double distanceTo(final Position pos) {
        return Math.sqrt(Math.pow(this.x - pos.getX(), 2.0) + Math.pow(this.y - pos.getY(), 2.0) + Math.pow(this.z - pos.getZ(), 2.0));
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
    
    @Override
    public int getBlockX() {
        return this.x;
    }
    
    @Override
    public int getBlockY() {
        return this.y;
    }
    
    @Override
    public int getBlockZ() {
        return this.z;
    }
    
    @Override
    public Block toBlock() {
        return this.getBlock();
    }
    
    public BlockPosition clone() {
        return new BlockPosition(this.world, this.x, this.y, this.z);
    }
    
    @Override
    public String toString() {
        return "BlockPosition{" + this.world + ", " + this.x + ", " + this.y + ", " + this.z + "}";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final BlockPosition bp = (BlockPosition)obj;
        return this.world.equals(bp.world) && bp.x == this.x && bp.y == this.y && bp.z == this.z;
    }
}
