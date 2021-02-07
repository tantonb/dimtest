
package net.tantonb.dimtest.world;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.world.dimx.DimxModDimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class ModDimensions {

    public static final Logger LOGGER = LogManager.getLogger();

    // dimension ids used to tie together dimension related registry keys
    public static final ResourceLocation DIMX_ID = DimTestMod.resLoc("dimx");
    public static final ResourceLocation CAVE_ID = DimTestMod.resLoc("cave");
    public static final ResourceLocation ALTOVER_ID = DimTestMod.resLoc("altover");

    // world keys (used by portals)
    public static final RegistryKey<World> RK_CAVE_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, CAVE_ID);
    public static final RegistryKey<World> RK_ALTOVER_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, ALTOVER_ID);
    public static final RegistryKey<World> RK_DIMX_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, DIMX_ID);

    // dimension settings keys
    public static final RegistryKey<DimensionSettings> RK_DIMX_DIMENSION_SETTINGS = RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, DIMX_ID);

    // dimension type keys (not used yet?)
    public static final RegistryKey<DimensionType> RK_DIMX_DIMENSION_TYPE = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, DIMX_ID);

    // dimension keys (what is difference between world and dimension keys?)
    public static final RegistryKey<Dimension> RK_DIMX_DIMENSION = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, DIMX_ID);

    public static List<ModDimension> MOD_DIMENSIONS = Arrays.asList(
            new DimxModDimension(DIMX_ID, RK_DIMX_DIMENSION_SETTINGS, RK_DIMX_DIMENSION)
    );

    /**
     * Called during mod loading so dimension settings exist prior to mixin server registration call...
     */
    public static void registerSettings() {
        for (ModDimension dim : MOD_DIMENSIONS) dim.registerSettings();
    }

    /**
     * Entry point from MinecraftServerMixin
     *
     * Called during minecraft server initialization.
     *
     * Receives minecraft internals such as the world seed and normally
     * inaccessible dimension registries needed to create new custom dimensions.
     *
     */
    public static void register(
            long seed,
            MutableRegistry<Biome> biomeRegistry,
            MutableRegistry<DimensionSettings> dimensionSettingsRegistry,
            SimpleRegistry<Dimension> dimensionRegistry)
    {
        for (ModDimension dim : MOD_DIMENSIONS) {
            dim.register(seed, biomeRegistry, dimensionSettingsRegistry, dimensionRegistry);
        }
    }
}