package larguma.crawling_mysteries.datagen;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

public class ModRecipeProvider extends FabricRecipeProvider {

  // private static final List<ItemConvertible> RUBY_SMELTABLE = List.of(ModItems.RUBY_ORE, ModItems.RAW_RUBY, ...);

  public ModRecipeProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generate(Consumer<RecipeJsonProvider> exporter) {
    // offerSmelting(exporter, RUBY_SMELTABLE, RecipeCategory.MISC, ModItem.RUBY, 0.77f, 200, "ruby");
    // offerBlasting(exporter, RUBY_SMELTABLE, RecipeCategory.MISC, ModItem.RUBY, 0.77f, 100, "ruby");

    // offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.RUBY, RecipeCategory.DECORATIONS, ModBlocks.RUBY_BLOCK);

    // ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MYSTERIOUS_AMULET, 1)
    //   .pattern("SSS")
    //   .pattern("SRS")
    //   .pattern("SSS")
    //   .input('S', Items.STONE)
    //   .input('R', ModItems.MYSTERIOUS_AMULET)
    //   .criterion(hasItem(ModItems.MYSTERIOUS_AMULET), conditionsFromItem(ModItems.MYSTERIOUS_AMULET))
    //   .offerTo(exporter, new Identifier(getRecipeName(ModItems.MYSTERIOUS_AMULET)));
  }

}
