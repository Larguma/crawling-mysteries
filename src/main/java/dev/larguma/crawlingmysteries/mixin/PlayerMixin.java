package dev.larguma.crawlingmysteries.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.larguma.crawlingmysteries.Config;
import dev.larguma.crawlingmysteries.block.custom.TombstoneBlock;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mixin(Player.class)
public abstract class PlayerMixin {

  @Inject(at = @At("HEAD"), method = "dropEquipment")
  private void dropEquipment(CallbackInfo info) {
    if (!Config.ENABLE_TOMBSTONE.get()) {
      return;
    }

    Player player = (Player) (Object) this;
    boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    List<ItemStack> trinketStacks = new ArrayList<>();

    Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
    boolean hasEternalGuardiansBand = curiosInventory.map(inv -> inv.isEquipped(ModItems.ETERNAL_GUARDIANS_BAND.get()))
        .orElse(false);
    curiosInventory.ifPresent(inv -> collectTrinkets(inv, trinketStacks, keepInventory));

    if (hasEternalGuardiansBand) {
      TombstoneBlock.placeTombstone(player, trinketStacks);
      player.getInventory().clearContent();
    } else {
      for (ItemStack stack : trinketStacks) {
        player.drop(stack, true, false);
      }
      player.getInventory().dropAll();
    }
  }

  @Unique
  private void collectTrinkets(ICuriosItemHandler curiosInventory, List<ItemStack> trinketStacks,
      boolean keepInventory) {
    curiosInventory.getCurios().forEach((slotId, stacksHandler) -> {
      DropRule dropRule = stacksHandler.getDropRule();
      collectFromHandler(stacksHandler.getStacks(), trinketStacks, dropRule, keepInventory);
      collectFromHandler(stacksHandler.getCosmeticStacks(), trinketStacks, dropRule, keepInventory);
    });
  }

  @Unique
  private void collectFromHandler(IItemHandlerModifiable handler, List<ItemStack> trinketStacks,
      DropRule dropRule, boolean keepInventory) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }

      switch (dropRule) {
        case ALWAYS_DROP -> {
          trinketStacks.add(stack);
          handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        case DESTROY -> handler.setStackInSlot(i, ItemStack.EMPTY);
        case ALWAYS_KEEP -> {
          /* do nothing */ }
        default -> {
          if (!keepInventory && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            trinketStacks.add(stack);
            handler.setStackInSlot(i, ItemStack.EMPTY);
          }
        }
      }
    }
  }
}
