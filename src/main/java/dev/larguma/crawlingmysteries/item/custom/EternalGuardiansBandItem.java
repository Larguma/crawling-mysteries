package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;

import dev.larguma.crawlingmysteries.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EternalGuardiansBandItem extends Item implements ICurioItem {

  public EternalGuardiansBandItem() {
    super(new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardians_band.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardians_band.tooltip.line2"));
    if (!Config.ENABLE_TOMBSTONE.get()) {
      tooltipComponents.add(Component.translatable("general.crawlingmysteries.tooltip.blank"));
      tooltipComponents.add(Component.translatable("general.crawlingmysteries.tooltip.config_disabled"));
    }
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  @Override
  public void curioTick(SlotContext slotContext, ItemStack stack) {
    ICurioItem.super.curioTick(slotContext, stack);
  }

}
