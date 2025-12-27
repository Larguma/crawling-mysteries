package dev.larguma.crawlingmysteries.sound;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
  public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT,
      CrawlingMysteries.MOD_ID);

  public static final ResourceKey<JukeboxSong> OST_01_KEY = createSong("ost_01");
  public static final Supplier<SoundEvent> OST_01 = registerSoundEvent("ost_01");
  public static final ResourceKey<JukeboxSong> OST_02_KEY = createSong("ost_02");
  public static final Supplier<SoundEvent> OST_02 = registerSoundEvent("ost_02");

  public static final Supplier<SoundEvent> BEER_POUR = registerSoundEvent("beer_pour");
  public static final Supplier<SoundEvent> BEER_DRINK = registerSoundEvent("beer_drink");

  private static ResourceKey<JukeboxSong> createSong(String name) {
    return ResourceKey.create(Registries.JUKEBOX_SONG,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name));
  }

  private static Supplier<SoundEvent> registerSoundEvent(String name) {
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name);
    return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
  }

  public static void register(IEventBus eventBus) {
    SOUND_EVENTS.register(eventBus);
  }
}
