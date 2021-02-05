package net.tantonb.dimtest.items;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.blocks.ModBlocks;

import java.util.function.Supplier;

public class ModItems {

    public static final ItemGroup DIMTEST_ITEM_GROUP = new ItemGroup("dimtest_group") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.CAVE_PORTAL.get());
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimTestMod.MODID);

    public static final RegistryObject<Item> CAVE_PORTAL = register("cave_portal", () -> new BlockItem(ModBlocks.CAVE_PORTAL.get(), new Item.Properties().group(DIMTEST_ITEM_GROUP)));
    public static final RegistryObject<Item> ALTOVER_PORTAL = register("altover_portal", () -> new BlockItem(ModBlocks.ALTOVER_PORTAL.get(), new Item.Properties().group(DIMTEST_ITEM_GROUP)));
    public static final RegistryObject<Item> DIMX_PORTAL = register("dimx_portal", () -> new BlockItem(ModBlocks.DIMX_PORTAL.get(), new Item.Properties().group(DIMTEST_ITEM_GROUP)));

    public static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item) {
        return ITEMS.register(name, item);
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
