package larguma.crawling_mysteries.event;

import org.lwjgl.glfw.GLFW;

import larguma.crawling_mysteries.networking.ModMessages;
import larguma.crawling_mysteries.networking.packet.KeycodePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyInputHandler {
  public static final String KEY_CATEGORY_CRAWLING_MYSTERIES = "key.category.crawling-mysteries";
  public static final String KEY_ETERNAL_GUARDIAN_MASK_EFFECT = "key.crawling-mysteries.eternal_guardian_mask_effect";

  public static KeyBinding toggleEternalGuardianMaskEffect;

  public static void registerKeyInputs() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (toggleEternalGuardianMaskEffect.wasPressed())
        if (client.player != null)
          ModMessages.CHANNEL.clientHandle().send(new KeycodePacket(KEY_ETERNAL_GUARDIAN_MASK_EFFECT));
    });
  }

  public static void register() {
    toggleEternalGuardianMaskEffect = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        KEY_ETERNAL_GUARDIAN_MASK_EFFECT,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        KEY_CATEGORY_CRAWLING_MYSTERIES));

    registerKeyInputs();
  }
}
