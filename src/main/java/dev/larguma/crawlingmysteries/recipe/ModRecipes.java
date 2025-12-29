package dev.larguma.crawlingmysteries.recipe;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
      .create(Registries.RECIPE_SERIALIZER, CrawlingMysteries.MOD_ID);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingAwakeningRecipe>> SMITHING_AWAKENING = SERIALIZERS
      .register("smithing_awakening", SmithingAwakeningRecipe.Serializer::new);

  public static void register(IEventBus eventBus) {
    SERIALIZERS.register(eventBus);
  }
}
