//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList.Builder;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Area {
    private BlockPosition pos1;
    private BlockPosition pos2;

    public Area(BlockPosition pos1, BlockPosition pos2) {
        Preconditions.checkArgument(Objects.equals(pos1.getWorldName(), pos2.getWorldName()), "Points of Area must be in the same world");
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Area(Location pos1, Location pos2) {
        this(new BlockPosition(pos1), new BlockPosition(pos2));
    }

    public BlockPosition getPos1() {
        return this.pos1;
    }

    public BlockPosition getPos2() {
        return this.pos2;
    }

    public BlockPosition getMaxPoint() {
        int maxX = Math.max(this.pos1.getBlockX(), this.pos2.getBlockX());
        int maxY = Math.max(this.pos1.getBlockY(), this.pos2.getBlockY());
        int maxZ = Math.max(this.pos1.getBlockZ(), this.pos2.getBlockZ());
        return new BlockPosition(this.pos1.getWorldName(), maxX, maxY, maxZ);
    }

    public BlockPosition getMinPoint() {
        int minX = Math.min(this.pos1.getBlockX(), this.pos2.getBlockX());
        int minY = Math.min(this.pos1.getBlockY(), this.pos2.getBlockY());
        int minZ = Math.min(this.pos1.getBlockZ(), this.pos2.getBlockZ());
        return new BlockPosition(this.pos1.getWorldName(), minX, minY, minZ);
    }

    public boolean isInside(Entity e) {
        return this.isInside(e.getLocation());
    }

    public boolean isInside(BlockPosition pos) {
        return this.isInside(pos.toLocation());
    }

    public boolean isInside(EntityPosition pos) {
        return this.isInside(pos.toLocation());
    }

    public boolean isInside(Location l) {
        BlockPosition max = this.getMaxPoint();
        BlockPosition min = this.getMinPoint();
        return l.getWorld().getName().equals(max.getWorldName()) && (double)l.getBlockX() >= min.getX() && (double)l.getBlockX() <= max.getX() && (double)l.getBlockY() >= min.getY() && (double)l.getBlockY() <= max.getY() && (double)l.getBlockZ() >= min.getZ() && (double)l.getBlockZ() <= max.getZ();
    }

    public List<Player> getPlayersInside() {
        Preconditions.checkNotNull(this.pos1);
        Preconditions.checkNotNull(this.pos2);
        World world = this.pos1.getWorld();
        return (List)world.getPlayers().stream().filter(this::isInside).collect(Collectors.toList());
    }

    public List<Entity> getAllEntitiesInside() {
        Preconditions.checkNotNull(this.pos1);
        Preconditions.checkNotNull(this.pos2);
        World world = this.pos1.getWorld();
        return (List)world.getEntities().stream().filter(this::isInside).collect(Collectors.toList());
    }

    public boolean isInside(Block block) {
        return this.isInside(block.getLocation());
    }

    public BlockPosition getCenter() {
        BlockPosition max = this.getMaxPoint();
        BlockPosition min = this.getMinPoint();
        int x1 = max.getBlockX() + 1;
        int y1 = max.getBlockY() + 1;
        int z1 = max.getBlockZ() + 1;
        int x2 = min.getBlockX() + 1;
        int y2 = min.getBlockY() + 1;
        int z2 = min.getBlockZ() + 1;
        return new BlockPosition(max.getWorldName(), Position.locToBlock((double)x2 + (double)(x1 - x2) / 2.0D), Position.locToBlock((double)y2 + (double)(y1 - y2) / 2.0D), Position.locToBlock((double)z2 + (double)(z1 - z2) / 2.0D));
    }

    public List<BlockPosition> getBlocks() {
        Builder<BlockPosition> blocks = new Builder();
        BlockPosition maxPos = this.getMaxPoint();
        BlockPosition minPos = this.getMinPoint();

        for(int y = minPos.getBlockY(); y < maxPos.getBlockY() + 1; ++y) {
            for(int x = minPos.getBlockX(); x < maxPos.getBlockX() + 1; ++x) {
                for(int z = minPos.getBlockZ(); z < maxPos.getBlockZ() + 1; ++z) {
                    blocks.add(new BlockPosition(minPos.getWorldName(), x, y, z));
                }
            }
        }

        return blocks.build();
    }

    public String toString() {
        return "{ " + this.pos1 + " - " + this.pos2 + " }";
    }

    public static Area fromSelection(Selection sel) {
        Preconditions.checkArgument(sel instanceof CuboidSelection, "Selection must be a CuboidSelection");
        return new Area(new BlockPosition(sel.getMaximumPoint()), new BlockPosition(sel.getMinimumPoint()));
    }

    public static Area fromVectorsWE(World world, Vector minPos, Vector maxPos) {
        return new Area(BukkitUtil.toLocation(world, minPos), BukkitUtil.toLocation(world, maxPos));
    }
}
