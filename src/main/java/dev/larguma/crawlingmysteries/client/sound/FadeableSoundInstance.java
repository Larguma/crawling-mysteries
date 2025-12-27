package dev.larguma.crawlingmysteries.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class FadeableSoundInstance extends AbstractTickableSoundInstance {
  private boolean fadingOut = false;
  private int fadeTicks = 0;
  private final int fadeDuration;
  private final float originalVolume;

  public FadeableSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch,
      RandomSource random, int fadeDuration) {
    super(soundEvent, source, random);
    this.volume = volume;
    this.pitch = pitch;
    this.originalVolume = volume;
    this.fadeDuration = fadeDuration;
    this.looping = false;
    this.delay = 0;
    this.relative = true;
    this.attenuation = SoundInstance.Attenuation.NONE;
  }

  public void fadeOut() {
    if (!this.fadingOut) {
      this.fadingOut = true;
      this.fadeTicks = this.fadeDuration;
    }
  }

  @Override
  public void tick() {
    if (this.fadingOut) {
      this.fadeTicks--;
      if (this.fadeTicks <= 0) {
        this.stop();
      } else {
        this.volume = this.originalVolume * ((float) this.fadeTicks / this.fadeDuration);
      }
    }
  }
}