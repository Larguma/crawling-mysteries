package dev.larguma.crawlingmysteries.sound;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class ModJukeboxSongs {
  public static void bootstrap(BootstrapContext<JukeboxSong> context) {
    registerJukeboxSong(context, ModSounds.OST_01_KEY, ModSounds.OST_01, 166.0f, 15);
    registerJukeboxSong(context, ModSounds.OST_02_KEY, ModSounds.OST_02, 162.0f, 15);
  }

  public static void registerJukeboxSong(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> songKey,
      Supplier<SoundEvent> soundEvent, float lengthInSeconds, int comparatorOutput) {

    context.register(songKey, soundEvent.get() == null ? null
        : new JukeboxSong(
            context.lookup(Registries.SOUND_EVENT)
                .getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, soundEvent.get().getLocation())),
            Component.translatable(
                "jukebox_song." + songKey.location().getNamespace() + "." + songKey.location().getPath() + ".desc"),
            lengthInSeconds, comparatorOutput));
  }
}
