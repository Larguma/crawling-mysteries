package larguma.crawling_mysteries.entity;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

  public static final EntityType<EternalGuardianEntity> ETERNAL_GUARDIAN = registerMob("eternal_guardian",
      EternalGuardianEntity::new, 1f, 3f);

  public static <T extends MobEntity> EntityType<T> registerMob(String name, EntityType.EntityFactory<T> entity,
      float width, float height) {
    return Registry.register(Registries.ENTITY_TYPE,
        new Identifier(CrawlingMysteries.MOD_ID, name), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, entity)
            .dimensions(EntityDimensions.changing(width, height)).build());
  }
}
