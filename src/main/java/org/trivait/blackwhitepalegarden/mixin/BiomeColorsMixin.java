package org.trivait.blackwhitepalegarden.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    @Unique
    private static final int BLACKWHITEPALEGARDEN_GRASS_COLOR = 0x0b6624;

    @Unique
    private static final int BLACKWHITEPALEGARDEN_FOLIAGE_COLOR = 0x0c5e22;

    @Unique
    private static final Identifier BLACKWHITEPALEGARDEN_PALE_GARDEN = Identifier.of("minecraft", "pale_garden");

    @Unique
    private static boolean blackwhitepalegarden$isPaleGarden(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || pos == null) return false;
        
        var biomeEntry = client.world.getBiome(pos);
        var key = biomeEntry.getKey().orElse(null);
        if (key == null) return false;
        return key.getValue().equals(BLACKWHITEPALEGARDEN_PALE_GARDEN);
    }

    @Inject(method = "getGrassColor", at = @At("HEAD"), cancellable = true)
    private static void onGetGrassColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (blackwhitepalegarden$isPaleGarden(pos)) {
            cir.setReturnValue(BLACKWHITEPALEGARDEN_GRASS_COLOR);
        }
    }

    @Inject(method = "getFoliageColor", at = @At("HEAD"), cancellable = true)
    private static void onGetFoliageColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (blackwhitepalegarden$isPaleGarden(pos)) {
            cir.setReturnValue(BLACKWHITEPALEGARDEN_FOLIAGE_COLOR);
        }
    }
}
