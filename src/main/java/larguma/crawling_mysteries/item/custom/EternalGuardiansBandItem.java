package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.SoftOverride;

import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.TrinketItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public class EternalGuardiansBandItem extends TrinketItem {

  public EternalGuardiansBandItem() {
    super(new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(0).rarity(Rarity.RARE));
  }

  // #region Trinkets
  public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
    return DropRule.KEEP;
  }
  // #endregion

  // #region Base
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardians_band.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardians_band.tooltip.line2"));
    tooltip.add(Text.translatable("general.crawling-mysteries.tooltip.blank"));
    if (!CrawlingMysteries.CONFIG.enableTombstone())
      tooltip.add(Text.translatable("general.crawling-mysteries.tooltip.config_disabled"));
    super.appendTooltip(stack, world, tooltip, context);
  }
  // #endregion
}
