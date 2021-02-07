package net.tantonb.dimtest;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tantonb.dimtest.blocks.ModBlocks;
import net.tantonb.dimtest.config.ClientConfig;
import net.tantonb.dimtest.config.DimTestConfig;
import net.tantonb.dimtest.config.ServerConfig;
import net.tantonb.dimtest.items.ModItems;
import net.tantonb.dimtest.tileentity.ModTileEntities;
import net.tantonb.dimtest.world.ModDimensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimTestMod.MODID)
public class DimTestMod
{
    public static final String MODID = "dimtest";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    // used for ids, generate resource location using modid for namespace
    public static final ResourceLocation resLoc(String name) {
        return new ResourceLocation(DimTestMod.MODID, name);
    }

    public DimTestMod() {
        LOGGER.info("DimTestMod instantiation...");

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // mod loading
        modEventBus.addListener(this::setupCommon);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);
        ModDimensions.registerSettings();

        // load mod configuration
        DimTestConfig config = DimTestConfig.init();
        SERVER_CONFIG = config.server;
        CLIENT_CONFIG = config.client;

        LOGGER.info("client config show custon world warning? {}", CLIENT_CONFIG.showCustomWorldWarning);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        LOGGER.debug("Handling common setup event: {}", event);
        //event.enqueueWork(ModDimensions::setupDimensions);
    }
}
