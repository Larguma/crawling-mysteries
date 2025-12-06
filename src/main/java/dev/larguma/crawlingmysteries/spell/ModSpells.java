package dev.larguma.crawlingmysteries.spell;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
      Spell.create("feed_totem", "cryptic_eye", 1, Spell.GOLDEN_COLORS));
  public static final Spell BE_TOTEM = register(
      Spell.create("be_totem", "cryptic_eye", 200, Spell.GOLDEN_COLORS, false));

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

  public static List<Spell> getSpellsFromSource(ResourceLocation sourceItemId, boolean onlyShown) {
    return SPELLS.values().stream()
        .filter(spell -> spell.sourceItem().equals(sourceItemId) && (!onlyShown || spell.showOnWheel())).toList();
  }

  public static List<Spell> getAvailableSpells(Player player) {
    return getAvailableSpells(player, true);
  }

  public static List<Spell> getAvailableSpells(Player player, boolean onlyShown) {
    List<Spell> available = new ArrayList<>();

    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isPresent()) {
      ICuriosItemHandler inventory = curiosHandler.get();

      for (SlotResult slotResult : inventory.findCurios(stack -> !stack.isEmpty())) {
        ResourceLocation itemId = slotResult.stack().getItemHolder().getKey().location();
        available.addAll(getSpellsFromSource(itemId, onlyShown));
      }
    }

    return available;
  }

  public static void init() {
    CrawlingMysteries.LOGGER.info("Registered {} spells", SPELLS.size());
  }
}
