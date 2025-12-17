package dev.larguma.crawlingmysteries.client.event;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.screen.CrypticCodexScreen;
import dev.larguma.crawlingmysteries.client.screen.SpellSelectMenuScreen;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class KeyMappingsEvents {
  public static final String KEY_CATEGORY_CRAWLING_MYSTERIES = "key.category.crawlingmysteries";

  public static final Lazy<KeyMapping> OPEN_SPELL_MENU = Lazy.of(() -> new KeyMapping(
      "key.crawlingmysteries.open_spell_menu",
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_V,
      KEY_CATEGORY_CRAWLING_MYSTERIES));

  public static final Lazy<KeyMapping> OPEN_CODEX = Lazy.of(() -> new KeyMapping(
      "key.crawlingmysteries.open_codex",
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_H,
      KEY_CATEGORY_CRAWLING_MYSTERIES));

  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Post event) {
    Minecraft minecraft = Minecraft.getInstance();
    while (OPEN_SPELL_MENU.get().consumeClick()) {
      if (minecraft.player != null && minecraft.screen == null) {
        minecraft.setScreen(new SpellSelectMenuScreen());
      }
    }

    while (OPEN_CODEX.get().consumeClick()) {
      if (minecraft.player != null && minecraft.screen == null) {
        if (ItemHelper.hasItem(minecraft.player, ModItems.CRYPTIC_EYE.get())) {
          minecraft.setScreen(new CrypticCodexScreen());
        }
      }
    }
  }

  @SubscribeEvent
  public static void registerBindings(RegisterKeyMappingsEvent event) {
    event.register(OPEN_SPELL_MENU.get());
    event.register(OPEN_CODEX.get());
  }

}
