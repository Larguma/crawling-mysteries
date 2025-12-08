package dev.larguma.crawlingmysteries.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
  public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister
      .createDataComponents(Registries.DATA_COMPONENT_TYPE, CrawlingMysteries.MOD_ID);

  // for command suggestions
  private static final Map<String, ComponentInfo<?>> COMPONENT_REGISTRY = new LinkedHashMap<>();

  // #region Common
  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ENABLED = register(
      "enabled",
      builder -> builder.persistent(Codec.BOOL),
      ComponentType.BOOLEAN, false);

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ATTUNEMENT = register(
      "attunement",
      builder -> builder.persistent(Codec.FLOAT),
      ComponentType.FLOAT, 0f);

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPELL_STAGE = register(
      "spell_stage",
      builder -> builder.persistent(Codec.INT),
      ComponentType.INTEGER, 0);
  // #endregion Common

  // Cryptic Eye
  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOTEMS_CONSUMED = register(
      "totems_consumed",
      builder -> builder.persistent(Codec.INT),
      ComponentType.INTEGER, 0);

  private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
      UnaryOperator<DataComponentType.Builder<T>> builderOperator, ComponentType type, T defaultValue) {
    DeferredHolder<DataComponentType<?>, DataComponentType<T>> holder = DATA_COMPONENT_TYPES.register(name,
        () -> builderOperator.apply(DataComponentType.builder()).build());
    COMPONENT_REGISTRY.put(name, new ComponentInfo<>(holder, type, defaultValue));
    return holder;
  }

  public static void register(IEventBus eventBus) {
    DATA_COMPONENT_TYPES.register(eventBus);
  }

  // #region Command Support

  public static Set<String> getComponentNames() {
    return COMPONENT_REGISTRY.keySet();
  }

  public static boolean hasComponent(String name) {
    return COMPONENT_REGISTRY.containsKey(name);
  }

  public static ComponentType getComponentType(String name) {
    ComponentInfo<?> info = COMPONENT_REGISTRY.get(name);
    return info != null ? info.type : null;
  }

  @SuppressWarnings("unchecked")
  public static boolean setComponentValue(ItemStack stack, String componentName, String valueStr) {
    ComponentInfo<?> info = COMPONENT_REGISTRY.get(componentName);
    if (info == null) {
      return false;
    }

    try {
      Object parsedValue = info.type.parse(valueStr);
      if (parsedValue == null) {
        return false;
      }

      DataComponentType<Object> componentType = (DataComponentType<Object>) info.holder.get();
      stack.set(componentType, parsedValue);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getComponentValue(ItemStack stack, String componentName) {
    ComponentInfo<?> info = COMPONENT_REGISTRY.get(componentName);
    if (info == null) {
      return null;
    }

    DataComponentType<T> componentType = (DataComponentType<T>) info.holder.get();
    T value = stack.get(componentType);
    return value != null ? value : (T) info.defaultValue;
  }

  // #endregion Command Support

  public enum ComponentType {
    BOOLEAN {
      @Override
      public Object parse(String value) {
        if ("true".equalsIgnoreCase(value)
            || "1".equals(value)
            || "yes".equalsIgnoreCase(value)
            || "y".equalsIgnoreCase(value))
          return true;
        if ("false".equalsIgnoreCase(value)
            || "0".equals(value)
            || "no".equalsIgnoreCase(value)
            || "n".equalsIgnoreCase(value))
          return false;
        return null;
      }

      @Override
      public String getTypeName() {
        return "boolean";
      }
    },
    INTEGER {
      @Override
      public Object parse(String value) {
        try {
          return Integer.parseInt(value);
        } catch (NumberFormatException e) {
          return null;
        }
      }

      @Override
      public String getTypeName() {
        return "integer";
      }
    },
    FLOAT {
      @Override
      public Object parse(String value) {
        try {
          return Float.parseFloat(value);
        } catch (NumberFormatException e) {
          return null;
        }
      }

      @Override
      public String getTypeName() {
        return "float";
      }
    },
    STRING {
      @Override
      public Object parse(String value) {
        return value;
      }

      @Override
      public String getTypeName() {
        return "string";
      }
    };

    public abstract Object parse(String value);

    public abstract String getTypeName();
  }

  private record ComponentInfo<T>(DeferredHolder<DataComponentType<?>, DataComponentType<T>> holder, ComponentType type,
      T defaultValue) {
  }
}
