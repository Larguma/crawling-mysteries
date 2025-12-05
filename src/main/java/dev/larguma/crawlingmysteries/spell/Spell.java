package dev.larguma.crawlingmysteries.spell;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record Spell(String id, ResourceLocation icon, Component name, Component description,
    ResourceLocation sourceItem, int cooldownTicks, int[] colors) {

  // Default colors
  public static final int[] DEFAULT_COLORS = { 0x6B33D7, 0x9B59B6, 0x7D90FD };
  // Spectral-based - ghostly cyan/teal colors
  public static final int[] SPECTRAL_COLORS = { 0x00CED1, 0x40E0D0, 0x48D1CC, 0x7FFFD4 };
  // Fire-based spells - warm orange/red
  public static final int[] FIRE_COLORS = { 0xFF4500, 0xFF6347, 0xFFA500, 0xFFD700 };
  // Ice-based spells - cool blue/white
  public static final int[] ICE_COLORS = { 0x87CEEB, 0xADD8E6, 0xB0E0E6, 0xE0FFFF };
  // Nature-based spells - green tones
  public static final int[] NATURE_COLORS = { 0x32CD32, 0x90EE90, 0x98FB98, 0x7CFC00 };
  // Dark/shadow spells - purple/black tones
  public static final int[] SHADOW_COLORS = { 0x4B0082, 0x8B008B, 0x9400D3, 0x800080 };
  // Lightning spells - electric yellow/blue
  public static final int[] LIGHTNING_COLORS = { 0xFFFF00, 0xADD8E6, 0x87CEEB, 0xF0E68C };
  // Golden/holy spells - warm gold tones
  public static final int[] GOLDEN_COLORS = { 0xFFD700, 0xFFA500, 0xDAA520, 0xF0E68C };
  // Void/arcane spells - dark blue/purple
  public static final int[] VOID_COLORS = { 0x191970, 0x483D8B, 0x6A5ACD, 0x7B68EE };

  public static Spell create(String id, String sourceItemId, int cooldownTicks, int[] colors) {
    return new Spell(
        id,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/spell/" + id + ".png"),
        Component.translatable("spell.crawlingmysteries." + id),
        Component.translatable("spell.crawlingmysteries." + id + ".description"),
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, sourceItemId),
        cooldownTicks,
        colors);
  }

  public static Spell create(String id, String sourceItemId, int cooldownTicks) {
    return create(id, sourceItemId, cooldownTicks, DEFAULT_COLORS);
  }

  public int getPrimaryColor() {
    return colors.length > 0 ? colors[0] : 0x6B33D7;
  }

  public ResourceLocation getRegistryId() {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id);
  }
}
