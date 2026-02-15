package org.trivait.blackwhitepalegarden;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.util.Identifier;

public class PaleGardenColorUtil {
    public static void init() {

        BiomeModifications.create(Identifier.of("blackwhitepalegarden", "modify_colors"))
                .add(
                        ModificationPhase.POST_PROCESSING,

                        (BiomeSelectionContext ctx) -> {
                            Identifier id = ctx.getBiomeKey().getValue();
                            return id.equals(Identifier.of("minecraft", "pale_garden"));
                        },

                        (BiomeSelectionContext ctx, BiomeModificationContext mod) -> {
                            BiomeModificationContext.EffectsContext effects = mod.getEffects();

                            effects.setGrassColor(0x0b6624);
                            effects.setFoliageColor(0x0c5e22);
                            effects.setFogColor(0xC0D8FF);
                            effects.setSkyColor(0x78A7FF);
                        }
                );
    }
}
