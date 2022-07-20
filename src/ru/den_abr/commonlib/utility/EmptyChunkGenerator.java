package ru.den_abr.commonlib.utility;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator
{
    public static final EmptyChunkGenerator INSTANCE;
    
    public byte[] generate(final World world, final Random random, final int cx, final int cz) {
        final int height = world.getMaxHeight();
        final byte[] result = new byte[256 * height];
        return result;
    }
    
    public boolean canSpawn(final World world, final int x, final int z) {
        return false;
    }
    
    static {
        INSTANCE = new EmptyChunkGenerator();
    }
}
