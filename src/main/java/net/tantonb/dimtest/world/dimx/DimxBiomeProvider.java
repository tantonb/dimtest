package net.tantonb.dimtest.world.dimx;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.MaxMinNoiseMixer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.tantonb.dimtest.world.ModDimensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Notes
 *
 *  Extends net.minecraft.world.biome.provider.BiomeProvider
 */

public class DimxBiomeProvider extends BiomeProvider {

    public static final Logger LOGGER = LogManager.getLogger();

    private static final DimxBiomeProvider.Noise defaultNoise = new DimxBiomeProvider.Noise(-7, ImmutableList.of(1.0D, 1.0D));

    public static final MapCodec<DimxBiomeProvider> field_235262_e_ = RecordCodecBuilder.mapCodec((p_242602_0_) ->
    {
        return p_242602_0_.group(Codec.LONG.fieldOf("seed").forGetter((p_235286_0_) ->
        {
            return p_235286_0_.seed;
        }), RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create((p_235282_0_) ->
        {
            return p_235282_0_.group(Biome.Attributes.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.BIOME_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_235282_0_, Pair::of);
        }).listOf().fieldOf("biomes").forGetter((p_235284_0_) ->
        {
            return p_235284_0_.biomes;
        }), DimxBiomeProvider.Noise.field_242609_a.fieldOf("temperature_noise").forGetter((p_242608_0_) ->
        {
            return p_242608_0_.temperatureNoise;
        }), DimxBiomeProvider.Noise.field_242609_a.fieldOf("humidity_noise").forGetter((p_242607_0_) ->
        {
            return p_242607_0_.humidityNoise;
        }), DimxBiomeProvider.Noise.field_242609_a.fieldOf("altitude_noise").forGetter((p_242606_0_) ->
        {
            return p_242606_0_.altitudeNoise;
        }), DimxBiomeProvider.Noise.field_242609_a.fieldOf("weirdness_noise").forGetter((p_242604_0_) ->
        {
            return p_242604_0_.weirdnessNoise;
        })).apply(p_242602_0_, DimxBiomeProvider::new);
    });

    public static final Codec<DimxBiomeProvider> dimxProviderCodec = Codec.mapEither(DimxBuilder.field_242624_a, field_235262_e_).xmap((p_235277_0_) ->
    {
        return p_235277_0_.map(DimxBuilder::func_242635_d, Function.identity());
    }, (p_235275_0_) ->
    {
        return p_235275_0_.createDimxBuilder().map(Either::<DimxBuilder, DimxBiomeProvider>left).orElseGet(() ->
        {
            return Either.right(p_235275_0_);
        });
    }).codec();

    private final DimxBiomeProvider.Noise temperatureNoise;
    private final DimxBiomeProvider.Noise humidityNoise;
    private final DimxBiomeProvider.Noise altitudeNoise;
    private final DimxBiomeProvider.Noise weirdnessNoise;
    private final MaxMinNoiseMixer field_235264_g_;
    private final MaxMinNoiseMixer field_235265_h_;
    private final MaxMinNoiseMixer field_235266_i_;
    private final MaxMinNoiseMixer field_235267_j_;
    private final List<Pair<Biome.Attributes, Supplier<Biome>>> biomes;
    private final boolean field_235269_l_;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, DimxPreset>> biomePreset;

    public DimxBiomeProvider(
            long seedIn,
            List<Pair<Biome.Attributes, Supplier<Biome>>> biomesIn,
            Optional<Pair<Registry<Biome>, DimxPreset>> presetIn)
    {
        this(seedIn, biomesIn, defaultNoise, defaultNoise, defaultNoise, defaultNoise, presetIn);
    }

    public DimxBiomeProvider(
            long seedIn,
            List<Pair<Biome.Attributes, Supplier<Biome>>> biomesIn,
            DimxBiomeProvider.Noise tempNoiseIn,
            DimxBiomeProvider.Noise humidityNoiseIn,
            DimxBiomeProvider.Noise altitudeNoiseIn,
            DimxBiomeProvider.Noise weirdnessNoiseIn)
    {
        this(seedIn, biomesIn, tempNoiseIn, humidityNoiseIn, altitudeNoiseIn, weirdnessNoiseIn, Optional.empty());
    }

    public DimxBiomeProvider(
            long seedIn,
            List<Pair<Biome.Attributes, Supplier<Biome>>> biomesIn,
            DimxBiomeProvider.Noise tempNoiseIn,
            DimxBiomeProvider.Noise humidityNoiseIn,
            DimxBiomeProvider.Noise altitudeNoiseIn,
            DimxBiomeProvider.Noise weirdnessNoiseIn,
            Optional<Pair<Registry<Biome>, DimxPreset>> presetIn)
    {
        super(biomesIn.stream().map(Pair::getSecond));
        this.seed = seedIn;
        this.biomePreset = presetIn;
        this.temperatureNoise = tempNoiseIn;
        this.humidityNoise = humidityNoiseIn;
        this.altitudeNoise = altitudeNoiseIn;
        this.weirdnessNoise = weirdnessNoiseIn;
        this.field_235264_g_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seedIn), tempNoiseIn.func_242612_a(), tempNoiseIn.func_242614_b());
        this.field_235265_h_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seedIn + 1L), humidityNoiseIn.func_242612_a(), humidityNoiseIn.func_242614_b());
        this.field_235266_i_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seedIn + 2L), altitudeNoiseIn.func_242612_a(), altitudeNoiseIn.func_242614_b());
        this.field_235267_j_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seedIn + 3L), weirdnessNoiseIn.func_242612_a(), weirdnessNoiseIn.func_242614_b());
        this.biomes = biomesIn;
        this.field_235269_l_ = false;
    }

    protected Codec<? extends BiomeProvider> getBiomeProviderCodec()
    {
        return dimxProviderCodec;
    }

    @OnlyIn(Dist.CLIENT)
    public BiomeProvider getBiomeProvider(long seed)
    {
        return new DimxBiomeProvider(seed, this.biomes, this.temperatureNoise, this.humidityNoise, this.altitudeNoise, this.weirdnessNoise, this.biomePreset);
    }

    private Optional<DimxBuilder> createDimxBuilder()
    {
        return this.biomePreset.map((p_242601_1_) ->
        {
            return new DimxBuilder(p_242601_1_.getSecond(), p_242601_1_.getFirst(), this.seed);
        });
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        int i = this.field_235269_l_ ? y : 0;
        Biome.Attributes biome$attributes =
                new Biome.Attributes(
                        (float) this.field_235264_g_.func_237211_a_((double) x, (double) i, (double) z),
                        (float) this.field_235265_h_.func_237211_a_((double) x, (double) i, (double) z),
                        (float) this.field_235266_i_.func_237211_a_((double) x, (double) i, (double) z),
                        (float) this.field_235267_j_.func_237211_a_((double) x, (double) i, (double) z),
                        0.0F);
        return this.biomes.stream().min(Comparator.comparing((attributeBiomePair) ->
        {
            return attributeBiomePair.getFirst().getAttributeDifference(biome$attributes);
        })).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
    }

    public boolean func_235280_b_(long p_235280_1_)
    {
        return this.seed == p_235280_1_ && this.biomePreset.isPresent() && Objects.equals(this.biomePreset.get().getSecond(), DimxPreset.dimxPreset);
    }

    static final class DimxBuilder
    {
        public static final MapCodec<DimxBuilder> field_242624_a =
            RecordCodecBuilder.mapCodec(
                (p_242630_0_) -> {
                    return p_242630_0_.group(
                        ResourceLocation.CODEC.flatXmap(
                            (location) -> {
                                return Optional.ofNullable(DimxPreset.biomeMap.get(location)).map(DataResult::success).orElseGet(() ->
                                {
                                    return DataResult.error("Unknown preset: " + location);
                                });
                            }, (p_242629_0_) -> {
                                return DataResult.success(p_242629_0_.getName());
                            }
                        ).fieldOf("preset").stable().forGetter(DimxBuilder::func_242628_a),
                        RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(DimxBuilder::func_242632_b),
                        Codec.LONG.fieldOf("seed").stable().forGetter(DimxBuilder::func_242634_c)
                    ).apply(p_242630_0_, p_242630_0_.stable(DimxBuilder::new));
                }
            );
        private final DimxPreset field_242625_b;
        private final Registry<Biome> field_242626_c;
        private final long field_242627_d;

        private DimxBuilder(DimxPreset p_i241956_1_, Registry<Biome> p_i241956_2_, long p_i241956_3_)
        {
            this.field_242625_b = p_i241956_1_;
            this.field_242626_c = p_i241956_2_;
            this.field_242627_d = p_i241956_3_;
        }

        public DimxPreset func_242628_a()
        {
            return this.field_242625_b;
        }

        public Registry<Biome> func_242632_b()
        {
            return this.field_242626_c;
        }

        public long func_242634_c()
        {
            return this.field_242627_d;
        }

        public DimxBiomeProvider func_242635_d()
        {
            return this.field_242625_b.func_242619_a(this.field_242626_c, this.field_242627_d);
        }
    }

    static class Noise
    {
        private final int field_242610_b;
        private final DoubleList field_242611_c;
        public static final Codec<DimxBiomeProvider.Noise> field_242609_a =
            RecordCodecBuilder.create((p_242613_0_) -> {
                return p_242613_0_.group(
                        Codec.INT.fieldOf("firstOctave").forGetter(DimxBiomeProvider.Noise::func_242612_a),
                        Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(DimxBiomeProvider.Noise::func_242614_b)
                ).apply(p_242613_0_, DimxBiomeProvider.Noise::new);
            });

        public Noise(int p_i241954_1_, List<Double> p_i241954_2_)
        {
            this.field_242610_b = p_i241954_1_;
            this.field_242611_c = new DoubleArrayList(p_i241954_2_);
        }

        public int func_242612_a()
        {
            return this.field_242610_b;
        }

        public DoubleList func_242614_b()
        {
            return this.field_242611_c;
        }
    }

    public static class DimxPreset
    {
        private static final Map<ResourceLocation, DimxPreset> biomeMap = Maps.newHashMap();
        public static final DimxPreset dimxPreset =
            new DimxPreset(ModDimensions.DIMX_ID, (preset, biomeList, seedIn) ->
                {
                    return new DimxBiomeProvider(
                        seedIn,
                        ImmutableList.of(
                            Pair.of(
                                new Biome.Attributes(0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                () -> {
                                    return biomeList.getOrThrow(DimxBiomes.Keys.HILLS);
                                })
                        ),
                        Optional.of(Pair.of(biomeList, preset))
                    );
                });
        private final ResourceLocation field_235290_d_;
        private final Function3<DimxPreset, Registry<Biome>, Long, DimxBiomeProvider> field_235291_e_;

        public DimxPreset(ResourceLocation p_i241955_1_, Function3<DimxPreset, Registry<Biome>, Long, DimxBiomeProvider> p_i241955_2_)
        {
            this.field_235290_d_ = p_i241955_1_;
            this.field_235291_e_ = p_i241955_2_;
            biomeMap.put(p_i241955_1_, this);
        }

        public DimxBiomeProvider func_242619_a(Registry<Biome> p_242619_1_, long p_242619_2_)
        {
            return this.field_235291_e_.apply(this, p_242619_1_, p_242619_2_);
        }

        public ResourceLocation getName()
        {
            return field_235290_d_;
        }
    }
}
