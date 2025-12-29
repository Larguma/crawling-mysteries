package dev.larguma.crawlingmysteries.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.larguma.crawlingmysteries.data.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class SmithingAwakeningRecipe extends SmithingTransformRecipe {

  final Ingredient template;
  final Ingredient base;
  final Ingredient addition;

  public SmithingAwakeningRecipe(Ingredient template, Ingredient base, Ingredient addition) {
    super(template, base, addition, ItemStack.EMPTY);
    this.template = template;
    this.base = base;
    this.addition = addition;
  }

  public Ingredient getBase() {
    return this.base;
  }

  @Override
  public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
    ItemStack base = input.base();
    if (base.isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack result = base.copy();
    result.setCount(1);
    result.set(ModDataComponents.GOOGLY_EYES, true);
    return result;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    ItemStack stack = this.base.getItems()[0].copy();
    stack.set(ModDataComponents.GOOGLY_EYES, true);
    return stack;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.SMITHING_AWAKENING.get();
  }

  public static class Serializer implements RecipeSerializer<SmithingAwakeningRecipe> {
    public static final MapCodec<SmithingAwakeningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
            Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
            Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition))
        .apply(instance, SmithingAwakeningRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmithingAwakeningRecipe> STREAM_CODEC = StreamCodec
        .composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.addition,
            SmithingAwakeningRecipe::new);

    @Override
    public MapCodec<SmithingAwakeningRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SmithingAwakeningRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
