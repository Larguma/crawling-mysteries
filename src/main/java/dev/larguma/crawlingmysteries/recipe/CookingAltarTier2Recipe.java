package dev.larguma.crawlingmysteries.recipe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CookingAltarTier2Recipe implements Recipe<CookingAltarTier2RecipeInput> {

  private final NonNullList<Ingredient> inputs;
  private final ItemStack successOutput;
  private final ItemStack failureOutput;
  private final int cookingTime;

  public CookingAltarTier2Recipe(NonNullList<Ingredient> inputs, ItemStack successOutput, ItemStack failureOutput,
      int cookingTime) {
    this.inputs = inputs;
    this.successOutput = successOutput;
    this.failureOutput = failureOutput;
    this.cookingTime = cookingTime;
  }

  public NonNullList<Ingredient> getInputs() {
    return inputs;
  }

  public ItemStack getSuccessOutput() {
    return successOutput;
  }

  public ItemStack getFailureOutput() {
    return failureOutput;
  }

  public int getCookingTime() {
    return cookingTime;
  }

  @Override
  public boolean matches(CookingAltarTier2RecipeInput input, Level level) {
    List<ItemStack> nonEmpty = new ArrayList<>();
    for (int i = 0; i < input.size(); i++) {
      ItemStack stack = input.getItem(i);
      if (!stack.isEmpty()) {
        nonEmpty.add(stack);
      }
    }
    if (nonEmpty.size() != this.inputs.size()) {
      return false;
    }
    return matchInputs(nonEmpty, this.inputs, new boolean[nonEmpty.size()], 0);
  }

  private static boolean matchInputs(List<ItemStack> items, List<Ingredient> ingredients, boolean[] used, int index) {
    if (index >= ingredients.size()) {
      return true;
    }
    Ingredient ingredient = ingredients.get(index);
    for (int i = 0; i < items.size(); i++) {
      if (!used[i] && ingredient.test(items.get(i))) {
        used[i] = true;
        if (matchInputs(items, ingredients, used, index + 1)) {
          return true;
        }
        used[i] = false;
      }
    }
    return false;
  }

  @Override
  public ItemStack assemble(CookingAltarTier2RecipeInput input, HolderLookup.Provider registries) {
    return this.successOutput.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    return this.successOutput.copy();
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.inputs;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.COOKING_ALTAR_TIER_2.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.COOKING_ALTAR_TIER_2_TYPE.get();
  }

  public static class Serializer implements RecipeSerializer<CookingAltarTier2Recipe> {

    public static final MapCodec<CookingAltarTier2Recipe> RECORD_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("inputs").xmap(
                list -> {
                  NonNullList<Ingredient> nonNull = NonNullList.create();
                  nonNull.addAll(list);
                  return nonNull;
                },
                list -> list)
                .forGetter(CookingAltarTier2Recipe::getInputs),
            ItemStack.CODEC.fieldOf("success_output").forGetter(CookingAltarTier2Recipe::getSuccessOutput),
            ItemStack.CODEC.optionalFieldOf("failure_output", new ItemStack(Items.ROTTEN_FLESH))
                .forGetter(CookingAltarTier2Recipe::getFailureOutput),
            Codec.INT.optionalFieldOf("cooking_time", 200).forGetter(CookingAltarTier2Recipe::getCookingTime))
        .apply(instance, CookingAltarTier2Recipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CookingAltarTier2Recipe> STREAM_CODEC = StreamCodec
        .composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)),
            CookingAltarTier2Recipe::getInputs,
            ItemStack.STREAM_CODEC, CookingAltarTier2Recipe::getSuccessOutput,
            ItemStack.STREAM_CODEC, CookingAltarTier2Recipe::getFailureOutput,
            ByteBufCodecs.VAR_INT, CookingAltarTier2Recipe::getCookingTime,
            CookingAltarTier2Recipe::new);

    @Override
    public MapCodec<CookingAltarTier2Recipe> codec() {
      return RECORD_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CookingAltarTier2Recipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
