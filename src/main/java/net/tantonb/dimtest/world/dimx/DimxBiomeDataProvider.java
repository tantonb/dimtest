package net.tantonb.dimtest.world.dimx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.biome.*;
import net.tantonb.dimtest.DimTestMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.BiomeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class DimxBiomeDataProvider extends BiomeProvider
{
    public static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator = ObfuscationReflectionHelper.getPrivateValue(BiomeProvider.class, this, "field_244197_d");
    private static final Gson gsonBuilder = (new GsonBuilder()).setPrettyPrinting().create();
    protected static Map<ResourceLocation, Biome> BIOMES = new HashMap<>();

    public DimxBiomeDataProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    @Override
    public void act(DirectoryCache cache)
    {
        Makers.init();
        System.out.println("trying");

        LOGGER.info("burger beginning biome gen");
        Path path = this.generator.getOutputFolder();

        for (Entry<ResourceLocation, Biome> entry : BIOMES.entrySet())
        {
            Path path1 = createPath(path, entry.getKey());
            Biome biome = entry.getValue();
            Function<Supplier<Biome>, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(Biome.BIOME_CODEC);

            try
            {
                Optional<JsonElement> optional = function.apply(() ->
                {
                    return biome;
                }).result();
                if (optional.isPresent())
                {
                    IDataProvider.save(gsonBuilder, cache, optional.get(), path1);
                }
                else
                {
                    LOGGER.error("Couldn't serialize biome {}", (Object) path1);
                }
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn't save biome {}", path1, ioexception);
            }
        }

    }

    private static Path createPath(Path pathIn, ResourceLocation locationIn)
    {
        Path path = pathIn.resolve("data/" + locationIn.getNamespace() + "/worldgen/biome/" + locationIn.getPath() + ".json");
        System.out.println(path.toString());
        return path;
    }

    public static class Makers
    {
        public static final Biome HILLS = createHillsBiome(0.1F, 0.5F, 0.5F, 0.0F, 4159204, 329011);

        public static void init()
        {
            BIOMES.put(DimTestMod.resLoc("hills"), HILLS); // 329011
        }

        /**
         * The hills biome.
         */
        public static Biome createHillsBiome(
                float depthIn,
                float scaleIn,
                float tempIn,
                float downfallIn,
                int waterColorIn,
                int waterFogColorIn)
        {
            MobSpawnInfo.Builder spawnInfoBuilder = new MobSpawnInfo.Builder();
            /*
            spawnInfoBuilder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(GNSEntityTypes.SPAWNER_ENTITY, 140, 1, 1));
            spawnInfoBuilder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(GNSEntityTypes.UNICORN, 90, 1, 4));
            spawnInfoBuilder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(GNSEntityTypes.BABY_CREEPER, 10, 1, 4));
            */
            MobSpawnInfo spawnInfo = spawnInfoBuilder.copy();

            BiomeGenerationSettings.Builder generationBuilder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(DimxBiomes.SurfaceBuilders.DIMX_GRASS_SURFACE_BUILDER);
            /*
            GNSFeatures.addDreamTrees(generationBuilder);
            GNSFeatures.addHugeHopeMushrooms(generationBuilder);
            GNSFeatures.addScatteredDreamFeatures(generationBuilder);
            GNSFeatures.addDreamOres(generationBuilder);
            generationBuilder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, GNSFeatures.Configured.NOISE_BASED_DREAM_GRASS);
            generationBuilder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, GNSFeatures.Configured.DREAM_FLOWERS_5);
            GNSFeatures.addCarvers(generationBuilder);
            */
            BiomeGenerationSettings generation = generationBuilder.build();

            BiomeAmbience.Builder ambienceBuilder = new BiomeAmbience.Builder();
            ambienceBuilder.withGrassColor(0xffffff);
            ambienceBuilder.setWaterColor(waterColorIn);
            ambienceBuilder.setWaterFogColor(waterFogColorIn);
            ambienceBuilder.setFogColor(12638463);
            ambienceBuilder.withSkyColor(calculateSkyColor(tempIn));
            ambienceBuilder.setMoodSound(MoodSoundAmbience.DEFAULT_CAVE);
            BiomeAmbience ambience = ambienceBuilder.build();

            Biome.Builder biomeBuilder = new Biome.Builder();
            biomeBuilder.precipitation(Biome.RainType.NONE);
            biomeBuilder.category(Biome.Category.NONE);
            biomeBuilder.depth(depthIn);
            biomeBuilder.scale(scaleIn);
            biomeBuilder.temperature(tempIn);
            biomeBuilder.downfall(downfallIn);
            biomeBuilder.setEffects(ambience);
            biomeBuilder.withMobSpawnSettings(spawnInfo);
            biomeBuilder.withGenerationSettings(generation);

            return biomeBuilder.build();
        }

        private static int calculateSkyColor(float tempIn)
        {
            float lvt_1_1_ = tempIn / 3.0F;
            lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
            return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
        }
    }

}
