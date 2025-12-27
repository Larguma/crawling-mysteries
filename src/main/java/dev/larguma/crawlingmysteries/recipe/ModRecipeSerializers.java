package dev.larguma.crawlingmysteries.recipe;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, CrawlingMysteries.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingAwakeningRecipe>> SMITHING_AWAKENING = 
        RECIPE_SERIALIZERS.register("smithing_awakening", SmithingAwakeningRecipe.Serializer::new);
}
