package ru.den_abr.commonlib.utility;

public class Vector2D
{
    public static final Vector2D ZERO;
    public static final Vector2D UNIT_X;
    public static final Vector2D UNIT_Z;
    public static final Vector2D ONE;
    protected final double x;
    protected final double z;
    
    public Vector2D(final double x, final double z) {
        this.x = x;
        this.z = z;
    }
    
    public Vector2D(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
    
    public Vector2D(final float x, final float z) {
        this.x = x;
        this.z = z;
    }
    
    public Vector2D(final Vector2D other) {
        this.x = other.x;
        this.z = other.z;
    }
    
    public Vector2D() {
        this.x = 0.0;
        this.z = 0.0;
    }
    
    public double getX() {
        return this.x;
    }
    
    public int getBlockX() {
        return (int)Math.round(this.x);
    }
    
    public Vector2D setX(final double x) {
        return new Vector2D(x, this.z);
    }
    
    public Vector2D setX(final int x) {
        return new Vector2D(x, this.z);
    }
    
    public double getZ() {
        return this.z;
    }
    
    public int getBlockZ() {
        return (int)Math.round(this.z);
    }
    
    public Vector2D setZ(final double z) {
        return new Vector2D(this.x, z);
    }
    
    public Vector2D setZ(final int z) {
        return new Vector2D(this.x, z);
    }
    
    public Vector2D add(final Vector2D other) {
        return new Vector2D(this.x + other.x, this.z + other.z);
    }
    
    public Vector2D add(final double x, final double z) {
        return new Vector2D(this.x + x, this.z + z);
    }
    
    public Vector2D add(final int x, final int z) {
        return new Vector2D(this.x + x, this.z + z);
    }
    
    public Vector2D add(final Vector2D... others) {
        double newX = this.x;
        double newZ = this.z;
        for (final Vector2D other : others) {
            newX += other.x;
            newZ += other.z;
        }
        return new Vector2D(newX, newZ);
    }
    
    public Vector2D subtract(final Vector2D other) {
        return new Vector2D(this.x - other.x, this.z - other.z);
    }
    
    public Vector2D subtract(final double x, final double z) {
        return new Vector2D(this.x - x, this.z - z);
    }
    
    public Vector2D subtract(final int x, final int z) {
        return new Vector2D(this.x - x, this.z - z);
    }
    
    public Vector2D subtract(final Vector2D... others) {
        double newX = this.x;
        double newZ = this.z;
        for (final Vector2D other : others) {
            newX -= other.x;
            newZ -= other.z;
        }
        return new Vector2D(newX, newZ);
    }
    
    public Vector2D multiply(final Vector2D other) {
        return new Vector2D(this.x * other.x, this.z * other.z);
    }
    
    public Vector2D multiply(final double x, final double z) {
        return new Vector2D(this.x * x, this.z * z);
    }
    
    public Vector2D multiply(final int x, final int z) {
        return new Vector2D(this.x * x, this.z * z);
    }
    
    public Vector2D multiply(final Vector2D... others) {
        double newX = this.x;
        double newZ = this.z;
        for (final Vector2D other : others) {
            newX *= other.x;
            newZ *= other.z;
        }
        return new Vector2D(newX, newZ);
    }
    
    public Vector2D multiply(final double n) {
        return new Vector2D(this.x * n, this.z * n);
    }
    
    public Vector2D multiply(final float n) {
        return new Vector2D(this.x * n, this.z * n);
    }
    
    public Vector2D multiply(final int n) {
        return new Vector2D(this.x * n, this.z * n);
    }
    
    public Vector2D divide(final Vector2D other) {
        return new Vector2D(this.x / other.x, this.z / other.z);
    }
    
    public Vector2D divide(final double x, final double z) {
        return new Vector2D(this.x / x, this.z / z);
    }
    
    public Vector2D divide(final int x, final int z) {
        return new Vector2D(this.x / x, this.z / z);
    }
    
    public Vector2D divide(final int n) {
        return new Vector2D(this.x / n, this.z / n);
    }
    
    public Vector2D divide(final double n) {
        return new Vector2D(this.x / n, this.z / n);
    }
    
    public Vector2D divide(final float n) {
        return new Vector2D(this.x / n, this.z / n);
    }
    
    public double length() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }
    
    public double lengthSq() {
        return this.x * this.x + this.z * this.z;
    }
    
    public double distance(final Vector2D other) {
        return Math.sqrt(Math.pow(other.x - this.x, 2.0) + Math.pow(other.z - this.z, 2.0));
    }
    
    public double distanceSq(final Vector2D other) {
        return Math.pow(other.x - this.x, 2.0) + Math.pow(other.z - this.z, 2.0);
    }
    
    public Vector2D normalize() {
        return this.divide(this.length());
    }
    
    public double dot(final Vector2D other) {
        return this.x * other.x + this.z * other.z;
    }
    
    public boolean containedWithin(final Vector2D min, final Vector2D max) {
        return this.x >= min.x && this.x <= max.x && this.z >= min.z && this.z <= max.z;
    }
    
    public boolean containedWithinBlock(final Vector2D min, final Vector2D max) {
        return this.getBlockX() >= min.getBlockX() && this.getBlockX() <= max.getBlockX() && this.getBlockZ() >= min.getBlockZ() && this.getBlockZ() <= max.getBlockZ();
    }
    
    public Vector2D floor() {
        return new Vector2D(Math.floor(this.x), Math.floor(this.z));
    }
    
    public Vector2D ceil() {
        return new Vector2D(Math.ceil(this.x), Math.ceil(this.z));
    }
    
    public Vector2D round() {
        return new Vector2D(Math.floor(this.x + 0.5), Math.floor(this.z + 0.5));
    }
    
    public Vector2D positive() {
        return new Vector2D(Math.abs(this.x), Math.abs(this.z));
    }
    
    public Vector2D transform2D(double angle, final double aboutX, final double aboutZ, final double translateX, final double translateZ) {
        angle = Math.toRadians(angle);
        final double x = this.x - aboutX;
        final double z = this.z - aboutZ;
        final double x2 = x * Math.cos(angle) - z * Math.sin(angle);
        final double z2 = x * Math.sin(angle) + z * Math.cos(angle);
        return new Vector2D(x2 + aboutX + translateX, z2 + aboutZ + translateZ);
    }
    
    public boolean isCollinearWith(final Vector2D other) {
        if (this.x == 0.0 && this.z == 0.0) {
            return true;
        }
        final double otherX = other.x;
        final double otherZ = other.z;
        if (otherX == 0.0 && otherZ == 0.0) {
            return true;
        }
        if (this.x == 0.0 != (otherX == 0.0)) {
            return false;
        }
        if (this.z == 0.0 != (otherZ == 0.0)) {
            return false;
        }
        final double quotientX = otherX / this.x;
        if (!Double.isNaN(quotientX)) {
            return other.equals(this.multiply(quotientX));
        }
        final double quotientZ = otherZ / this.z;
        if (!Double.isNaN(quotientZ)) {
            return other.equals(this.multiply(quotientZ));
        }
        throw new RuntimeException("This should not happen");
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Vector2D)) {
            return false;
        }
        final Vector2D other = (Vector2D)obj;
        return other.x == this.x && other.z == this.z;
    }
    
    @Override
    public int hashCode() {
        return new Double(this.x).hashCode() >> 13 ^ new Double(this.z).hashCode();
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }
    
    public static Vector2D getMinimum(final Vector2D v1, final Vector2D v2) {
        return new Vector2D(Math.min(v1.x, v2.x), Math.min(v1.z, v2.z));
    }
    
    public static Vector2D getMaximum(final Vector2D v1, final Vector2D v2) {
        return new Vector2D(Math.max(v1.x, v2.x), Math.max(v1.z, v2.z));
    }
    
    static {
        ZERO = new Vector2D(0, 0);
        UNIT_X = new Vector2D(1, 0);
        UNIT_Z = new Vector2D(0, 1);
        ONE = new Vector2D(1, 1);
    }
}
