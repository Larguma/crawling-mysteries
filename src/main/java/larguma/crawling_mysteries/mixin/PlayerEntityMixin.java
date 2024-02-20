package larguma.crawling_mysteries.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.custom.TombstoneBlock;
import larguma.crawling_mysteries.item.ModItems;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

  @Shadow
  @Final
  private PlayerInventory inventory;
  private boolean hasEternalGuardiansBand = false;

  protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
    super(type, world);
  }

  @Inject(at = @At("HEAD"), method = "dropInventory")
  private void dropInventory(CallbackInfo info) {
    if (CrawlingMysteries.CONFIG.enableTombstone()) {

      PlayerEntity playerEntity = (PlayerEntity) (Object) this;
      ItemStack eternalGuardiansBandStack = new ItemStack(ModItems.ETERNAL_GUARDIANS_BAND);
      eternalGuardiansBandStack.setCount(1);
      boolean keepInv = playerEntity.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
      List<ItemStack> trinketStacks = new ArrayList<>();

      TrinketsApi.getTrinketComponent(playerEntity).ifPresent(trinkets -> trinkets.forEach((ref, stack) -> {
        if (stack.isEmpty()) {
          return;
        }

        if (Objects.equals(stack.getItem(), ModItems.ETERNAL_GUARDIANS_BAND))
          hasEternalGuardiansBand = true;

        DropRule dropRule = TrinketsApi.getTrinket(stack.getItem()).getDropRule(stack, ref, playerEntity);
        dropRule = TrinketDropCallback.EVENT.invoker().drop(dropRule, stack, ref, playerEntity);
        TrinketInventory trinketInventory = ref.inventory();

        if (dropRule == DropRule.DEFAULT) {
          dropRule = trinketInventory.getSlotType().getDropRule();
        }

        if (dropRule == DropRule.DEFAULT) {
          if (keepInv && playerEntity.getType() == EntityType.PLAYER) {
            dropRule = DropRule.KEEP;
          } else {
            if (EnchantmentHelper.hasVanishingCurse(stack)) {
              dropRule = DropRule.DESTROY;
            } else {
              dropRule = DropRule.DROP;
            }
          }
        }

        switch (dropRule) {
          case DROP:
            trinketStacks.add(stack);
            // Fallthrough
          case DESTROY:
            trinketInventory.setStack(ref.index(), ItemStack.EMPTY);
            break;
          default:
            break;
        }
      }));

      if (hasEternalGuardiansBand) {
        TombstoneBlock.placeTombstone(playerEntity, trinketStacks);
        playerEntity.getInventory().clear();
      } else {
        this.inventory.dropAll();
      }
    }
  }
}