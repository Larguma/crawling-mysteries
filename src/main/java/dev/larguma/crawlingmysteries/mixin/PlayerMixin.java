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
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import dev.larguma.crawlingmysteries.util.SlottedItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mixin(Player.class)
public abstract class PlayerMixin {

  @Inject(at = @At("HEAD"), method = "dropEquipment")
  private void dropEquipment(CallbackInfo info) {
    if (!Config.SERVER.enableTombstone.get()) {
      return;
    }

    Player player = (Player) (Object) this;
    boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    List<SlottedItemStack> trinketStacks = new ArrayList<>();

    Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
    List<SlotResult> eternalGuardiansBand = curiosInventory
        .map(inv -> inv.findCurios(ModItems.ETERNAL_GUARDIANS_BAND.get())).orElse(List.of());
    boolean hasEternalGuardiansBand = eternalGuardiansBand.stream().anyMatch(slot -> {
      return ItemDataHelper.getAttunement(slot.stack()) >= 1.0f;
    });
    curiosInventory.ifPresent(inv -> collectTrinkets(inv, trinketStacks, keepInventory));

    if (hasEternalGuardiansBand) {
      TombstoneBlock.placeTombstone(player, trinketStacks);
      player.getInventory().clearContent();
    } else {
      for (SlottedItemStack slottedStack : trinketStacks) {
        player.drop(slottedStack.stack(), true, false);
      }
      player.getInventory().dropAll();
    }
  }

  @Unique
  private void collectTrinkets(ICuriosItemHandler curiosInventory, List<SlottedItemStack> trinketStacks,
      boolean keepInventory) {
    curiosInventory.getCurios().forEach((slotId, stacksHandler) -> {
      DropRule dropRule = stacksHandler.getDropRule();
      collectFromHandler(stacksHandler.getStacks(), trinketStacks, dropRule, keepInventory, slotId, false);
      collectFromHandler(stacksHandler.getCosmeticStacks(), trinketStacks, dropRule, keepInventory, slotId, true);
    });
  }

  @Unique
  private void collectFromHandler(IItemHandlerModifiable handler, List<SlottedItemStack> trinketStacks,
      DropRule dropRule, boolean keepInventory, String slotId, boolean isCosmetic) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }

      switch (dropRule) {
        case ALWAYS_DROP -> {
          trinketStacks.add(SlottedItemStack.forCurios(stack.copy(), slotId, i, isCosmetic));
          handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        case DESTROY -> handler.setStackInSlot(i, ItemStack.EMPTY);
        case ALWAYS_KEEP -> {
          /* do nothing */ }
        default -> {
          if (!keepInventory && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            trinketStacks.add(SlottedItemStack.forCurios(stack.copy(), slotId, i, isCosmetic));
            handler.setStackInSlot(i, ItemStack.EMPTY);
          }
        }
      }
    }
  }
}
