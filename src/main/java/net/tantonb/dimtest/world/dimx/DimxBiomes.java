package net.tantonb.dimtest.world.dimx;

import java.util.Optional;

import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.world.ModDimensions;
import net.tantonb.dimtest.ModRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimxBiomes {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final Biome HILLS = DimxBiomeDataProvider.Makers.HILLS;

    public static void init(Register<Biome> event) {
        LOGGER.info(" *** initializing dimx biomes...");
        IForgeRegistry<Biome> registry = event.getRegistry();
        ModDimensions.initNoiseSettings();
        register(registry, "hills", HILLS);
        Registry.register(Registry.BIOME_PROVIDER_CODEC, "dimx_multi_noise", DimxBiomeProvider.dimxProviderCodec);
    }

    private static void register(IForgeRegistry<Biome> registryIn, String keyIn, Biome biomeIn) {
        ModRegistration.register(registryIn, keyIn, biomeIn);
    }

    public static class SurfaceBuilders {
        public static final SurfaceBuilderConfig DIMX_GRASS_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState());
        public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> DIMX_GRASS_SURFACE_BUILDER = SurfaceBuilder.DEFAULT.func_242929_a(DIMX_GRASS_DIRT_GRAVEL_CONFIG);
    }

    public static class Keys {
        public static final RegistryKey<Biome> HILLS = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, DimTestMod.resloc("hills"));

        public static RegistryKey<Biome> getKeyFromBiome(World world, Biome biomeIn) {
            Optional<RegistryKey<Biome>> biome = world.func_241828_r().getRegistry(Registry.BIOME_KEY).getOptionalKey(biomeIn);

            if (biome.isPresent())
                return biome.get();

            return null;
        }

    }
}