package org.trivait.blackwhitepalegarden;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.trivait.blackwhitepalegarden.modmenu.Config;

public class BlackWhitePaleGarden implements ClientModInitializer {
	public static Config CONFIG;

	@Override
	public void onInitializeClient() {
		MinecraftClient client = MinecraftClient.getInstance();

		AutoConfig.register(Config.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();

		PaleGardenUtil.init();
		PaleGardenColorUtil.init();
	}
}
