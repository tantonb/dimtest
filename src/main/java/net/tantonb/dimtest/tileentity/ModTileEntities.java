package net.tantonb.dimtest.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.blocks.ModBlocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.tantonb.dimtest.DimTestMod.MODID;

public class ModTileEntities {

    private static final Logger LOGGER = LogManager.getLogger(DimTestMod.MODID);

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public static final RegistryObject<TileEntityType<TestDim1TE>> CAVE_PORTAL_TE = TILE_ENTITIES.register("cave_portal", () -> TileEntityType.Builder.create(TestDim1TE::new, ModBlocks.CAVE_PORTAL.get()).build(null));
    public static final RegistryObject<TileEntityType<AltoverTE>> ALTOVER_PORTAL_TE = TILE_ENTITIES.register("altover_portal", () -> TileEntityType.Builder.create(AltoverTE::new, ModBlocks.ALTOVER_PORTAL.get()).build(null));
    public static final RegistryObject<TileEntityType<TeleporterTE>> PORTAL_TE = TILE_ENTITIES.register("portal", () -> TileEntityType.Builder.create(TeleporterTE::new, ModBlocks.CAVE_PORTAL.get(), ModBlocks.ALTOVER_PORTAL.get()).build(null));

    public static void register(IEventBus modBus) {
        TILE_ENTITIES.register(modBus);
    }

}
