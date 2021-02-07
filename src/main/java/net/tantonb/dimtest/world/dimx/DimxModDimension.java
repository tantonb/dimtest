package net.tantonb.dimtest.world.dimx;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.tantonb.dimtest.world.ModDimension;

import java.util.OptionalLong;
import java.util.function.Supplier;

public class DimxModDimension extends ModDimension {

    // dimension settings
    public static final BlockState filler = Blocks.STONE.getDefaultState();
    public static final BlockState fluid = Blocks.WATER.getDefaultState();
    public static final int bedrockFloor = -10;
    public static final int bedrockCeiling = 0;
    public static final int seaLevel = 63;
    public static final boolean disableMobGenFlag = false;

    // noise settings
    public static final int noiseHeight = 256;
    public static final double noiseScaleXz = 0.9999999814507745D;
    public static final double noiseScaleY = 0.9999999814507745D;
    public static final double noiseFactorXz = 80.0D;
    public static final double noiseFactorY = 160.0D;

    public static final int noiseTopTarget = -10;
    public static final int noiseTopSize = 3;
    public static final int noiseTopOffset = 0;

    public static final int noiseBottomTarget = -30;
    public static final int noiseBottomSize = 0;
    public static final int noiseBottomOffset = 0;

    public static final int noiseSizeHorizontal = 1; // 1 - 4
    public static final int noiseSizeVertical = 2; // 1 - 4
    public static final double noiseDensityFactor = 1.0D;
    public static final double noiseDensityOffset = -0.46875D;

    public static final boolean noiseSimplexSurfaceNoise = true;
    public static final boolean noiseRandomDensityOffset = true;
    public static final boolean noiseIslandNoiseOverride = false;
    public static final boolean noiseAmplified = false;

    public DimxModDimension(
            ResourceLocation dimensionId,
            RegistryKey<DimensionSettings> dimensionSettingsKey,
            RegistryKey<Dimension> dimensionKey)
    {
        super(dimensionId, dimensionSettingsKey, dimensionKey);
    }

    protected BlockState getFiller() { return filler; }

    protected BlockState getFluid() { return fluid; }

    protected int getBedrockFloor() { return bedrockFloor; }

    protected int getBedrockCeiling() { return bedrockCeiling; }

    protected int getSeaLevel() { return seaLevel; }

    protected boolean getDisableMobGenFlag() { return disableMobGenFlag; }

    private ScalingSettings createNoiseScalingSettings() {
        return new ScalingSettings(noiseScaleXz, noiseScaleY, noiseFactorXz, noiseFactorY);
    }

    private SlideSettings createTopSlideSettings() {
        return new SlideSettings(noiseTopTarget, noiseTopSize, noiseTopOffset);
    }

    private SlideSettings createBottomSlideSettings() {
        return new SlideSettings(noiseBottomTarget, noiseBottomSize, noiseBottomOffset);
    }

    protected NoiseSettings createNoiseSettings() {
        return new NoiseSettings(
                noiseHeight,
                createNoiseScalingSettings(),
                createTopSlideSettings(),
                createBottomSlideSettings(),
                noiseSizeHorizontal,
                noiseSizeVertical,
                noiseDensityFactor,
                noiseDensityOffset,
                noiseSimplexSurfaceNoise,
                noiseRandomDensityOffset,
                noiseIslandNoiseOverride,
                noiseAmplified
        );
    }

    protected BiomeProvider getBiomeProvider(long seed, Registry<Biome> biomeRegistry) {
        return DimxBiomeProvider.DimxPreset.dimxPreset.func_242619_a(biomeRegistry, seed);
    }

    protected ChunkGenerator createChunkGenerator(
            long seed,
            MutableRegistry<Biome> biomeRegistry,
            MutableRegistry<DimensionSettings> dimensionSettingsRegistry)
    {
        BiomeProvider biomeProvider = getBiomeProvider(seed, biomeRegistry);
        Supplier<DimensionSettings> dimSettingsSupplier =
                () -> dimensionSettingsRegistry.getOrThrow(dimensionSettingsKey);
        return new NoiseChunkGenerator(biomeProvider, seed, dimSettingsSupplier);
    }

    protected DimensionType createDimensionType() {
        return new DimensionType(
                OptionalLong.of(6000L), true, false, false, true,
                1, false, false, true,
                false, false, 256,
                FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(),
                dimensionId, 0.0F)
        {
        };
    }
}
