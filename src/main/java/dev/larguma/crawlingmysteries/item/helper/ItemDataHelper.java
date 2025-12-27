package dev.larguma.crawlingmysteries.item.helper;

import dev.larguma.crawlingmysteries.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

/**
 * Helper methods for managing item data components.
 */
public final class ItemDataHelper {

  // #region Enabled State

  /**
   * Gets the enabled state, defaults to false if not set.
   */
  public static boolean isEnabled(ItemStack stack) {
    if (!stack.has(ModDataComponents.ENABLED))
      stack.set(ModDataComponents.ENABLED, false);
    return stack.get(ModDataComponents.ENABLED);
  }

  /**
   * Sets the enabled state.
   */
  public static void setEnabled(ItemStack stack, boolean enabled) {
    stack.set(ModDataComponents.ENABLED, enabled);
  }

  /**
   * Toggles the enabled state.
   */
  public static void enabledToggle(ItemStack stack) {
    stack.set(ModDataComponents.ENABLED, !isEnabled(stack));
  }

  // #endregion Enabled State

  // #region Spell Stage

  /**
   * Gets the spell stage.
   * 
   * @return The spell stage, defaults to 0 if not set.
   */
  public static int getSpellStage(ItemStack stack) {
    if (!stack.has(ModDataComponents.SPELL_STAGE))
      stack.set(ModDataComponents.SPELL_STAGE, 0);
    return stack.get(ModDataComponents.SPELL_STAGE);
  }

  /**
   * Sets the spell stage, minimum 0.
   */
  public static int setSpellStage(ItemStack stack, int value) {
    if (value < 0)
      value = 0;
    return stack.set(ModDataComponents.SPELL_STAGE, value);
  }

  /**
   * Increases the spell stage by 1.
   */
  public static int nextSpellStage(ItemStack stack) {
    return setSpellStage(stack, getSpellStage(stack) + 1);
  }

  // #endregion Spell Stage

  // #region Attunement

  /**
   * Gets the attunement value.
   * 
   * @return The attunement value, defaults to 0 if not set.
   */
  public static float getAttunement(ItemStack stack) {
    if (!stack.has(ModDataComponents.ATTUNEMENT))
      stack.set(ModDataComponents.ATTUNEMENT, 0f);
    return stack.get(ModDataComponents.ATTUNEMENT);
  }

  /**
   * Sets the attunement value, clamped between 0 and 1.
   */
  public static float setAttunement(ItemStack stack, float value) {
    if (value < 0f)
      value = 0f;
    if (value > 1f)
      value = 1f;
    return stack.set(ModDataComponents.ATTUNEMENT, value);
  }

  /**
   * Adds to the current attunement value, clamped between 0 and 1.
   */
  public static float addAttunement(ItemStack stack, float value) {
    return setAttunement(stack, getAttunement(stack) + value);
  }

  // #endregion Attunement

  // #region Sentience

  /**
   * Checks if the item is sentient.
   */
  public static boolean isSentient(ItemStack stack) {
    if (!stack.has(ModDataComponents.GOOGLY_EYES))
      stack.set(ModDataComponents.GOOGLY_EYES, false);
    return stack.get(ModDataComponents.GOOGLY_EYES);
  }

  /**
   * Sets the sentience state of the item.
   */
  public static void setSentient(ItemStack stack, boolean sentient) {
    stack.set(ModDataComponents.GOOGLY_EYES, sentient);
  }

  // #endregion Sentience

}
