package dev.larguma.crawlingmysteries.block;

import java.util.List;
import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrawlingMysteries.MODID);

  private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
    DeferredBlock<T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
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
