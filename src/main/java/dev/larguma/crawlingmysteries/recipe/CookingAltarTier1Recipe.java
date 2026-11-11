package dev.larguma.crawlingmysteries.recipe;

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

public class CookingAltarTier1Recipe implements Recipe<CookingAltarTier1RecipeInput> {

  private final Ingredient input;
  private final Ingredient container;
  private final ItemStack result;
  private final int cookingTime;

  public CookingAltarTier1Recipe(Ingredient input, Ingredient container, ItemStack result, int cookingTime) {
    this.input = input;
    this.container = container;
    this.result = result;
    this.cookingTime = cookingTime;
  }

  public Ingredient getInput() {
    return input;
  }

  public Ingredient getContainer() {
    return container;
  }

  public ItemStack getResult() {
    return result;
  }

  public int getCookingTime() {
    return cookingTime;
  }

  @Override
  public boolean matches(CookingAltarTier1RecipeInput input, Level level) {
    return this.input.test(input.item());
  }

  @Override
  public ItemStack assemble(CookingAltarTier1RecipeInput input, HolderLookup.Provider registries) {
    return this.result.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    return this.result.copy();
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> list = NonNullList.create();
    list.add(this.input);
    list.add(this.container);
    return list;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.COOKING_ALTAR_TIER_1.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.COOKING_ALTAR_TIER_1_TYPE.get();
  }

  public static class Serializer implements RecipeSerializer<CookingAltarTier1Recipe> {

    public static final MapCodec<CookingAltarTier1Recipe> RECORD_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            Ingredient.CODEC.fieldOf("input").forGetter(CookingAltarTier1Recipe::getInput),
            Ingredient.CODEC.optionalFieldOf("container", Ingredient.of(Items.BOWL))
                .forGetter(CookingAltarTier1Recipe::getContainer),
            ItemStack.CODEC.fieldOf("result").forGetter(CookingAltarTier1Recipe::getResult),
            Codec.INT.optionalFieldOf("cooking_time", 100).forGetter(CookingAltarTier1Recipe::getCookingTime))
        .apply(instance, CookingAltarTier1Recipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CookingAltarTier1Recipe> STREAM_CODEC = StreamCodec
        .composite(
            Ingredient.CONTENTS_STREAM_CODEC, CookingAltarTier1Recipe::getInput,
            Ingredient.CONTENTS_STREAM_CODEC, CookingAltarTier1Recipe::getContainer,
            ItemStack.STREAM_CODEC, CookingAltarTier1Recipe::getResult,
            ByteBufCodecs.VAR_INT, CookingAltarTier1Recipe::getCookingTime,
            CookingAltarTier1Recipe::new);

    @Override
    public MapCodec<CookingAltarTier1Recipe> codec() {
      return RECORD_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CookingAltarTier1Recipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
