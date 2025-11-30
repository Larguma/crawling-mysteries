package dev.larguma.crawlingmysteries.spell;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record Spell(String id, ResourceLocation icon, Component name, Component description,
    ResourceLocation sourceItem, int cooldownTicks) {

  public static Spell create(String id, String sourceItemId, int cooldownTicks) {
    return new Spell(
        id,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/spell/" + id + ".png"),
        Component.translatable("spell.crawlingmysteries." + id),
        Component.translatable("spell.crawlingmysteries." + id + ".description"),
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, sourceItemId),
        cooldownTicks);
  }

  public ResourceLocation getRegistryId() {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id);
  }
}
