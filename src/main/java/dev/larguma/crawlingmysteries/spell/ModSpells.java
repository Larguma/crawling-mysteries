package dev.larguma.crawlingmysteries.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

public class ModSpells {
  private static final Map<ResourceLocation, Spell> SPELLS = new HashMap<>();
  private static final List<Spell> SPELL_LIST = new ArrayList<>();

  public static final Spell SPECTRAL_GAZE = register(Spell.create(
      "spectral_gaze",
      "eternal_guardian_mask",
      0));

  private static Spell register(Spell spell) {
    ResourceLocation id = spell.getRegistryId();
    if (SPELLS.containsKey(id)) {
      CrawlingMysteries.LOGGER.warn("Duplicate spell registration: {}", id);
    }
    SPELLS.put(id, spell);
    SPELL_LIST.add(spell);
    return spell;
  }

  public static Optional<Spell> getSpell(ResourceLocation id) {
    return Optional.ofNullable(SPELLS.get(id));
  }

  public static Optional<Spell> getSpell(String id) {
    return getSpell(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id));
  }

  public static List<Spell> getAllSpells() {
    return List.copyOf(SPELL_LIST);
  }

  public static List<Spell> getSpellsFromSource(ResourceLocation sourceItemId) {
    return SPELL_LIST.stream()
        .filter(spell -> spell.sourceItem().equals(sourceItemId))
        .toList();
  }

  public static List<Spell> getAvailableSpells(Player player) {
    List<Spell> available = new ArrayList<>();

    var curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isPresent()) {
      var inventory = curiosHandler.get();

      var maskSlot = inventory.findFirstCurio(ModItems.ETERNAL_GUARDIAN_MASK.get());
      if (maskSlot.isPresent()) {
        available.addAll(getSpellsFromSource(
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "eternal_guardian_mask")));
      }
    }

    return available;
  }

  public static void init() {
    CrawlingMysteries.LOGGER.info("Registered {} spells", SPELL_LIST.size());
  }
}
