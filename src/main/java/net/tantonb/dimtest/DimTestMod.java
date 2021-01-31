package net.tantonb.dimtest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tantonb.dimtest.blocks.ModBlocks;
import net.tantonb.dimtest.config.DimTestConfig;
import net.tantonb.dimtest.dimensions.ModDimensions;
import net.tantonb.dimtest.items.ModItems;
import net.tantonb.dimtest.tileentity.ModTileEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimTestMod.MODID)
public class DimTestMod
{
    public static final String MODID = "dimtest";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public DimTestMod() {
        LOGGER.info("DimTestMod instantiation...");

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // load mod configuration
        DimTestConfig config = DimTestConfig.init();

        // mod loading
        modEventBus.addListener(this::setupCommon);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);

    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        LOGGER.debug("Handling common setup event: {}", event);
        event.enqueueWork(ModDimensions::setupDimensions);
    }
}
