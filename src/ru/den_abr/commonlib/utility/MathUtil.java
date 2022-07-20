//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.den_abr.commonlib.utility;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class MathUtil {
    private static final int CHUNK_BITS = 4;
    private static final int CHUNK_VALUES = 16;
    public static final float DEGTORAD = 0.017453292F;
    public static final float RADTODEG = 57.29578F;
    public static final double HALFROOTOFTWO = 0.707106781D;
    static final double sq2p1 = 2.414213562373095D;
    static final double sq2m1 = 0.41421356237309503D;
    static final double p4 = 16.15364129822302D;
    static final double p3 = 268.42548195503974D;
    static final double p2 = 1153.029351540485D;
    static final double p1 = 1780.406316433197D;
    static final double p0 = 896.7859740366387D;
    static final double q4 = 58.95697050844462D;
    static final double q3 = 536.2653740312153D;
    static final double q2 = 1666.7838148816338D;
    static final double q1 = 2079.33497444541D;
    static final double q0 = 896.7859740366387D;
    static final double PIO2 = 1.5707963267948966D;

    public MathUtil() {
    }

    public static double lengthSquared(double... values) {
        double rval = 0.0D;
        double[] var3 = values;
        int var4 = values.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            double value = var3[var5];
            rval += value * value;
        }

        return rval;
    }

    public static double length(double... values) {
        return Math.sqrt(lengthSquared(values));
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return length(x1 - x2, y1 - y2);
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return lengthSquared(x1 - x2, y1 - y2);
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return length(x1 - x2, y1 - y2, z1 - z2);
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return lengthSquared(x1 - x2, y1 - y2, z1 - z2);
    }

    public static double getPercentage(int subtotal, int total, int decimals) {
        return round(getPercentage(subtotal, total), decimals);
    }

    public static double getPercentage(int subtotal, int total) {
        return (double)((float)subtotal / (float)total * 100.0F);
    }

    public static int getAngleDifference(int angle1, int angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    public static int wrapAngle(int angle) {
        int wrappedAngle;
        for(wrappedAngle = angle; wrappedAngle <= -180; wrappedAngle += 360) {
        }

        while(wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

    public static float wrapAngle(float angle) {
        float wrappedAngle;
        for(wrappedAngle = angle; wrappedAngle <= -180.0F; wrappedAngle += 360.0F) {
        }

        while(wrappedAngle > 180.0F) {
            wrappedAngle -= 360.0F;
        }

        return wrappedAngle;
    }

    public static double normalize(double x, double z, double reqx, double reqz) {
        return Math.sqrt(lengthSquared(reqx, reqz) / lengthSquared(x, z));
    }

    public static float getLookAtYaw(Entity loc, Entity lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Block loc, Block lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Location loc, Location lookat) {
        return getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
    }

    public static float getLookAtYaw(Vector motion) {
        return getLookAtYaw(motion.getX(), motion.getZ());
    }

    public static float getLookAtYaw(double dx, double dz) {
        return atan2(dz, dx) - 180.0F;
    }

    public static float getLookAtPitch(double dX, double dY, double dZ) {
        return getLookAtPitch(dY, length(dX, dZ));
    }

    public static float getLookAtPitch(double dY, double dXZ) {
        return -atan(dY / dXZ);
    }

    public static float atan(double value) {
        return 57.29578F * (float)atan_0(value);
    }

    public static float atan2(double y, double x) {
        return 57.29578F * (float)atan2_0(y, x);
    }

    public static long longFloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int ceil(double value) {
        return -floor(-value);
    }

    public static int ceil(float value) {
        return -floor(-value);
    }

    public static Location move(Location loc, Vector offset) {
        return move(loc, offset.getX(), offset.getY(), offset.getZ());
    }

    public static Location move(Location loc, double dx, double dy, double dz) {
        Vector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        float angle = yaw * 0.017453292F;
        double sinyaw = Math.sin((double)angle);
        double cosyaw = Math.cos((double)angle);
        angle = pitch * 0.017453292F;
        double sinpitch = Math.sin((double)angle);
        double cospitch = Math.cos((double)angle);
        Vector vector = new Vector();
        vector.setX(x * sinyaw - y * cosyaw * sinpitch - z * cosyaw * cospitch);
        vector.setY(y * cospitch - z * sinpitch);
        vector.setZ(-(x * cosyaw) - y * sinyaw * sinpitch - z * sinyaw * cospitch);
        return vector;
    }

    public static int floorMod(int x, int y) {
        return Math.floorMod(x, y);
    }

    public static long floorMod(long x, long y) {
        return Math.floorMod(x, y);
    }

    public static int floorDiv(int x, int y) {
        return Math.floorDiv(x, y);
    }

    public static long floorDiv(long x, long y) {
        return Math.floorDiv(x, y);
    }

    public static double round(double value, int decimals) {
        double p = Math.pow(10.0D, (double)decimals);
        return (double)Math.round(value * p) / p;
    }

    public static double fixNaN(double value) {
        return fixNaN(value, 0.0D);
    }

    public static float fixNaN(float value) {
        return fixNaN(value, 0.0F);
    }

    public static double fixNaN(double value, double def) {
        return Double.isNaN(value) ? def : value;
    }

    public static float fixNaN(float value, float def) {
        return Float.isNaN(value) ? def : value;
    }

    public static int toChunk(double loc) {
        return floor(loc / 16.0D);
    }

    public static int toChunk(int loc) {
        return loc >> 4;
    }

    public static double useOld(double oldvalue, double newvalue, double peruseold) {
        return oldvalue + peruseold * (newvalue - oldvalue);
    }

    public static double lerp(double d1, double d2, double stage) {
        if (!Double.isNaN(stage) && stage <= 1.0D) {
            return stage < 0.0D ? d1 : d1 * (1.0D - stage) + d2 * stage;
        } else {
            return d2;
        }
    }

    public static Vector lerp(Vector vec1, Vector vec2, double stage) {
        Vector newvec = new Vector();
        newvec.setX(lerp(vec1.getX(), vec2.getX(), stage));
        newvec.setY(lerp(vec1.getY(), vec2.getY(), stage));
        newvec.setZ(lerp(vec1.getZ(), vec2.getZ(), stage));
        return newvec;
    }

    public static Location lerp(Location loc1, Location loc2, double stage) {
        Location newloc = new Location(loc1.getWorld(), 0.0D, 0.0D, 0.0D);
        newloc.setX(lerp(loc1.getX(), loc2.getX(), stage));
        newloc.setY(lerp(loc1.getY(), loc2.getY(), stage));
        newloc.setZ(lerp(loc1.getZ(), loc2.getZ(), stage));
        newloc.setYaw((float)lerp((double)loc1.getYaw(), (double)loc2.getYaw(), stage));
        newloc.setPitch((float)lerp((double)loc1.getPitch(), (double)loc2.getPitch(), stage));
        return newloc;
    }

    public static boolean isInverted(double value1, double value2) {
        return value1 > 0.0D && value2 < 0.0D || value1 < 0.0D && value2 > 0.0D;
    }

    public static Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        double rotX = (double)(0.017453292F * yaw);
        double rotY = (double)(0.017453292F * pitch);
        vector.setY(-Math.sin(rotY));
        double h = Math.cos(rotY);
        vector.setX(-h * Math.sin(rotX));
        vector.setZ(h * Math.cos(rotX));
        return vector;
    }

    public static double clamp(double value, double limit) {
        return clamp(value, -limit, limit);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float clamp(float value, float limit) {
        return clamp(value, -limit, limit);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int limit) {
        return clamp(value, -limit, limit);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static long clamp(long value, long limit) {
        return clamp(value, -limit, limit);
    }

    public static long clamp(long value, long min, long max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int invert(int value, boolean negative) {
        return negative ? -value : value;
    }

    public static float invert(float value, boolean negative) {
        return negative ? -value : value;
    }

    public static double invert(double value, boolean negative) {
        return negative ? -value : value;
    }

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - -2147483648L;
    }

    public static long longHashToLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - -2147483648L;
    }

    public static int longHashMsw(long key) {
        return (int)(key >> 32);
    }

    public static int longHashLsw(long key) {
        return (int)(key & -1L) + -2147483648;
    }

    public static long longHashAdd(long key_a, long key_b) {
        return key_a + key_b + -2147483648L;
    }

    public static void setVectorLength(Vector vector, double length) {
        setVectorLengthSquared(vector, Math.signum(length) * length * length);
    }

    public static void setVectorLengthSquared(Vector vector, double lengthsquared) {
        double vlength = vector.lengthSquared();
        if (Math.abs(vlength) > 1.0E-4D) {
            if (lengthsquared < 0.0D) {
                vector.multiply(-Math.sqrt(-lengthsquared / vlength));
            } else {
                vector.multiply(Math.sqrt(lengthsquared / vlength));
            }
        }

    }

    public static boolean isHeadingTo(BlockFace direction, Vector velocity) {
        return isHeadingTo(FaceUtil.faceToVector(direction), velocity);
    }

    public static boolean isHeadingTo(Location from, Location to, Vector velocity) {
        return isHeadingTo(new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()), velocity);
    }

    public static boolean isHeadingTo(Vector offset, Vector velocity) {
        double dbefore = offset.lengthSquared();
        if (dbefore < 1.0E-4D) {
            return true;
        } else {
            Vector clonedVelocity = velocity.clone();
            setVectorLengthSquared(clonedVelocity, dbefore);
            return dbefore > clonedVelocity.subtract(offset).lengthSquared();
        }
    }

    public static double getNormalizationFactor(Vector v) {
        return getNormalizationFactorLS(v.lengthSquared());
    }

    public static double getNormalizationFactor(double x, double y, double z, double w) {
        return getNormalizationFactorLS(x * x + y * y + z * z + w * w);
    }

    public static double getNormalizationFactor(double x, double y, double z) {
        return getNormalizationFactorLS(x * x + y * y + z * z);
    }

    public static double getNormalizationFactor(double x, double y) {
        return getNormalizationFactorLS(x * x + y * y);
    }

    public static double getNormalizationFactorLS(double lengthSquared) {
        return Math.abs(1.0D - lengthSquared) < 2.107342E-8D ? 2.0D / (1.0D + lengthSquared) : 1.0D / Math.sqrt(lengthSquared);
    }

    public static double getAngleDifference(Vector v0, Vector v1) {
        double dot = v0.dot(v1);
        dot *= getNormalizationFactor(v0);
        dot *= getNormalizationFactor(v1);
        return Math.toDegrees(Math.acos(dot));
    }

    private static double mxatan(double arg) {
        double argsq = arg * arg;
        double value = (((16.15364129822302D * argsq + 268.42548195503974D) * argsq + 1153.029351540485D) * argsq + 1780.406316433197D) * argsq + 896.7859740366387D;
        value /= ((((argsq + 58.95697050844462D) * argsq + 536.2653740312153D) * argsq + 1666.7838148816338D) * argsq + 2079.33497444541D) * argsq + 896.7859740366387D;
        return value * arg;
    }

    private static double msatan(double arg) {
        return arg < 0.41421356237309503D ? mxatan(arg) : (arg > 2.414213562373095D ? 1.5707963267948966D - mxatan(1.0D / arg) : 0.7853981633974483D + mxatan((arg - 1.0D) / (arg + 1.0D)));
    }

    public static double atan_0(double arg) {
        return arg > 0.0D ? msatan(arg) : -msatan(-arg);
    }

    public static double atan2_0(double arg1, double arg2) {
        if (arg1 + arg2 == arg1) {
            return arg1 >= 0.0D ? 1.5707963267948966D : -1.5707963267948966D;
        } else {
            arg1 = (double)atan(arg1 / arg2);
            return arg2 < 0.0D ? (arg1 <= 0.0D ? arg1 + 3.141592653589793D : arg1 - 3.141592653589793D) : arg1;
        }
    }
}
