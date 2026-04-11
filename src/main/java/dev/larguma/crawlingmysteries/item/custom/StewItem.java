package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class StewItem extends Item {
  private final String tooltipKey;
  
  public StewItem(Properties properties, String tooltipKey) {
    super(properties);
    this.tooltipKey = tooltipKey;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
    super.appendHoverText(stack, context, tooltip, flag);
  }
}
