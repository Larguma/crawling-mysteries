package larguma.crawling_mysteries.datagen;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {

  public ModRecipeProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generate(RecipeExporter exporter) {
    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.ETERNAL_GUARDIAN_MASK, 1)
        .pattern("lhl")
        .input('l', Items.LEATHER)
        .input('h', ModItems.ETERNAL_GUARDIAN_HEAD)
        .criterion(FabricRecipeProvider.hasItem(Items.LEATHER),
            FabricRecipeProvider.conditionsFromItem(Items.LEATHER))
        .criterion(FabricRecipeProvider.hasItem(ModItems.ETERNAL_GUARDIAN_HEAD),
            FabricRecipeProvider.conditionsFromItem(ModItems.ETERNAL_GUARDIAN_HEAD))
        .offerTo(exporter, new Identifier(CrawlingMysteries.MOD_ID, "eternal_guardian_mask"));
  }
}
