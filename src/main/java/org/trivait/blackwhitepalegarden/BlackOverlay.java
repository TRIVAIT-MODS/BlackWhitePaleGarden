package org.trivait.blackwhitepalegarden;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.DrawContext;

public class BlackOverlay {
    private static final int FADE_IN_TICKS = 10;  // 0.5 seconds
    private static final int FADE_OUT_TICKS = 60; // 3 seconds
    
    private static boolean active = false;
    private static int currentTick = 0;
    private static int maxTicks = 0;
    private static boolean fadingIn = true;
    
    public static void init() {
        HudRenderCallback.EVENT.register(BlackOverlay::render);
        ClientTickEvents.END_CLIENT_TICK.register(BlackOverlay::tick);
    }
    
    public static void startAnimation() {
        active = true;
        fadingIn = true;
        currentTick = 0;
        maxTicks = FADE_IN_TICKS + FADE_OUT_TICKS;
    }
    
    private static void tick(MinecraftClient client) {
        if (!active) return;
        
        currentTick++;
        
        if (currentTick >= FADE_IN_TICKS) {
            fadingIn = false;
        }
        
        if (currentTick >= maxTicks) {
            active = false;
        }
    }
    
    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!active) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        float alpha;
        if (fadingIn) {
            // Fade in: 0 -> 1 over FADE_IN_TICKS
            alpha = (float) currentTick / FADE_IN_TICKS;
        } else {
            // Fade out: 1 -> 0 over FADE_OUT_TICKS
            int fadeOutTick = currentTick - FADE_IN_TICKS;
            alpha = 1.0f - ((float) fadeOutTick / FADE_OUT_TICKS);
        }
        
        // Clamp alpha between 0 and 1
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Render black rectangle with calculated alpha
        int color = ((int) (alpha * 255) << 24) | 0x000000;
        drawContext.fill(0, 0, width, height, color);
    }
}
