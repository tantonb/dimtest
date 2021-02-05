package net.tantonb.dimtest;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tantonb.dimtest.world.dimx.DimxBiomes;

@EventBusSubscriber(modid = DimTestMod.MODID, bus = Bus.MOD)
public class ModRegistration {
    @SubscribeEvent
    public static void onRegisterSounds(RegistryEvent.Register<SoundEvent> event)
    {
        //GNSSounds.init(event);
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event)
    {
        //GNSBlocks.init(event);
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event)
    {
        /*
        GNSItems.init(event);

        for (int i3 = 0; i3 < GNSBlocks.gnsBlockList.size(); ++i3)
        {
            register(event.getRegistry(), GNSBlocks.gnsBlockList.get(i3).getRegistryName().toString().replace(GoodNightSleep.MODID + ":", ""), new BlockItem(GNSBlocks.gnsBlockList.get(i3), (new Item.Properties().group(GNSCreativeTabs.blocks))));
        }
        */
    }

    @SubscribeEvent
    public static void onRegisterEntityTypes(Register<EntityType<?>> event)
    {
        //GNSEntityTypes.init(event);
    }

    @SubscribeEvent
    public static void registerTileEntityTypes(Register<TileEntityType<?>> event)
    {
        //GNSTileEntityTypes.init(event);
    }

    @SubscribeEvent
    public static void onRegisterBiomes(Register<Biome> event)
    {
        DimxBiomes.init(event);
    }

    @SubscribeEvent
    public static void registerFeatures(Register<Feature<?>> event)
    {
        //GNSFeatures.init(event);
    }

    @SubscribeEvent
    public static void onRegisterWorldCarvers(Register<WorldCarver<?>> event)
    {
        //GNSFeatures.Carvers.init(event);
    }

    public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, String name, T object)
    {
        object.setRegistryName(DimTestMod.resloc(name));
        registry.register(object);
    }
}
