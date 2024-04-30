package larguma.crawling_mysteries.util;

import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.spell.EternalGuardianMaskSpellEffect;
import larguma.crawling_mysteries.spell.ISpellEffect;
import net.minecraft.item.ItemStack;

public class SpellUtils {

  private enum Spell {
    ETERNAL_GUARDIAN_MASK
  }

  public static ISpellEffect getSpellEffectFromInt(int i) {
    if (i == Spell.ETERNAL_GUARDIAN_MASK.ordinal()) {
      return new EternalGuardianMaskSpellEffect();
    }

    return null;
  }

  public static int getSpellId(ItemStack cursorStack) {
    if (cursorStack.getItem() == ModItems.ETERNAL_GUARDIAN_MASK) {
      return Spell.ETERNAL_GUARDIAN_MASK.ordinal();
    }

    return -1;
  }
}
