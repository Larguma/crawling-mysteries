package dev.larguma.crawlingmysteries.loot.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class AddItemModifier extends LootModifier {
  public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder
      .mapCodec(inst -> LootModifier.codecStart(inst)
          .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(e -> e.item))
          .and(Codec.FLOAT.fieldOf("chance").forGetter(e -> e.chance))
          .and(DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
              .forGetter(e -> e.components))
          .apply(inst, AddItemModifier::new));

  private final Item item;
  private final float chance;
  private final DataComponentPatch components;

  public AddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance) {
    this(conditionsIn, item, chance, DataComponentPatch.EMPTY);
  }

  public AddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance, DataComponentPatch components) {
    super(conditionsIn);
    this.item = item;
    this.chance = chance;
    this.components = components;
  }

  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
    for (LootItemCondition condition : this.conditions) {
      if (!condition.test(lootContext)) {
        return generatedLoot;
      }
    }
    if (lootContext.getRandom().nextFloat() <= this.chance) {
      ItemStack stack = new ItemStack(this.item);
      stack.applyComponents(this.components);
      generatedLoot.add(stack);
    }
    return generatedLoot;
  }

  @Override
  public MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }

}