package net.tantonb.dimtest.mixin;

import net.tantonb.dimtest.DimTestMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    /**
     * Allow experimental world warning to be suppressed, credit to henkelmax/advanced_mining_dimension:
     *
     *   https://github.com/henkelmax/advanced-mining-dimension/blob/1.16.4/src/main/java/de/maxhenkel/miningdimension/mixin/MinecraftMixin.java
     */

    @ModifyVariable(
            method = "loadWorld(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/Minecraft$WorldSelectionType;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft$WorldSelectionType;NONE:Lnet/minecraft/client/Minecraft$WorldSelectionType;", ordinal = 0),
            name = "flag1"
    )
    private boolean setFlag(boolean flag) {
        if (DimTestMod.CLIENT_CONFIG.showCustomWorldWarning.get()) {
            return flag;
        }
        return false;
    }

}