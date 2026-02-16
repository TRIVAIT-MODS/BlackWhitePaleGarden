package org.trivait.blackwhitepalegarden.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private static void blackwhitepalegarden$forceColorWarmup(ClientWorld world) {
        if (world == null) return;

        BlockPos pos = BlockPos.ORIGIN;
        try {
            BiomeColors.getGrassColor(world, pos);
            BiomeColors.getFoliageColor(world, pos);
            BiomeColors.getWaterColor(world, pos);
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "setWorld", at = @At("TAIL"))
    private void blackwhitepalegarden$afterSetWorld(ClientWorld world, CallbackInfo ci) {
        blackwhitepalegarden$forceColorWarmup(world);
    }

    @Inject(method = "reload", at = @At("TAIL"))
    private void blackwhitepalegarden$afterReload(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        blackwhitepalegarden$forceColorWarmup(client.world);
    }
}
