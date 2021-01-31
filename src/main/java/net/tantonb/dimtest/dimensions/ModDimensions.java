
package net.tantonb.dimtest.dimensions;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.dimensions.TestDim1.CaveChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModDimensions {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final RegistryKey<World> CAVE_DIM = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(DimTestMod.MODID, "cave"));
    public static final RegistryKey<World> ALTOVER_DIM = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(DimTestMod.MODID, "altover"));

    public static void setupDimensions() {
        LOGGER.info("Setting up dimensions...");
        CaveChunkGenerator.register();
    }
}