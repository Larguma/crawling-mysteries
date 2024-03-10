package larguma.crawling_mysteries.entity;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities implements AutoRegistryContainer<EntityType<?>> {

  public static final EntityType<EternalGuardianEntity> ETERNAL_GUARDIAN = FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, EternalGuardianEntity::new).dimensions(EntityDimensions.changing(1f, 3f)).build();

  @Override
  public Registry<EntityType<?>> getRegistry() {
    return Registries.ENTITY_TYPE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<EntityType<?>> getTargetFieldType() {
    return (Class<EntityType<?>>) (Object) EntityType.class;
  }

  @Override
  public void afterFieldProcessing() {
    FabricDefaultAttributeRegistry.register(ETERNAL_GUARDIAN, EternalGuardianEntity.setAttributes());
  }
}
