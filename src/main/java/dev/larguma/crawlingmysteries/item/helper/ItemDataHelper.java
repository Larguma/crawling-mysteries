package dev.larguma.crawlingmysteries.item.helper;

import dev.larguma.crawlingmysteries.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

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

}
