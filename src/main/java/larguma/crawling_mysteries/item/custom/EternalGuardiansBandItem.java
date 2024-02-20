package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.TrinketItem;
import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class EternalGuardiansBandItem extends TrinketItem {

  public EternalGuardiansBandItem(Settings settings) {
    super(settings);
  }
  
    // #region Trinkets
  @SuppressWarnings("null")
  public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
      LivingEntity entity, UUID uuid) {
    var modifiers = super.getModifiers(stack, slot, entity, uuid);
    // +10% movement speed
    modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        new EntityAttributeModifier(uuid, CrawlingMysteries.MOD_ID + ":movement_speed", 0.1,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    return modifiers;
  }

  public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
    return DropRule.KEEP;
  }
  // #endregion

  // #region Base
  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardians_band.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardians_band.tooltip.line2"));
    super.appendTooltip(stack, world, tooltip, context);
  }
  // #endregion
}
