package dev.larguma.crawlingmysteries.datagen.sound;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.sound.ModSounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class ModJukeboxSongProvider extends DatapackBuiltinEntriesProvider {

  public ModJukeboxSongProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, createBuilder(), java.util.Set.of(CrawlingMysteries.MOD_ID));
  }

  private static RegistrySetBuilder createBuilder() {
    RegistrySetBuilder builder = new RegistrySetBuilder();
    builder.add(Registries.JUKEBOX_SONG, ModJukeboxSongProvider::bootstrap);
    return builder;
  }

  private static void bootstrap(BootstrapContext<JukeboxSong> context) {
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
