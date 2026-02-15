package org.trivait.blackwhitepalegarden.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.trivait.blackwhitepalegarden.PaleGardenUtil;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    protected abstract void setPostProcessor(Identifier id);

    @Shadow
    public abstract void clearPostProcessor();

    @Inject(method = "onCameraEntitySet", at = @At("TAIL"))
    private void change(Entity entity, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            if (PaleGardenUtil.showGrayScale) {
                this.setPostProcessor(Identifier.ofVanilla("grayscale"));
            } else {
                this.clearPostProcessor();
            }
        }
    }
}
