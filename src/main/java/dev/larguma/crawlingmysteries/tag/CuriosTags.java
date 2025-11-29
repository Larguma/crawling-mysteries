package dev.larguma.crawlingmysteries.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CuriosTags {
  public static class Items {
    public static final TagKey<Item> OBSERVER = createTagKey("observer");
    public static final TagKey<Item> MASK = createTagKey("mask");

    private static TagKey<Item> createTagKey(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", name));
    }
  }
}
