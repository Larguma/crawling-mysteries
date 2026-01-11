package dev.larguma.crawlingmysteries.block;

import java.util.List;
import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.custom.*;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.custom.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrawlingMysteries.MOD_ID);

  // Custom
  public static final DeferredBlock<Block> TOMBSTONE = registerBlock("tombstone", () -> new TombstoneBlock());
  public static final DeferredBlock<Block> BEER_KEG = registerBlock("beer_keg", () -> new BeerKegBlock(),
      block -> new BeerKegItem(block, new Item.Properties()));
  public static final DeferredBlock<Block> BEER_MUG = registerBlock("beer_mug", () -> new BeerMugBlock(),
      block -> new BeerMugItem(block, new Item.Properties()));
  public static final DeferredBlock<Block> COOKING_ALTAR_TIER_1 = registerBlock("cooking_altar_tier_1",
      () -> new CookingAltarTier1Block(), block -> new CookingAltarTier1Item(block, new Item.Properties()));

  // Standard
  public static final DeferredBlock<Block> MYSTERIOUS_STONE = registerBlock("mysterious_stone",
      () -> new DropExperienceBlock(UniformInt.of(2, 5),
          Block.Properties.of().strength(3f).requiresCorrectToolForDrops()));

  private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
    DeferredBlock<T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
    return toReturn;
  }

  private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block,
      java.util.function.Function<T, Item> itemFactory) {
    DeferredBlock<T> toReturn = BLOCKS.register(name, block);
    ModItems.ITEMS.register(name, () -> itemFactory.apply(toReturn.get()));
    return toReturn;
  }

  private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
    ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
      public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
          TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("block.crawlingmysteries." + name + ".tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      };
    });
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
