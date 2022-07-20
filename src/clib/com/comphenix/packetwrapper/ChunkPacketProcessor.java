/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.StructureModifier
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Location;
import org.bukkit.World;

public class ChunkPacketProcessor {
    protected static final int BYTES_PER_NIBBLE_PART = 2048;
    protected static final int CHUNK_SEGMENTS = 16;
    protected static final int NIBBLES_REQUIRED = 4;
    public static final int BLOCK_ID_LENGHT = 4096;
    public static final int DATA_LENGHT = 2048;
    public static final int BIOME_ARRAY_LENGTH = 256;
    private int chunkX;
    private int chunkZ;
    private int chunkMask;
    private int extraMask;
    private int chunkSectionNumber;
    private int extraSectionNumber;
    private boolean hasContinous = true;
    private int startIndex;
    private int size;
    private byte[] data;
    private World world;

    private ChunkPacketProcessor() {
    }

    public static ChunkPacketProcessor fromMapPacket(PacketContainer packet, World world) {
        if (!packet.getType().equals(PacketType.Play.Server.MAP_CHUNK)) {
            throw new IllegalArgumentException(packet + " must be a MAP_CHUNK packet.");
        }
        StructureModifier ints = packet.getIntegers();
        StructureModifier byteArray = packet.getByteArrays();
        ChunkPacketProcessor processor = new ChunkPacketProcessor();
        processor.world = world;
        processor.chunkX = (Integer)ints.read(0);
        processor.chunkZ = (Integer)ints.read(1);
        processor.chunkMask = (Integer)ints.read(2);
        processor.extraMask = (Integer)ints.read(3);
        processor.data = (byte[])byteArray.read(1);
        processor.startIndex = 0;
        if (packet.getBooleans().size() > 0) {
            processor.hasContinous = packet.getBooleans().read(0);
        }
        return processor;
    }

    public static ChunkPacketProcessor[] fromMapBulkPacket(PacketContainer packet, World world) {
        if (!packet.getType().equals(PacketType.Play.Server.MAP_CHUNK_BULK)) {
            throw new IllegalArgumentException(packet + " must be a MAP_CHUNK_BULK packet.");
        }
        StructureModifier intArrays = packet.getIntegerArrays();
        StructureModifier byteArrays = packet.getByteArrays();
        int[] x = (int[])intArrays.read(0);
        int[] z = (int[])intArrays.read(1);
        ChunkPacketProcessor[] processors = new ChunkPacketProcessor[x.length];
        int[] chunkMask = (int[])intArrays.read(2);
        int[] extraMask = (int[])intArrays.read(3);
        int dataStartIndex = 0;
        for (int chunkNum = 0; chunkNum < processors.length; ++chunkNum) {
            ChunkPacketProcessor processor;
            processors[chunkNum] = processor = new ChunkPacketProcessor();
            processor.world = world;
            processor.chunkX = x[chunkNum];
            processor.chunkZ = z[chunkNum];
            processor.chunkMask = chunkMask[chunkNum];
            processor.extraMask = extraMask[chunkNum];
            processor.hasContinous = true;
            processor.data = (byte[])byteArrays.read(1);
            if (processor.data == null || processor.data.length == 0) {
                processor.data = packet.getSpecificModifier(byte[][].class).read(0)[chunkNum];
            } else {
                processor.startIndex = dataStartIndex;
            }
            dataStartIndex += processor.size;
        }
        return processors;
    }

    public void process(ChunkletProcessor processor) {
        for (int i = 0; i < 16; ++i) {
            if ((this.chunkMask & 1 << i) > 0) {
                ++this.chunkSectionNumber;
            }
            if ((this.extraMask & 1 << i) <= 0) continue;
            ++this.extraSectionNumber;
        }
        int skylightCount = this.getSkylightCount();
        this.size = 2048 * ((4 + skylightCount) * this.chunkSectionNumber + this.extraSectionNumber) + (this.hasContinous ? 256 : 0);
        if (this.getOffset(2) - this.startIndex > this.data.length) {
            return;
        }
        if (this.isChunkLoaded(this.world, this.chunkX, this.chunkZ)) {
            this.translate(processor);
        }
    }

    protected int getSkylightCount() {
        return this.world.getEnvironment() == World.Environment.NORMAL ? 1 : 0;
    }

    private int getOffset(int nibbles) {
        return this.startIndex + nibbles * this.chunkSectionNumber * 2048;
    }

    private void translate(ChunkletProcessor processor) {
        int current = 4;
        ChunkOffsets offsets = new ChunkOffsets(this.getOffset(0), this.getOffset(2), this.getOffset(3), this.getSkylightCount() > 0 ? this.getOffset(current++) : -1, this.extraSectionNumber > 0 ? this.getOffset(current++) : -1);
        for (int i = 0; i < 16; ++i) {
            if ((this.chunkMask & 1 << i) > 0) {
                Location origin = new Location(this.world, this.chunkX << 4, i * 16, this.chunkZ << 4);
                processor.processChunklet(origin, this.data, offsets);
                offsets.incrementIdIndex();
            }
            if ((this.extraMask & 1 << i) <= 0) continue;
            offsets.incrementExtraIndex();
        }
        if (this.hasContinous) {
            processor.processBiomeArray(new Location(this.world, this.chunkX << 4, 0.0, this.chunkZ << 4), this.data, this.startIndex + this.size - 256);
        }
    }

    private boolean isChunkLoaded(World world, int x, int z) {
        return world.isChunkLoaded(x, z);
    }

    public interface ChunkletProcessor {
        void processChunklet(Location var1, byte[] var2, ChunkOffsets var3);

        void processBiomeArray(Location var1, byte[] var2, int var3);
    }

    public static class ChunkOffsets {
        private int blockIdOffset;
        private int dataOffset;
        private final int lightOffset;
        private int skylightOffset;
        private int extraOffset;

        private ChunkOffsets(int blockIdOffset, int dataOffset, int lightOffset, int skylightOffset, int extraOffset) {
            this.blockIdOffset = blockIdOffset;
            this.dataOffset = dataOffset;
            this.lightOffset = lightOffset;
            this.skylightOffset = skylightOffset;
            this.extraOffset = extraOffset;
        }

        private void incrementIdIndex() {
            this.blockIdOffset += 4096;
            this.dataOffset += 2048;
            this.dataOffset += 2048;
            if (this.skylightOffset >= 0) {
                this.skylightOffset += 2048;
            }
        }

        private void incrementExtraIndex() {
            if (this.extraOffset >= 0) {
                this.extraOffset += 2048;
            }
        }

        public int getBlockIdOffset() {
            return this.blockIdOffset;
        }

        public int getDataOffset() {
            return this.dataOffset;
        }

        public int getLightOffset() {
            return this.lightOffset;
        }

        public int getSkylightOffset() {
            return this.skylightOffset;
        }

        public boolean hasSkylightOffset() {
            return this.skylightOffset >= 0;
        }

        public int getExtraOffset() {
            return this.extraOffset;
        }

        public boolean hasExtraOffset() {
            return this.extraOffset > 0;
        }
    }
}

