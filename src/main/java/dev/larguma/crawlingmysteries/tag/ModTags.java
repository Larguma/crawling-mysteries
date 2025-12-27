package dev.larguma.crawlingmysteries.tag;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
  public static class Blocks {
    
  }

  public static class Items {
    public static final TagKey<Item> OBSERVER = createTagKey("observer");
    public static final TagKey<Item> MASK = createTagKey("mask");
    public static final TagKey<Item> CAN_BE_SENTIENT = createTagKey("can_be_sentient");

    private static TagKey<Item> createTagKey(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name));
    }
  }
}
