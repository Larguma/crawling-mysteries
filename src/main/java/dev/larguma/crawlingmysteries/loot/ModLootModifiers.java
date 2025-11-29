package dev.larguma.crawlingmysteries.loot;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.loot.custom.AddItemModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
  public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister
      .create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CrawlingMysteries.MOD_ID);

  public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIER_SERIALIZERS
      .register("add_item", () -> AddItemModifier.CODEC);

  public static void register(IEventBus eventBus) {
    LOOT_MODIFIER_SERIALIZERS.register(eventBus);
  }
}
