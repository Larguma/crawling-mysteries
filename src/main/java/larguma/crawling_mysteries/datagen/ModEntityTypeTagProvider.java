package larguma.crawling_mysteries.datagen;

import java.util.concurrent.CompletableFuture;

import larguma.crawling_mysteries.entity.ModEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

  private static final TagKey<EntityType<?>> CAN_BREATHE_UNDER_WATER = of("can_breathe_under_water");

  public ModEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
    super(output, completableFuture);
  }

  @Override
  protected void configure(WrapperLookup arg) {
    getOrCreateTagBuilder(CAN_BREATHE_UNDER_WATER).add(ModEntities.ETERNAL_GUARDIAN);
  }

  private static TagKey<EntityType<?>> of(String id) {
    return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(id));
  }

}
