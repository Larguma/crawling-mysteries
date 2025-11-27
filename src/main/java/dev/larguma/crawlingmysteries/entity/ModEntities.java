package dev.larguma.crawlingmysteries.entity;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
      .create(BuiltInRegistries.ENTITY_TYPE, CrawlingMysteries.MOD_ID);

  public static final Supplier<EntityType<EternalGuardianEntity>> ETERNAL_GUARDIAN = ENTITY_TYPES
      .register("eternal_guardian", () -> EntityType.Builder.<EternalGuardianEntity>of(
          EternalGuardianEntity::new, MobCategory.CREATURE).sized(1f, 3f).build("eternal_guardian"));

  public static void register(IEventBus eventBus) {
    ENTITY_TYPES.register(eventBus);
  }
}