package dev.larguma.crawlingmysteries.spell;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class ModSpells {
  private static final Map<ResourceLocation, Spell> SPELLS = new LinkedHashMap<>();

  // Eternal Guardian Mask
  public static final Spell SPECTRAL_GAZE = register(
      Spell.create("spectral_gaze", "eternal_guardian_mask", 0, Spell.SPECTRAL_COLORS));

  // Cryptic Eye
  public static final Spell FEED_TOTEM = register(
      Spell.create("feed_totem", "cryptic_eye", 200, Spell.GOLDEN_COLORS, 1));
  public static final Spell BE_TOTEM = register(
      Spell.create("be_totem", "cryptic_eye", 6000, Spell.GOLDEN_COLORS, false, 2));

  public static Spell register(Spell spell) {
    ResourceLocation id = spell.getRegistryId();
    if (SPELLS.containsKey(id)) {
      CrawlingMysteries.LOGGER.warn("Duplicate spell registration: {}", id);
    }
    SPELLS.put(id, spell);
    return spell;
  }

  public static Optional<Spell> getSpell(ResourceLocation id) {
    return Optional.ofNullable(SPELLS.get(id));
  }

  public static Optional<Spell> getSpell(String id) {
    return getSpell(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id));
  }

  public static List<Spell> getAllSpells() {
    return List.copyOf(SPELLS.values());
  }

  public static List<Spell> getSpellsFromSource(ResourceLocation sourceItemId, int itemStage, boolean onlyShown) {
    return SPELLS.values().stream()
        .filter(spell -> spell.sourceItem().equals(sourceItemId)
            && (!onlyShown || spell.showOnWheel())
            && (spell.stage() == 0 || spell.stage() == itemStage))
        .toList();
  }

  @Nullable
  public static Spell getSpellFromStage(String sourceItemId, int stage) {
    for (Spell spell : SPELLS.values()) {
      if (spell.sourceItem().equals(
          ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, sourceItemId))
          && spell.stage() == stage) {
        return spell;
      }
    }
    return null;
  }

  /**
   * Get all available spells for the given player that are shown on the spell
   * wheel.
   */
  public static List<Spell> getAvailableSpells(Player player) {
    return getAvailableSpells(player, true);
  }

  /**
   * Get all available spells for the given player.
   */
  public static List<Spell> getAvailableSpells(Player player, boolean onlyShown) {
    List<Spell> available = new ArrayList<>();

    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isPresent()) {
      ICuriosItemHandler inventory = curiosHandler.get();

      for (SlotResult slotResult : inventory.findCurios(stack -> !stack.isEmpty())) {
        ItemStack stack = slotResult.stack();
        ResourceLocation itemId = stack.getItemHolder().getKey().location();
        int itemStage = stack.getOrDefault(ModDataComponents.SPELL_STAGE.get(), 0);
        available.addAll(getSpellsFromSource(itemId, itemStage, onlyShown));
      }
    }

    return available;
  }

  public static void init() {
    CrawlingMysteries.LOGGER.info("Registered {} spells", SPELLS.size());
  }
}
