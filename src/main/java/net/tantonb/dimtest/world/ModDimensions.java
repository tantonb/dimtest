
package net.tantonb.dimtest.world;

import com.mojang.serialization.Lifecycle;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.world.dimx.DimxBiomeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModDimensions {

    public static final Logger LOGGER = LogManager.getLogger();

    // dimension ids defined as resource locations using the mod namespace and a name
    // these are used to tie together dimension related registry keys
    public static final ResourceLocation DIMX_ID = DimTestMod.resloc("dimx");
    public static final ResourceLocation CAVE_ID = DimTestMod.resloc("cave");
    public static final ResourceLocation ALTOVER_ID = DimTestMod.resloc("altover");

    // world keys
    public static final RegistryKey<World> RK_CAVE_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, CAVE_ID);
    public static final RegistryKey<World> RK_ALTOVER_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, ALTOVER_ID);
    public static final RegistryKey<World> RK_DIMX_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, DIMX_ID);

    // dimension settings keys
    public static final RegistryKey<DimensionSettings> RK_DIMX_NOISE_SETTINGS = RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, DIMX_ID);

    // dimension type keys (not used yet?)
    //public static final RegistryKey<DimensionType> RK_DIMX_DIMENSION_TYPE = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, DIMX_ID);

    // dimension keys (what is difference between world and dimension keys?)
    public static final RegistryKey<Dimension> RK_DIMX_DIMENSION = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, DIMX_ID);

    // create custom DimensionSettings object using desired NoiseSettings
    public static DimensionSettings createNoiseDimensionSettings(
            DimensionStructuresSettings structureSettingsIn,
            boolean flag1, BlockState fillerBlockIn, BlockState fluidBlockIn,
            ResourceLocation settingsLocationIn)
    {
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
                    structureSettingsIn,
                    new NoiseSettings(
                        256,
                        new ScalingSettings(
                                0.9999999814507745D,
                                0.9999999814507745D,
                                80.0D, 160.0D
                        ),
                        new SlideSettings(-10, 3, 0),
                        new SlideSettings(-30, 0, 0),
                        1, 2, 1.0D, -0.46875D,
                        true, true, false, flag1
                    ),
                    fillerBlockIn, fluidBlockIn,
                    -10, 0, 63, false
            );
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to create dimension settings. This issue should be reported!");
            e.printStackTrace();
        }

        return null;
    }

    private static DimensionSettings registerNoiseSettings(
            RegistryKey<DimensionSettings> settingsKeyIn,
            DimensionSettings dimSettingsIn)
    {
        WorldGenRegistries.register(WorldGenRegistries.NOISE_SETTINGS, settingsKeyIn.getLocation(), dimSettingsIn);
        return dimSettingsIn;
    }

    public static void initNoiseSettings() {
        registerNoiseSettings(
                RK_DIMX_NOISE_SETTINGS,
                createNoiseDimensionSettings(
                        new DimensionStructuresSettings(false),
                        false,
                        Blocks.STONE.getDefaultState(),
                        Blocks.WATER.getDefaultState(),
                        RK_DIMX_NOISE_SETTINGS.getLocation())
        );
    }

    private static ChunkGenerator createChunkGenerator(
            Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimSettingsRegistry, long seed)
    {
        return new NoiseChunkGenerator(DimxBiomeProvider.DimxPreset.dimxPreset.func_242619_a(biomeRegistry, seed), seed, () ->
        {
            return dimSettingsRegistry.getOrThrow(ModDimensions.RK_DIMX_NOISE_SETTINGS);
        });
    }

    private static DimensionType createDimensionType(
            OptionalLong time,
            boolean ultrawarm,
            boolean piglinSafe,
            ResourceLocation effectsId)
    {
        return new DimensionType(
                time, true, false, ultrawarm, true,
                1, false, piglinSafe, true,
                false, false, 256,
                FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(),
                effectsId, 0.0F)
        {
        };
    }

    /**
     * Entry point from MinecraftServerMixin
     *
     * Receives minecraft internals such as the world seed and normally
     * inaccessible dimension and biome registries needed to create new
     * custom dimensions.
     *
     */
    public static void initFromMixin(
            SimpleRegistry<Dimension> dimRegistry,
            //MutableRegistry<DimensionType> mutableRegistry,
            MutableRegistry<Biome> biomeRegistry,
            MutableRegistry<DimensionSettings> dimSettingsRegistry,
            long seed)
    {
        LOGGER.info("ModDimensions.init()...");

        // create noise based dimension settings function
        Function<RegistryKey<DimensionSettings>, DimensionSettings> dimSettings =
                (dimNoiseSettings) -> createNoiseDimensionSettings(
                        new DimensionStructuresSettings(false),
                        false,
                        Blocks.STONE.getDefaultState(),
                        Blocks.WATER.getDefaultState(),
                        RK_DIMX_NOISE_SETTINGS.getLocation()
                );


        Function<DimensionSettings, ChunkGenerator> chunkGenFactory =
                (s) -> createChunkGenerator(biomeRegistry, dimSettingsRegistry, seed);

        Supplier<DimensionType> dimTypeSupplier = () -> createDimensionType(OptionalLong.of(6000L), false, false, DIMX_ID);
        Dimension dimension = new Dimension(dimTypeSupplier, chunkGenFactory.apply(dimSettings.apply(RK_DIMX_NOISE_SETTINGS)));
        dimRegistry.register(RK_DIMX_DIMENSION, dimension, Lifecycle.stable());
    }
}