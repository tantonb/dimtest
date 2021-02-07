package net.tantonb.dimtest.world;

import com.mojang.serialization.Lifecycle;
import net.minecraft.block.BlockState;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

abstract public class ModDimension {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected ResourceLocation dimensionId;
    protected RegistryKey<DimensionSettings> dimensionSettingsKey;
    protected RegistryKey<Dimension> dimensionKey;

    public ModDimension(
            ResourceLocation dimensionId,
            RegistryKey<DimensionSettings> dimensionSettingsKey,
            RegistryKey<Dimension> dimensionKey)
    {
        this.dimensionId = dimensionId;
        this.dimensionSettingsKey = dimensionSettingsKey;
        this.dimensionKey = dimensionKey;
    }

    protected DimensionStructuresSettings createStructuresSettings() {
        return new DimensionStructuresSettings(false);
    }

    abstract protected NoiseSettings createNoiseSettings();

    abstract protected BlockState getFiller();

    abstract protected BlockState getFluid();

    abstract protected int getBedrockFloor();

    abstract protected int getBedrockCeiling();

    abstract protected int getSeaLevel();

    abstract protected boolean getDisableMobGenFlag();

    protected DimensionSettings createDimensionSettings() {
        try
        {
            Constructor<DimensionSettings> constructor =
                    DimensionSettings.class.getDeclaredConstructor(
                            DimensionStructuresSettings.class,
                            NoiseSettings.class, BlockState.class, BlockState.class,
                            int.class, int.class, int.class, boolean.class
                    );
            constructor.setAccessible(true);
            return constructor.newInstance(
                    createStructuresSettings(),
                    createNoiseSettings(),
                    getFiller(),
                    getFluid(),
                    getBedrockFloor(),
                    getBedrockCeiling(),
                    getSeaLevel(),
                    getDisableMobGenFlag()
            );
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to create dimension settings. This issue should be reported!");
            e.printStackTrace();
        }

        return null;
    }


    public void registerSettings() {
        LOGGER.info("Registering dimension {} settings", dimensionId.getPath());
        WorldGenRegistries.register(
                WorldGenRegistries.NOISE_SETTINGS,
                dimensionSettingsKey.getLocation(),
                createDimensionSettings()
        );
    }

    abstract protected ChunkGenerator createChunkGenerator(
            long seed,
            MutableRegistry<Biome> biomeRegistry,
            MutableRegistry<DimensionSettings> dimensionSettingsRegistry
    );

    abstract protected DimensionType createDimensionType();

    public void register(
            long seed,
            MutableRegistry<Biome> biomeRegistry,
            MutableRegistry<DimensionSettings> dimensionSettingsRegistry,
            SimpleRegistry<Dimension> dimensionRegistry)
    {
        LOGGER.info("Registering dimension {}", dimensionId.getPath());
        ChunkGenerator chunkGenerator = createChunkGenerator(seed, biomeRegistry, dimensionSettingsRegistry);
        Supplier<DimensionType> dimTypeSupplier = () -> createDimensionType();
        Dimension dimension = new Dimension(dimTypeSupplier, chunkGenerator);
        dimensionRegistry.register(dimensionKey, dimension, Lifecycle.stable());
    }

}
