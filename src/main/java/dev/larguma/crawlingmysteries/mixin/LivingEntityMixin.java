package dev.larguma.crawlingmysteries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import dev.larguma.crawlingmysteries.spell.SpellHandlerHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

  @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
  private boolean checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> info) {
    if (!((Object) this instanceof ServerPlayer))
      return info.getReturnValueZ();
    ServerPlayer player = (ServerPlayer) (Object) this;
    ItemStack stack = SpellHandlerHelper.getCurioEquipped(player, ModItems.CRYPTIC_EYE.get());

    if (!stack.isEmpty()) {

      boolean enabled = ItemDataHelper.getSpellStage(stack) >= 2;
      boolean onCooldown = SpellCooldownManager.isOnCooldown(player, ModSpells.BE_TOTEM);

      if (enabled && !onCooldown && stack.getItem() instanceof CrypticEyeItem item) {
        item.beTotem(player);
        SpellCooldownManager.setCooldown(player, ModSpells.BE_TOTEM);
        info.setReturnValue(true);
        info.cancel();
        return true;
      }
    }
    return info.getReturnValueZ();
  }
}
