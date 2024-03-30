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
  public static final String KEY_SPELL_KEY_SLOT_LEFT = "key.crawling-mysteries.spell_key_slot_left";
  public static final String KEY_SPELL_KEY_SLOT_TOP = "key.crawling-mysteries.spell_key_slot_top";
  public static final String KEY_SPELL_KEY_SLOT_RIGHT = "key.crawling-mysteries.spell_key_slot_right";
  public static final String KEY_SPELL_KEY_SLOT_BOTTOM = "key.crawling-mysteries.spell_key_slot_bottom";

  public static KeyBinding spellKeySlotLeft; // top left
  public static KeyBinding spellKeySlotTop; // top right
  public static KeyBinding spellKeySlotRight; // bottom left
  public static KeyBinding spellKeySlotBotton; // bottom right

  public static void registerKeyInputs() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (spellKeySlotLeft.wasPressed())
        if (client.player != null)
          ModMessages.CHANNEL.clientHandle().send(new KeycodePacket(KEY_SPELL_KEY_SLOT_LEFT));

      while (spellKeySlotTop.wasPressed())
        if (client.player != null)
          ModMessages.CHANNEL.clientHandle().send(new KeycodePacket(KEY_SPELL_KEY_SLOT_TOP));

      while (spellKeySlotRight.wasPressed())
        if (client.player != null)
          ModMessages.CHANNEL.clientHandle().send(new KeycodePacket(KEY_SPELL_KEY_SLOT_RIGHT));

      while (spellKeySlotBotton.wasPressed())
        if (client.player != null)
          ModMessages.CHANNEL.clientHandle().send(new KeycodePacket(KEY_SPELL_KEY_SLOT_BOTTOM));
    });
  }

  public static void register() {
    spellKeySlotLeft = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        KEY_SPELL_KEY_SLOT_LEFT,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_8,
        KEY_CATEGORY_CRAWLING_MYSTERIES));

    spellKeySlotTop = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        KEY_SPELL_KEY_SLOT_TOP,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_9,
        KEY_CATEGORY_CRAWLING_MYSTERIES));

    spellKeySlotRight = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        KEY_SPELL_KEY_SLOT_RIGHT,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_5,
        KEY_CATEGORY_CRAWLING_MYSTERIES));

    spellKeySlotBotton = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        KEY_SPELL_KEY_SLOT_BOTTOM,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_6,
        KEY_CATEGORY_CRAWLING_MYSTERIES));

    registerKeyInputs();
  }
}
