package net.tantonb.dimtest.mixin;

import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.tantonb.dimtest.world.ModDimensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.IServerConfiguration;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow
    protected DynamicRegistries.Impl field_240767_f_;
    @Shadow
    protected IServerConfiguration serverConfig;

    /**
     * A good spot to retrieve world seed, etc. for custom dimensions.  Credit to Good Nights Sleep:
     *
     *    https://gitlab.com/modding-legacy/Good-Night-Sleep/-/blob/1.16.x/src/main/java/com/legacy/goodnightsleep/mixin/MinecraftServerMixin.java
     *
     * MinecraftServer#func_240800_l__
     */
    @Inject(at = @At("HEAD"), method = "func_240800_l__()V")
    private void initMinecraftServer(CallbackInfo callback) {

        long seed = this.serverConfig.getDimensionGeneratorSettings().getSeed();
        MutableRegistry<Biome> biomeRegistry = this.field_240767_f_.getRegistry(Registry.BIOME_KEY);
        MutableRegistry<DimensionSettings> dimensionSettingsRegistry = this.field_240767_f_.getRegistry(Registry.NOISE_SETTINGS_KEY);
        SimpleRegistry<Dimension> dimensionRegistry = this.serverConfig.getDimensionGeneratorSettings().func_236224_e_();

        ModDimensions.register(seed, biomeRegistry, dimensionSettingsRegistry, dimensionRegistry);
    }
}
