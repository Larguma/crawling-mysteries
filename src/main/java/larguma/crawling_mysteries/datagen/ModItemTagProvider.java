package larguma.crawling_mysteries.datagen;

import java.util.concurrent.CompletableFuture;

import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

  public static final TagKey<Item> SPELL_ITEMS = of("spell_items");

  public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
    super(output, completableFuture);
  }

  @Override
  protected void configure(WrapperLookup arg) {
    getOrCreateTagBuilder(SPELL_ITEMS)
        .add(ModItems.ETERNAL_GUARDIAN_MASK);
  }

  private static TagKey<Item> of(String id) {
    return TagKey.of(RegistryKeys.ITEM, new Identifier(id));
  }

}
