package dev.larguma.crawlingmysteries.potion;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
  public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION,
      CrawlingMysteries.MOD_ID);

  public static final Holder<Potion> BEER = POTIONS.register("beer",
      () -> new Potion(new MobEffectInstance(ModMobEffects.DRUNK, 600, 0)));
  public static final Holder<Potion> STRONG_BEER = POTIONS.register("strong_beer",
      () -> new Potion(new MobEffectInstance(ModMobEffects.DRUNK, 300, 1)));

  public static void register(IEventBus eventBus) {
    POTIONS.register(eventBus);
  }
}