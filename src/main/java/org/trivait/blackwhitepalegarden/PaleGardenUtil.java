package org.trivait.blackwhitepalegarden;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class PaleGardenUtil {
    private static boolean isPlayerInPaleGarden = false;
    private static boolean isNight = false;

    public static boolean showGrayScale = false;
    private static int tickCounter = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(PaleGardenUtil::tick);
        BlackOverlay.init();
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        tickCounter++;
        if (tickCounter < 10) return; // раз в 10 тиков проверяем условия
        tickCounter = 0;

        isPlayerInPaleGarden = isPlayerInPaleGarden(client);
        isNight = isNight(client);

        boolean needPale = BlackWhitePaleGarden.CONFIG.needPaleGarden;
        boolean needNight = BlackWhitePaleGarden.CONFIG.needNight;

        boolean shouldEnable =
                (!needPale || isPlayerInPaleGarden) &&
                        (!needNight || isNight);

        boolean shouldDisable =
                (needPale && !isPlayerInPaleGarden) ||
                        (needNight && !isNight);

        // включаем эффект
        if (!showGrayScale && shouldEnable) {
            showGrayScale = true;
            client.gameRenderer.onCameraEntitySet(client.player);
            if (BlackWhitePaleGarden.CONFIG.toggleAnimation) {
                startAnimation(); // fade-in
            }
            return;
        }

        // выключаем эффект
        if (showGrayScale && shouldDisable) {
            showGrayScale = false;
            client.gameRenderer.onCameraEntitySet(client.player);
            if (BlackWhitePaleGarden.CONFIG.toggleAnimation) {
                startAnimation(); // fade-out
            }
        }
    }

    public static boolean isNight(MinecraftClient client) {
        if (client.world == null) return false;

        ClientWorld world = client.world;
        long time = world.getTimeOfDay() % 24000;

        return time >= 12000 && time < 24000;
    }

    public static boolean isPlayerInPaleGarden(MinecraftClient client) {
        if (client.player == null || client.world == null) return false;

        var biomeEntry = client.world.getBiome(client.player.getBlockPos());
        var key = biomeEntry.getKey().orElse(null);
        if (key == null) return false;

        Identifier id = key.getValue();
        return id.getPath().contains("pale_garden");
    }

    private static void startAnimation() {
        BlackOverlay.startAnimation();
        
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.world != null) {
            client.player.playSound(
                    SoundEvents.ENTITY_CREAKING_SPAWN,
                    1.0f, 1.0f
            );

            for (int i = 0; i < 20; i++) {
                client.world.addParticle(
                        net.minecraft.particle.ParticleTypes.CLOUD,
                        client.player.getX() + (client.world.random.nextDouble() - 0.5),
                        client.player.getY() + 1,
                        client.player.getZ() + (client.world.random.nextDouble() - 0.5),
                        0, 0.05, 0
                );
            }
        }
    }
}
