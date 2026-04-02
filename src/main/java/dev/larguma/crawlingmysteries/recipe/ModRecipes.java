package dev.larguma.crawlingmysteries.recipe;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
      .create(Registries.RECIPE_SERIALIZER, CrawlingMysteries.MOD_ID);

  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
      CrawlingMysteries.MOD_ID);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingAwakeningRecipe>> SMITHING_AWAKENING = SERIALIZERS
      .register("smithing_awakening", SmithingAwakeningRecipe.Serializer::new);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindstoneGrindRecipe>> GRINDSTONE_GRIND = SERIALIZERS
      .register("grindstone_grind", GrindstoneGrindRecipe.Serializer::new);

  public static final DeferredHolder<RecipeType<?>, RecipeType<GrindstoneGrindRecipe>> GRINDSTONE_GRIND_TYPE = RECIPE_TYPES
      .register("grindstone_grind", () -> new RecipeType<GrindstoneGrindRecipe>() { // NOSONAR
        @Override
        public String toString() {
          return CrawlingMysteries.MOD_ID + ":grindstone_grind";
        }
      });

  public static void register(IEventBus eventBus) {
    SERIALIZERS.register(eventBus);
    RECIPE_TYPES.register(eventBus);
  }
}
