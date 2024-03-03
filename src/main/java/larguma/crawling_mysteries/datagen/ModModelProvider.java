package larguma.crawling_mysteries.datagen;

import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;

public class ModModelProvider extends FabricModelProvider {

  public ModModelProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    blockStateModelGenerator.registerParentedItemModel(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG,
        ModelIds.getMinecraftNamespacedItem("template_spawn_egg"));
  }

  @Override
  public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    //itemModelGenerator.register(ModItems.ETERNAL_GUARDIANS_BAND, Models.GENERATED);
  }

}
