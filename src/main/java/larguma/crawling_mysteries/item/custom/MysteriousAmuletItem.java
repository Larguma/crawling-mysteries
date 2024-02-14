package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class MysteriousAmuletItem extends TrinketItem {

  public MysteriousAmuletItem(Settings settings) {
    super(settings);
  }

  public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
      LivingEntity entity, UUID uuid) {
    var modifiers = super.getModifiers(stack, slot, entity, uuid);
    // +10% movement speed
    modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        new EntityAttributeModifier(uuid, CrawlingMysteries.MOD_ID + ":movement_speed", 0.1,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    // If the player has access to ring slots, this will give them an extra one
    SlotAttributes.addSlotModifier(modifiers, "hand/ring", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
    return modifiers;
  }

  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling_mysteries.mysterious_amulet.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling_mysteries.mysterious_amulet.tooltip.line2")
        .formatted(Formatting.ITALIC, Formatting.LIGHT_PURPLE));
    super.appendTooltip(stack, world, tooltip, context);
  }
}
