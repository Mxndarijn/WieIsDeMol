package nl.mxndarijn.util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int cx, int cz, @NotNull BiomeGrid biomes) {
        return this.createChunkData(world);
    }
}
