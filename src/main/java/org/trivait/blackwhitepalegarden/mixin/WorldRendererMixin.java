package org.trivait.blackwhitepalegarden.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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
    private static final int BLACKWHITEPALEGARDEN_GRASS_COLOR = 0x0b6624;

    @Unique
    private static final int BLACKWHITEPALEGARDEN_FOLIAGE_COLOR = 0x0c5e22;

    @Unique
    private static final Identifier BLACKWHITEPALEGARDEN_PALE_GARDEN = Identifier.of("minecraft", "pale_garden");

    @Unique
    private static boolean blackwhitepalegarden$colorsRegistered = false;

    @Unique
    private static boolean blackwhitepalegarden$isPaleGarden(ClientWorld world, BlockPos pos) {
        if (world == null) return false;
        var key = world.getBiome(pos).getKey().orElse(null);
        if (key == null) return false;
        return key.getValue().equals(BLACKWHITEPALEGARDEN_PALE_GARDEN);
    }

    @Unique
    private static void blackwhitepalegarden$registerColorProviders() {
        if (blackwhitepalegarden$colorsRegistered) return;
        blackwhitepalegarden$colorsRegistered = true;

        var paleOakLeaves = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "pale_oak_leaves")).orElse(null);
        if (paleOakLeaves != null) {
            ColorProviderRegistry.BLOCK.register(
                    (state, world, pos, tintIndex) -> {
                        if (world instanceof ClientWorld clientWorld && pos != null && blackwhitepalegarden$isPaleGarden(clientWorld, pos)) {
                            return BLACKWHITEPALEGARDEN_FOLIAGE_COLOR;
                        }
                        return -1;
                    },
                    paleOakLeaves
            );
        }

        var grassBlock = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "grass_block")).orElse(null);
        var fern = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "fern")).orElse(null);
        var largeFern = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "large_fern")).orElse(null);
        var grass = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "grass")).orElse(null);
        var tallGrass = Registries.BLOCK.getOptionalValue(Identifier.of("minecraft", "tall_grass")).orElse(null);

        ColorProviderRegistry.BLOCK.register(
                (state, world, pos, tintIndex) -> {
                    if (world instanceof ClientWorld clientWorld && pos != null && blackwhitepalegarden$isPaleGarden(clientWorld, pos)) {
                        return BLACKWHITEPALEGARDEN_GRASS_COLOR;
                    }
                    return -1;
                },
                grassBlock, fern, largeFern, grass, tallGrass
        );
    }

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
        blackwhitepalegarden$registerColorProviders();
        blackwhitepalegarden$forceColorWarmup(world);
    }

    @Inject(method = "reload", at = @At("TAIL"))
    private void blackwhitepalegarden$afterReload(CallbackInfo ci) {
        blackwhitepalegarden$registerColorProviders();
        MinecraftClient client = MinecraftClient.getInstance();
        blackwhitepalegarden$forceColorWarmup(client.world);
    }
}
