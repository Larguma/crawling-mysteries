package dev.larguma.crawlingmysteries.event;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
  public static final String KEY_CATEGORY_CRAWLING_MYSTERIES = "key.category.crawlingmysteries";

  public static final Lazy<KeyMapping> OPEN_SPELL_MENU = Lazy.of(() -> new KeyMapping(
      "key.crawlingmysteries.open_spell_menu",
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_G,
      KEY_CATEGORY_CRAWLING_MYSTERIES));

  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Post event) {
    while (OPEN_SPELL_MENU.get().consumeClick()) {
      // TODO: Open the spell menu GUI
    }
  }

  @SubscribeEvent
  public static void registerBindings(RegisterKeyMappingsEvent event) {
    event.register(OPEN_SPELL_MENU.get());
  }

}
