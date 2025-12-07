package dev.larguma.crawlingmysteries.data;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
  public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister
      .createDataComponents(Registries.DATA_COMPONENT_TYPE, CrawlingMysteries.MOD_ID);

  // #region Common
  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ENABLED = register(
      "enabled",
      builder -> builder.persistent(Codec.BOOL));

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ATTUNEMENT = register(
      "attunement",
      builder -> builder.persistent(Codec.FLOAT));

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPELL_STAGE = register(
      "spell_stage",
      builder -> builder.persistent(Codec.INT));
  // #endregion Common

  // Cryptic Eye
  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOTEMS_CONSUMED = register(
      "totems_consumed",
      builder -> builder.persistent(Codec.INT));

  private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
      UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
    return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
  }

  public static void register(IEventBus eventBus) {
    DATA_COMPONENT_TYPES.register(eventBus);
  }
}
