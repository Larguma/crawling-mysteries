package dev.larguma.crawlingmysteries.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.sound.ModSounds;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class MusicHandler {

  private static final int MIN_DELAY = 20 * 300;
  private static final int MAX_DELAY = 20 * 1200;
  private static final Minecraft minecraft = Minecraft.getInstance();
  private static int ticksUntilMusic = 0;
  private static SoundInstance customMusicPlaying;

  private static List<SoundEvent> musicTracks = null;

  private static void initMusicTracks() {
    if (musicTracks == null) {
      musicTracks = new ArrayList<>();

      ModSounds.SOUND_EVENTS.getEntries().stream()
          .filter(entry -> entry.getId().getPath().startsWith("ost_"))
          .forEach(entry -> musicTracks.add(entry.get()));
    }
  }

  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Post event) {

    if (minecraft.level == null || minecraft.isPaused()) {
      return;
    }

    initMusicTracks();

    if (customMusicPlaying != null && !minecraft.getSoundManager().isActive(customMusicPlaying)) {
      customMusicPlaying = null;
      ticksUntilMusic = MIN_DELAY + minecraft.level.random.nextInt(MAX_DELAY - MIN_DELAY);
    }

    boolean vanillaMusicPlaying = minecraft.getMusicManager().isPlayingMusic(minecraft.getSituationalMusic());
    if (!vanillaMusicPlaying && customMusicPlaying == null) {
      if (ticksUntilMusic > 0) {
        ticksUntilMusic--;
      } else {
        if (shouldPlayCustomMusic()) {
          playRandomMusic();
        } else {
          ticksUntilMusic = 200;
        }
      }
    }
  }

  private static void playRandomMusic() {
    if (musicTracks.isEmpty()) {
      return;
    }

    int randomIndex = minecraft.level.random.nextInt(musicTracks.size());
    SoundEvent musicSound = musicTracks.get(randomIndex);

    SoundInstance soundInstance = SimpleSoundInstance.forMusic(musicSound);
    minecraft.getSoundManager().play(soundInstance);
    customMusicPlaying = soundInstance;
  }

  public static void stopPlaying() {
    if (customMusicPlaying != null) {
      Minecraft.getInstance().getSoundManager().stop(customMusicPlaying);
      customMusicPlaying = null;
    }

    ticksUntilMusic += 100;
  }

  private static boolean shouldPlayCustomMusic() {
    if (minecraft.level.random.nextFloat() > 0.3f) {
      return false;
    }
    return true;
  }
}