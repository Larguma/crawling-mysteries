package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;

import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class LuckyHorseshoe extends Item implements ICurioItem {

  public LuckyHorseshoe() {
    super(new Item.Properties().stacksTo(1).component(ModDataComponents.HORSESHOE_TIER, new HorseshoeDataComponent(1)));
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.lucky_horseshoe.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.lucky_horseshoe.tooltip.line2"));

    tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
    if (Screen.hasShiftDown()) {
      HorseshoeDataComponent component = stack.get(ModDataComponents.HORSESHOE_TIER.get());
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.lucky_horseshoe.tooltip.luck",
          String.format("%.0f", component.getFallReduction() * 100)).withStyle(ChatFormatting.GOLD));
    } else {
      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.press_shift"));
    }

    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  public static ItemStack createWithTier(int tier) {
    ItemStack stack = new ItemStack(ModItems.LUCKY_HORSESHOE.get());
    stack.set(ModDataComponents.HORSESHOE_TIER.get(), new HorseshoeDataComponent(tier));
    return stack;
  }
}