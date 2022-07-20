//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public interface Position extends Cloneable, Comparable<Position> {
    double distanceTo(Position var1);

    int getBlockX();

    int getBlockY();

    int getBlockZ();

    double getX();

    double getY();

    double getZ();

    void setX(double var1);

    void setY(double var1);

    void setZ(double var1);

    void setWorld(String var1);

    default void setWorld(World world) {
        this.setWorld(world.getName());
    }

    default World getWorld() {
        return Bukkit.getWorld(this.getWorldName());
    }

    String getWorldName();

    Location toLocation();

    Block toBlock();

    default Vector toVector() {
        return new Vector(this.getX(), this.getY(), this.getZ());
    }

    default double[] toDoubleArray() {
        return new double[]{this.getX(), this.getY(), this.getZ()};
    }

    default int[] toIntArray() {
        return new int[]{this.getBlockX(), this.getBlockY(), this.getBlockZ()};
    }

    static int locToBlock(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
    }

    default int compareTo(Position other) {
        Preconditions.checkNotNull(other);
        if (this.getY() != other.getY()) {
            return Double.compare(this.getY(), other.getY());
        } else if (this.getZ() != other.getZ()) {
            return Double.compare(this.getZ(), other.getZ());
        } else {
            return this.getX() != other.getX() ? Double.compare(this.getX(), other.getX()) : 0;
        }
    }
}
