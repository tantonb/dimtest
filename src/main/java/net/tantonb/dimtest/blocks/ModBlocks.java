package net.tantonb.dimtest.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tantonb.dimtest.DimTestMod;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DimTestMod.MODID);

    public static final RegistryObject<Block> CAVE_PORTAL = register("cave_portal", CavePortalBlock::new);
    public static final RegistryObject<Block> ALTOVER_PORTAL = register("altover_portal", AltoverPortalBlock::new);
    public static final RegistryObject<Block> DIMX_PORTAL = register("dimx_portal", DimxPortalBlock::new);

    public static <B extends Block> RegistryObject<B> register(String name, Supplier<B> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
