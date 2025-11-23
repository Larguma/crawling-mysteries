package dev.larguma.crawlingmysteries.mixin;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.larguma.crawlingmysteries.Config;
import dev.larguma.crawlingmysteries.block.custom.TombstoneBlock;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
  @Shadow
  @Final
  private Inventory inventory;
  private boolean hasEternalGuardiansBand = false;

  protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(at = @At("HEAD"), method = "dropEquipment", cancellable = true)
  private void dropEquipment(CallbackInfo info) {
    if (Config.ENABLE_TOMBSTONE.get()) {
      Player player = (Player) (Object) this;
      boolean keepInv = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
      List<ItemStack> trinketStacks = new ArrayList<>();

      CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
        hasEternalGuardiansBand = curiosInventory.isEquipped(ModItems.ETERNAL_GUARDIANS_BAND.get());
        curiosInventory.getCurios().forEach((ref, stacksHandler) -> { // stacksHandler = all head or all rings
          var dropRule = stacksHandler.getDropRule();
          var activeStacks = stacksHandler.getStacks();
          var cosmeticStacks = stacksHandler.getCosmeticStacks();

          for (int i = 0; i < activeStacks.getSlots(); i++) {
            ItemStack stack = activeStacks.getStackInSlot(i);

            if (!stack.isEmpty()) {
              if (!EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)
                  && dropRule != DropRule.DESTROY && dropRule != DropRule.ALWAYS_KEEP && !keepInv) {
                trinketStacks.add(stack);
                activeStacks.setStackInSlot(i, ItemStack.EMPTY);
              }
              if (dropRule == DropRule.DESTROY) {
                activeStacks.setStackInSlot(i, ItemStack.EMPTY);
              }
            }
          }

          for (int i = 0; i < cosmeticStacks.getSlots(); i++) {
            ItemStack stack = cosmeticStacks.getStackInSlot(i);

            if (!stack.isEmpty()) {
              if (!EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)
                  && dropRule != DropRule.DESTROY && dropRule != DropRule.ALWAYS_KEEP && !keepInv) {
                trinketStacks.add(stack);
                cosmeticStacks.setStackInSlot(i, ItemStack.EMPTY);
              }
              if (dropRule == DropRule.DESTROY) {
                cosmeticStacks.setStackInSlot(i, ItemStack.EMPTY);
              }
            }
          }
        });
      });

      if (hasEternalGuardiansBand) {
        TombstoneBlock.placeTombstone(player, trinketStacks);
        player.getInventory().clearContent();
      } else {
        this.inventory.dropAll();
      }
    }
  }
}
