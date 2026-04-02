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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A recipe for grindstone grinding interactions.
 * Right-clicking a grindstone with the input item has a chance to produce
 * either
 * the success output or the failure output.
 */
public class GrindstoneGrindRecipe implements Recipe<GrindstoneGrindRecipeInput> {

  private final Ingredient input;
  private final ItemStack successOutput;
  private final ItemStack failureOutput;
  private final float successChance;

  public GrindstoneGrindRecipe(Ingredient input, ItemStack successOutput, ItemStack failureOutput,
      float successChance) {
    this.input = input;
    this.successOutput = successOutput;
    this.failureOutput = failureOutput;
    this.successChance = successChance;
  }

  public Ingredient getInput() {
    return input;
  }

  public ItemStack getSuccessOutput() {
    return successOutput;
  }

  public ItemStack getFailureOutput() {
    return failureOutput;
  }

  public float getSuccessChance() {
    return successChance;
  }

  @Override
  public boolean matches(GrindstoneGrindRecipeInput input, Level level) {
    return this.input.test(input.item());
  }

  @Override
  public ItemStack assemble(GrindstoneGrindRecipeInput input, HolderLookup.Provider registries) {
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
    NonNullList<Ingredient> list = NonNullList.create();
    list.add(this.input);
    return list;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.GRINDSTONE_GRIND.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.GRINDSTONE_GRIND_TYPE.get();
  }

  public static class Serializer implements RecipeSerializer<GrindstoneGrindRecipe> {

    public static final MapCodec<GrindstoneGrindRecipe> RECORD_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
            ItemStack.CODEC.fieldOf("success_output").forGetter(r -> r.successOutput),
            ItemStack.CODEC.fieldOf("failure_output").forGetter(r -> r.failureOutput),
            Codec.floatRange(0.0F, 1.0F).fieldOf("success_chance").forGetter(r -> r.successChance))
        .apply(instance, GrindstoneGrindRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrindstoneGrindRecipe> STREAM_CODEC = StreamCodec
        .composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
            ItemStack.STREAM_CODEC, r -> r.successOutput,
            ItemStack.STREAM_CODEC, r -> r.failureOutput,
            ByteBufCodecs.FLOAT, r -> r.successChance,
            GrindstoneGrindRecipe::new);

    @Override
    public MapCodec<GrindstoneGrindRecipe> codec() {
      return RECORD_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, GrindstoneGrindRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
